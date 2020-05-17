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
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.*;
import org.elasticsearch.search.builder.*;
import org.elasticsearch.search.fetch.subphase.*;

public class ElasticService implements Closeable {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(ElasticService.class);
	/**
	 * filedate
	 */
	private static final String FILEDATE = "filedate";
	/**
	 * data
	 */
	private static final String DATA = "data";
	/**
	 * date
	 */
	private static final String DATE = "date";
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
		/*
		 * connect
		 */
		client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));
	}

	/**
	 * build the JSON payload for elastic
	 *
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> buildFileMap(File file) throws Exception {
		final String fileData = ServiceFactory.getInstance().getFileReaderService().readFile(file);
		final Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put(DATA, fileData);
		jsonMap.put(DATE, new Date());
		jsonMap.put(FILEDATE, file.lastModified());
		return jsonMap;
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
	public void delete(String filename) throws IOException {
		final DeleteRequest request = new DeleteRequest(indexName, filename);
		final DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
		logger.info(deleteResponse.toString());
	}

	/**
	 * check if file exists
	 *
	 * @return exists
	 * @throws IOException
	 */
	public boolean exists(File file) throws IOException {
		final GetRequest getRequest = new GetRequest(indexName, file.getAbsolutePath());
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
	public long filedate(File file) throws IOException {
		final GetRequest getRequest = new GetRequest(indexName, file.getAbsolutePath());
		final String[] includes = new String[] { FILEDATE };
		final String[] excludes = new String[] { DATA };
		getRequest.fetchSourceContext(new FetchSourceContext(true, includes, excludes));
		final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		final Object o = getResponse.getSource().get(FILEDATE);
		if (null != o) {
			return (Long) o;
		}
		return 0;
	}

	/**
	 * iterate all files in the elastic engine
	 *
	 * @throws IOException
	 */
	public void iterateAll(FileIterator fileIterator) throws IOException {
		final SearchRequest searchRequest = new SearchRequest(indexName);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		final String[] includeFields = new String[] { FILEDATE };
		final String[] excludeFields = new String[] { DATA };
		searchSourceBuilder.fetchSource(includeFields, excludeFields);
		searchRequest.source(searchSourceBuilder);
		final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		logger.info(searchResponse.toString());
		for (final SearchHit searchHit : searchResponse.getHits()) {
			fileIterator.file(searchHit.getId(), (Long) searchHit.getSourceAsMap().get(FILEDATE));
		}
	}

	/**
	 * search
	 *
	 * @throws IOException
	 */
	public List<String> search(String searchTerm) throws IOException {
		final List<String> ret = new ArrayList<String>();
		final SearchRequest searchRequest = new SearchRequest(indexName);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		final MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(DATA, searchTerm).fuzziness(Fuzziness.AUTO);
		searchSourceBuilder.query(matchQueryBuilder);
		final String[] includeFields = new String[] { FILEDATE };
		final String[] excludeFields = new String[] { DATA };
		searchSourceBuilder.fetchSource(includeFields, excludeFields);
		searchRequest.source(searchSourceBuilder);
		final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		logger.info(searchResponse.toString());
		for (final SearchHit searchHit : searchResponse.getHits()) {
			ret.add(searchHit.getId());
		}
		return ret;
	}

	/**
	 * update file data
	 *
	 * @throws Exception
	 */
	public void update(File file) throws Exception {
		/*
		 * file map
		 */
		final Map<String, Object> jsonMap = buildFileMap(file);
		/*
		 * update
		 */
		final UpdateRequest updateRequest = new UpdateRequest(indexName, file.getAbsolutePath());
		updateRequest.doc(jsonMap);
		final UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
		logger.info(updateResponse.toString());
	}

	/**
	 * write file data
	 *
	 * @throws Exception
	 */
	public void write(File file) throws Exception {
		/*
		 * file map
		 */
		final Map<String, Object> jsonMap = buildFileMap(file);
		/*
		 * index
		 */
		final IndexRequest request = new IndexRequest(indexName);
		request.id(file.getAbsolutePath());
		request.source(jsonMap);
		final IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
		logger.info(indexResponse.toString());
	}
}
