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
package com.khubla.ksearch;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.*;

public class Configuration {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(Configuration.class);
	/**
	 * singleton
	 */
	private static Configuration instance;
	/**
	 * filename
	 */
	public static String propertiesFile = "kSearch.properties";

	/**
	 * singleton getter
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static Configuration getConfiguration() {
		if (null == instance) {
			instance = new Configuration();
			try {
				instance.load(propertiesFile);
			} catch (final Exception e) {
				logger.error(e);
			}
		}
		return instance;
	}

	/**
	 * dir to index
	 */
	private String[] dirs;
	/**
	 * extensions
	 */
	private String[] extensions;
	/**
	 * index threads
	 */
	private int threads;
	/**
	 * elastic host
	 */
	private String elasticHost;
	/**
	 * elastic port
	 */
	private int elasticPort;
	/**
	 * elastic index
	 */
	private String elasticIndex;
	/**
	 * http port
	 */
	private int httpPort;
	/**
	 * refresh time minutes
	 */
	private int refresh_minutes;
	/**
	 * orgname
	 */
	private String orgname;
	/**
	 * page size
	 */
	private int page_size;

	/**
	 * ctor
	 */
	private Configuration() {
	}

	public String[] getDirs() {
		return dirs;
	}

	public String getElasticHost() {
		return elasticHost;
	}

	public String getElasticIndex() {
		return elasticIndex;
	}

	public int getElasticPort() {
		return elasticPort;
	}

	public String[] getExtensions() {
		return extensions;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public String getOrgname() {
		return orgname;
	}

	public int getPage_size() {
		return page_size;
	}

	public int getRefresh_minutes() {
		return refresh_minutes;
	}

	public int getThreads() {
		return threads;
	}

	private void load(String propertiesFile) throws Exception {
		try {
			/*
			 * properties
			 */
			final Properties properties = new Properties();
			properties.load(new FileInputStream(propertiesFile));
			threads = Integer.parseInt(properties.getProperty("threads"));
			dirs = split(properties.getProperty("dirs"));
			extensions = split(properties.getProperty("extensions"));
			elasticPort = Integer.parseInt(properties.getProperty("elastic.port"));
			elasticHost = properties.getProperty("elastic.host").trim();
			elasticIndex = properties.getProperty("elastic.index").trim();
			httpPort = Integer.parseInt(properties.getProperty("http.port"));
			refresh_minutes = Integer.parseInt(properties.getProperty("refresh_minutes"));
			orgname = properties.getProperty("orgname").trim();
			page_size = Integer.parseInt(properties.getProperty("page_size"));
		} catch (final Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void setDirs(String[] dirs) {
		this.dirs = dirs;
	}

	public void setElasticHost(String elasticHost) {
		this.elasticHost = elasticHost;
	}

	public void setElasticIndex(String elasticIndex) {
		this.elasticIndex = elasticIndex;
	}

	public void setElasticPort(int elasticPort) {
		this.elasticPort = elasticPort;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

	public void setRefresh_minutes(int refresh_minutes) {
		this.refresh_minutes = refresh_minutes;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	private String[] split(String list) {
		final String[] str = list.split(",");
		final String[] ret = new String[str.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = str[i].trim();
		}
		return ret;
	}
}
