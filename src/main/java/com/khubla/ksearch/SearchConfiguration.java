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

import java.util.*;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.*;
import org.apache.commons.configuration2.builder.fluent.*;
import org.apache.commons.configuration2.convert.*;
import org.apache.logging.log4j.*;

public class SearchConfiguration {
	/**
	 * a specific index
	 *
	 * @author tom
	 */
	public static class SearchIndex {
		/**
		 * dir to index
		 */
		private String[] dirs;
		/**
		 * extensions
		 */
		private String[] extensions;
		/**
		 * elastic index
		 */
		private String elasticIndexName;
		/**
		 * SearchIndex name
		 */
		private String name;
		/**
		 * refresh time minutes
		 */
		private int refresh_minutes;

		public String[] getDirs() {
			return dirs;
		}

		public String getElasticIndexName() {
			return elasticIndexName;
		}

		public String[] getExtensions() {
			return extensions;
		}

		public String getName() {
			return name;
		}

		public int getRefresh_minutes() {
			return refresh_minutes;
		}

		public void setDirs(String[] dirs) {
			this.dirs = dirs;
		}

		public void setElasticIndexName(String elasticIndexName) {
			this.elasticIndexName = elasticIndexName;
		}

		public void setExtensions(String[] extensions) {
			this.extensions = extensions;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setRefresh_minutes(int refresh_minutes) {
			this.refresh_minutes = refresh_minutes;
		}
	}

	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(SearchConfiguration.class);
	/**
	 * singleton
	 */
	private static SearchConfiguration instance;
	/**
	 * filename
	 */
	public static String propertiesFile = "kSearch.properties";

	/**
	 * singleton getter
	 */
	public static SearchConfiguration getInstance() {
		if (null == instance) {
			instance = new SearchConfiguration();
			try {
				instance.load(propertiesFile);
			} catch (final Exception e) {
				logger.error(e);
			}
		}
		return instance;
	}

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
	 * http port
	 */
	private int httpPort;
	/**
	 * orgname
	 */
	private String orgname;
	/**
	 * page size
	 */
	private int page_size;
	private final HashMap<String, SearchIndex> indices = new HashMap<String, SearchIndex>();

	/**
	 * ctor
	 */
	private SearchConfiguration() {
	}

	public String getElasticHost() {
		return elasticHost;
	}

	public int getElasticPort() {
		return elasticPort;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public List<String> getIndexNames() {
		final List<String> ret = new ArrayList<String>();
		for (final String s : indices.keySet()) {
			ret.add(s);
		}
		return ret;
	}

	public HashMap<String, SearchIndex> getIndices() {
		return indices;
	}

	public String getOrgname() {
		return orgname;
	}

	public int getPage_size() {
		return page_size;
	}

	public int getThreads() {
		return threads;
	}

	private void load(String propertiesFile) throws Exception {
		try {
			final Parameters params = new Parameters();
			final FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
					.configure(params.properties().setFileName(propertiesFile).setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
			/*
			 * get config
			 */
			final Configuration configuration = builder.getConfiguration();
			/*
			 * indexes
			 */
			final List<String> indices = configuration.getList(String.class, "indices");
			for (final String name : indices) {
				final SearchIndex searchIndex = new SearchIndex();
				searchIndex.setName(name);
				searchIndex.setDirs(configuration.getStringArray("indices." + name + ".dirs"));
				searchIndex.setElasticIndexName(configuration.getString("indices." + name + ".index"));
				searchIndex.setExtensions(configuration.getStringArray("indices." + name + ".extensions"));
				searchIndex.setRefresh_minutes(configuration.get(Integer.class, "indices." + name + ".refresh_minutes"));
				this.indices.put(name, searchIndex);
			}
			/*
			 * parameters
			 */
			threads = configuration.get(Integer.class, "threads");
			elasticPort = configuration.get(Integer.class, "elastic.port");
			// refresh_minutes = configuration.get(Integer.class, "refresh_minutes");
			httpPort = configuration.get(Integer.class, "http.port");
			page_size = configuration.get(Integer.class, "page_size");
			orgname = configuration.get(String.class, "orgname").trim();
			elasticHost = configuration.get(String.class, "elastic.host").trim();
		} catch (final Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void setElasticHost(String elasticHost) {
		this.elasticHost = elasticHost;
	}

	public void setElasticPort(int elasticPort) {
		this.elasticPort = elasticPort;
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

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
