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
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import org.apache.http.*;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.action.update.*;
import org.elasticsearch.client.*;
import org.elasticsearch.search.fetch.subphase.*;
import org.slf4j.*;

public class ElasticService implements Closeable {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ElasticService.class);
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
	 * @throws IOException
	 */
	private Map<String, Object> buildFileMap(File file) throws IOException {
		final String fileData = readFile(file);
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
	 * read file as text
	 *
	 * @param file
	 * @return file text
	 * @throws IOException
	 */
	private String readFile(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
	}

	/**
	 * update file data
	 *
	 * @throws IOException
	 */
	public void update(File file) throws IOException {
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
	 * @throws IOException
	 */
	public void write(File file) throws IOException {
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
