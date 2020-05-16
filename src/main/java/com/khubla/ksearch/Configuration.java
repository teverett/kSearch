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

import org.slf4j.*;

public class Configuration {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
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
	public static Configuration getConfiguration() throws Exception {
		if (null == instance) {
			instance = new Configuration();
			instance.load(propertiesFile);
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
	 * elastic ip
	 */
	private String elasticIp;
	/**
	 * elastic port
	 */
	private int elasticPort;
	/**
	 * elastic index
	 */
	private String elasticIndex;

	/**
	 * ctor
	 */
	private Configuration() {
	}

	public String[] getDirs() {
		return dirs;
	}

	public String getElasticIndex() {
		return elasticIndex;
	}

	public String getElasticIp() {
		return elasticIp;
	}

	public int getElasticPort() {
		return elasticPort;
	}

	public String[] getExtensions() {
		return extensions;
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
			elasticIp = properties.getProperty("elastic.ip");
			elasticIndex = properties.getProperty("elastic.index");
		} catch (final Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void setDirs(String[] dirs) {
		this.dirs = dirs;
	}

	public void setElasticIndex(String elasticIndex) {
		this.elasticIndex = elasticIndex;
	}

	public void setElasticIp(String elasticIp) {
		this.elasticIp = elasticIp;
	}

	public void setElasticPort(int elasticPort) {
		this.elasticPort = elasticPort;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
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
