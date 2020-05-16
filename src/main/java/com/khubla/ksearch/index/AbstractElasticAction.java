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

import org.apache.http.*;
import org.elasticsearch.client.*;
import org.slf4j.*;

public abstract class AbstractElasticAction implements Runnable, Closeable {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractElasticAction.class);
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
	public AbstractElasticAction() throws Exception {
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
}
