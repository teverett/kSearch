/*
 * Copyright 2015-2020, Tom Everett
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.khubla.ksearch.service;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.logging.log4j.*;
import org.elasticsearch.action.delete.*;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.*;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.*;
import org.elasticsearch.search.builder.*;
import org.elasticsearch.search.fetch.subphase.*;
import org.elasticsearch.search.sort.*;

import com.google.gson.*;
import com.khubla.ksearch.domain.*;

public class ElasticService {
	/**
	 * a factory for a single static REST client
	 * <p>
	 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_changing_the_client_8217_s_initialization_code.html
	 * </p>
	 * <p>
	 * "The RestHighLevelClient is thread-safe. It is typically instantiated by the application at startup time or when the first request is executed."
	 * </p>
	 *
	 * @author tom
	 */
	private static class RestHighLevelClientFactory {
		/**
		 * singleton
		 */
		private static RestHighLevelClientFactory instance = null;

		private static RestHighLevelClientFactory getInstance() {
			if (null == instance) {
				instance = new RestHighLevelClientFactory();
			}
			return instance;
		}

		/**
		 * host
		 */
		protected final String elasticHost;
		/**
		 * port
		 */
		protected final int elasticPort;
		/**
		 * client
		 */
		private final RestHighLevelClient client;

		private RestHighLevelClientFactory() {
			elasticHost = com.khubla.ksearch.Configuration.getConfiguration().getElasticHost();
			elasticPort = com.khubla.ksearch.Configuration.getConfiguration().getElasticPort();
			/*
			 * connect
			 */
			client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));
		}

		public RestHighLevelClient getRestHighLevelClient() {
			return client;
		}
	}

	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(ElasticService.class);
	/**
	 * indexName
	 */
	protected final String indexName;
	/**
	 * GSON
	 */
	private final Gson gson = new Gson();

	/**
	 * ctor
	 *
	 * @param file
	 * @throws Exception
	 */
	public ElasticService() throws Exception {
		/*
		 * data
		 */
		indexName = com.khubla.ksearch.Configuration.getConfiguration().getElasticIndex();
	}

	/**
	 * delete a file from the elastic index
	 *
	 * @param filename
	 * @throws IOException
	 */
	public void delete(String fileAbsolutePath) throws IOException {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			final DeleteRequest request = new DeleteRequest(indexName, fileAbsolutePath);
			final DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
			logger.info(deleteResponse.toString());
		} catch (final IOException e) {
			logger.error("Error in  delete " + fileAbsolutePath, e);
			throw e;
		}
	}

	/**
	 * delete all file data
	 *
	 * @throws Exception
	 */
	public void deleteAll() throws Exception {
		try {
			/*
			 * pages of 100
			 */
			final int pagesize = 100;
			int idx = 0;
			List<FileDataSource> filedataSources = null;
			do {
				filedataSources = getAll(idx, pagesize);
				idx += filedataSources.size();
				for (final FileDataSource fileDataSource : filedataSources) {
					delete(fileDataSource.getFile_absolute_path());
				}
			} while (filedataSources.size() == pagesize);
		} catch (final IOException e) {
			logger.error("Error in  deleteAll", e);
			throw e;
		}
	}

	/**
	 * check if file exists
	 *
	 * @return exists
	 * @throws IOException
	 */
	public boolean exists(String fileAbsolutePath) throws IOException {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			final GetRequest getRequest = new GetRequest(indexName, fileAbsolutePath);
			getRequest.fetchSourceContext(new FetchSourceContext(false));
			getRequest.storedFields("_none_");
			return client.exists(getRequest, RequestOptions.DEFAULT);
		} catch (final IOException e) {
			logger.error("Error in  exists " + fileAbsolutePath, e);
			throw e;
		}
	}

	/**
	 * get a file
	 *
	 * @throws IOException
	 */
	public FileDataSource get(String fileAbsolutePath) throws IOException {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			final GetRequest getRequest = new GetRequest(indexName, fileAbsolutePath);
			getRequest.fetchSourceContext(new FetchSourceContext(true));
			final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			final FileDataSource fileDataSource = gson.fromJson(getResponse.getSourceAsString(), FileDataSource.class);
			return fileDataSource;
		} catch (final IOException e) {
			logger.error("Error in  get " + fileAbsolutePath, e);
			throw e;
		}
	}

	/**
	 * get all files in the elastic engine
	 *
	 * @throws IOException
	 */
	public List<FileDataSource> getAll(int from, int size) throws IOException {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			final List<FileDataSource> ret = new ArrayList<FileDataSource>();
			final SearchRequest searchRequest = new SearchRequest(indexName);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.from(from);
			searchSourceBuilder.size(size);
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			final String[] excludeFields = new String[] { FileDataSource.DATA };
			searchSourceBuilder.fetchSource(null, excludeFields);
			searchRequest.source(searchSourceBuilder);
			final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			logger.info(searchResponse.toString());
			for (final SearchHit searchHit : searchResponse.getHits()) {
				final String json = searchHit.toString();
				final FileData fileData = gson.fromJson(json, FileData.class);
				ret.add(fileData.get_source());
			}
			return ret;
		} catch (final IOException e) {
			logger.error("Error in getAll", e);
			throw e;
		}
	}

	/**
	 * get a file's metadata
	 *
	 * @throws IOException
	 */
	public FileDataSource getMetadata(String fileAbsolutePath) throws IOException {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			final GetRequest getRequest = new GetRequest(indexName, fileAbsolutePath);
			final String[] excludes = new String[] { FileDataSource.DATA };
			getRequest.fetchSourceContext(new FetchSourceContext(true, null, excludes));
			final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			final FileDataSource fileDataSource = gson.fromJson(getResponse.getSourceAsString(), FileDataSource.class);
			return fileDataSource;
		} catch (final IOException e) {
			logger.error("Error in  getMetadata " + fileAbsolutePath, e);
			throw e;
		}
	}

	/**
	 * search
	 *
	 * @throws IOException
	 */
	public List<FileDataSource> search(String searchTerm, int from, int size) throws IOException {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			final List<FileDataSource> ret = new ArrayList<FileDataSource>();
			final SearchRequest searchRequest = new SearchRequest(indexName);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.from(from);
			searchSourceBuilder.size(size);
			final MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FileDataSource.DATA, searchTerm);
			searchSourceBuilder.query(matchQueryBuilder);
			final String[] excludeFields = new String[] { FileDataSource.DATA };
			searchSourceBuilder.fetchSource(null, excludeFields);
			searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
			searchRequest.source(searchSourceBuilder);
			final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			logger.info(searchResponse.toString());
			for (final SearchHit searchHit : searchResponse.getHits()) {
				final String json = searchHit.toString();
				final FileData fileData = gson.fromJson(json, FileData.class);
				ret.add(fileData.get_source());
			}
			return ret;
		} catch (final IOException e) {
			logger.error("Error searching " + searchTerm, e);
			throw e;
		}
	}

	/**
	 * update file data
	 *
	 * @throws Exception
	 */
	public void update(FileDataSource fileDataSource) throws Exception {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			/*
			 * file data
			 */
			final String json = gson.toJson(fileDataSource);
			/*
			 * update
			 */
			final UpdateRequest updateRequest = new UpdateRequest(indexName, fileDataSource.getFile_absolute_path());
			updateRequest.doc(json, XContentType.JSON);
			final UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
			logger.info(updateResponse.toString());
		} catch (final IOException e) {
			logger.error("Error updating " + fileDataSource.getFile_absolute_path(), e);
			throw e;
		}
	}

	/**
	 * write file data
	 *
	 * @throws Exception
	 */
	public void write(FileDataSource fileDataSource) throws Exception {
		try {
			final RestHighLevelClient client = RestHighLevelClientFactory.getInstance().getRestHighLevelClient();
			/*
			 * file data
			 */
			final String json = gson.toJson(fileDataSource);
			/*
			 * index
			 */
			final IndexRequest request = new IndexRequest(indexName);
			request.id(fileDataSource.getFile_absolute_path());
			request.source(json, XContentType.JSON);
			final IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
			logger.info(indexResponse.toString());
		} catch (final IOException e) {
			logger.error("Error writing " + fileDataSource.getFile_absolute_path(), e);
			throw e;
		}
	}
}
