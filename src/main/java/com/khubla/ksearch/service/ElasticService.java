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
import org.elasticsearch.common.unit.*;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.*;
import org.elasticsearch.search.builder.*;
import org.elasticsearch.search.fetch.subphase.*;

import com.google.gson.*;
import com.khubla.ksearch.domain.*;

public class ElasticService implements Closeable {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(ElasticService.class);
	/**
	 * client
	 */
	protected final RestHighLevelClient client;
	/**
	 * host
	 */
	protected final String elasticHost;
	/**
	 * port
	 */
	protected final int elasticPort;
	/**
	 * indexName
	 */
	protected final String indexName;
	/**
	 * max search results
	 */
	private final long max_search_results;
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
		elasticHost = com.khubla.ksearch.Configuration.getConfiguration().getElasticHost();
		elasticPort = com.khubla.ksearch.Configuration.getConfiguration().getElasticPort();
		max_search_results = com.khubla.ksearch.Configuration.getConfiguration().getMax_search_results();
		/*
		 * connect
		 */
		client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));
	}

	@Override
	public void close() {
		try {
			if (null != client) {
				client.close();
			}
		} catch (final IOException e) {
			logger.error("Exception closeing ", e);
		}
	}

	/**
	 * delete a file from the elastic index
	 *
	 * @param filename
	 * @throws IOException
	 */
	public void delete(String fileAbsolutePath) throws IOException {
		final DeleteRequest request = new DeleteRequest(indexName, fileAbsolutePath);
		final DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
		logger.info(deleteResponse.toString());
	}

	/**
	 * check if file exists
	 *
	 * @return exists
	 * @throws IOException
	 */
	public boolean exists(String fileAbsolutePath) throws IOException {
		final GetRequest getRequest = new GetRequest(indexName, fileAbsolutePath);
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		return client.exists(getRequest, RequestOptions.DEFAULT);
	}

	/**
	 * get the file date
	 *
	 * @return file date
	 * @throws IOException
	 */
	public long filedate(String fileAbsolutePath) throws IOException {
		final GetRequest getRequest = new GetRequest(indexName, fileAbsolutePath);
		final String[] includes = new String[] { FileDataSource.MODIFIED_DATE };
		final String[] excludes = new String[] { FileDataSource.DATA };
		getRequest.fetchSourceContext(new FetchSourceContext(true, includes, excludes));
		final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		final Object o = getResponse.getSource().get(FileDataSource.MODIFIED_DATE);
		if (null != o) {
			return (Long) o;
		}
		return 0;
	}

	/**
	 * get all files in the elastic engine
	 *
	 * @throws IOException
	 */
	public List<FileDataSource> getAll() throws IOException {
		final List<FileDataSource> ret = new ArrayList<FileDataSource>();
		final SearchRequest searchRequest = new SearchRequest(indexName);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size((int) max_search_results);
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
	}

	/**
	 * iterate all files in the elastic engine
	 *
	 * @throws IOException
	 */
	public void iterateAll(FileIterator fileIterator) throws IOException {
		final SearchRequest searchRequest = new SearchRequest(indexName);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size((int) max_search_results);
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		final String[] excludeFields = new String[] { FileDataSource.DATA };
		searchSourceBuilder.fetchSource(null, excludeFields);
		searchRequest.source(searchSourceBuilder);
		final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		logger.info(searchResponse.toString());
		for (final SearchHit searchHit : searchResponse.getHits()) {
			final String json = searchHit.toString();
			final FileData fileData = gson.fromJson(json, FileData.class);
			fileIterator.file(fileData.get_source());
		}
	}

	/**
	 * search
	 *
	 * @throws IOException
	 */
	public List<FileDataSource> search(String searchTerm) throws IOException {
		final List<FileDataSource> ret = new ArrayList<FileDataSource>();
		final SearchRequest searchRequest = new SearchRequest(indexName);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		final MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FileDataSource.DATA, searchTerm).fuzziness(Fuzziness.AUTO);
		searchSourceBuilder.query(matchQueryBuilder);
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
	}

	/**
	 * update file data
	 *
	 * @throws Exception
	 */
	public void update(FileDataSource fileDataSource) throws Exception {
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
	}

	/**
	 * write file data
	 *
	 * @throws Exception
	 */
	public void write(FileDataSource fileDataSource) throws Exception {
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
	}
}
