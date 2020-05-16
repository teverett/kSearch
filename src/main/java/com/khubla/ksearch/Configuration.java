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

	private String dir;
	private int threads;

	/**
	 * ctor
	 */
	private Configuration() {
	}

	public String getDir() {
		return dir;
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
			dir = properties.getProperty("dir");
		} catch (final Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
