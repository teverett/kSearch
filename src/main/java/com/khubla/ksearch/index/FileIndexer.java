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
package com.khubla.ksearch.index;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import org.apache.http.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.client.*;
import org.slf4j.*;

public class FileIndexer implements Runnable {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileIndexer.class);
	/**
	 * File
	 */
	private final File file;

	public FileIndexer(File file) {
		this.file = file;
	}

	private String readFile(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
	}

	@Override
	public void run() {
		RestHighLevelClient client = null;
		try {
			logger.info("Indexing: " + file.getAbsolutePath());
			/*
			 * connect
			 */
			final String elasticHost = com.khubla.ksearch.Configuration.getConfiguration().getElasticHost();
			final int elasticPort = com.khubla.ksearch.Configuration.getConfiguration().getElasticPort();
			final String indexName = com.khubla.ksearch.Configuration.getConfiguration().getElasticIndex();
			client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));
			/*
			 * file string
			 */
			final String fileData = readFile(file);
			final Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("data", fileData);
			jsonMap.put("date", new Date());
			/*
			 * index
			 */
			final IndexRequest request = new IndexRequest(indexName);
			request.id(file.getAbsolutePath());
			request.source(jsonMap);
			final IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
			logger.info(indexResponse.toString());
		} catch (final Exception e) {
			logger.error("Exception indexing '" + file.getAbsolutePath() + "'", e);
		} finally {
			try {
				if (null != client) {
					client.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
