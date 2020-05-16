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
package com.khubla.ksearch.indexer.action.impl;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.action.update.*;
import org.elasticsearch.client.*;
import org.elasticsearch.search.fetch.subphase.*;
import org.slf4j.*;

public class FileIndexerAction extends AbstractElasticAction {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileIndexerAction.class);
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
	 * File
	 */
	private final File file;

	/**
	 * ctor
	 *
	 * @param file
	 * @throws Exception
	 */
	public FileIndexerAction(File file) throws Exception {
		super();
		this.file = file;
	}

	/**
	 * build the JSON payload for elastic
	 *
	 * @return
	 * @throws IOException
	 */
	private Map<String, Object> buildFileMap() throws IOException {
		final String fileData = readFile(file);
		final Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put(DATA, fileData);
		jsonMap.put(DATE, new Date());
		jsonMap.put(FILEDATE, file.lastModified());
		return jsonMap;
	}

	@Override
	public void doAction() {
		try {
			logger.info("Indexing: " + file.getAbsolutePath());
			/*
			 * exists?
			 */
			if (exists()) {
				/*
				 * needs update?
				 */
				final long filedate = filedate();
				if (filedate < file.lastModified()) {
					logger.info("Updating: " + file.getAbsolutePath());
					update();
				} else {
					logger.info("Skipped: " + file.getAbsolutePath());
				}
			} else {
				logger.info("Writing: " + file.getAbsolutePath());
				write();
			}
		} catch (final Exception e) {
			logger.error("Exception indexing '" + file.getAbsolutePath() + "'", e);
		}
	}

	/**
	 * check if file exists
	 *
	 * @return exists
	 * @throws IOException
	 */
	private boolean exists() throws IOException {
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
	private long filedate() throws IOException {
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
	private void update() throws IOException {
		/*
		 * file map
		 */
		final Map<String, Object> jsonMap = buildFileMap();
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
	private void write() throws IOException {
		/*
		 * file map
		 */
		final Map<String, Object> jsonMap = buildFileMap();
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
