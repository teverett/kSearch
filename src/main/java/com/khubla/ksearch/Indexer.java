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
import java.util.concurrent.*;

import org.apache.commons.io.*;
import org.slf4j.*;

import com.khubla.ksearch.progress.*;

public class Indexer {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Indexer.class);
	/**
	 * executor
	 */
	private final ExecutorService executor;
	/**
	 * config
	 */
	private final Configuration configuration;

	public Indexer(Configuration configuration) {
		this.configuration = configuration;
		executor = Executors.newFixedThreadPool(configuration.getThreads());
	}

	public void index(ProgressCallback progressCallback) throws Exception {
		try {
			final File dir = new File(configuration.getDir());
			if (dir.exists()) {
				/*
				 * find files
				 */
				if (null != progressCallback) {
					progressCallback.status("Finding files in " + dir.getPath());
				}
				final List<File> files = listFiles(dir);
				if (null != progressCallback) {
					progressCallback.status("Found '" + files.size() + "'  files in " + dir.getPath());
				}
				/*
				 * walk the files
				 */
				for (final File file : files) {
					/*
					 * get or create file
					 */
					System.out.println(file.getName());
				}
				/*
				 * wait
				 */
				executor.shutdown();
				executor.awaitTermination(30, TimeUnit.DAYS);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private List<File> listFiles(File dir) throws Exception {
		final List<File> ret = new ArrayList<File>();
		if (dir.exists()) {
			if (dir.isDirectory()) {
				final Collection<File> files = FileUtils.listFiles(dir, null, true);
				if (null != files) {
					ret.addAll(files);
				}
			} else {
				ret.add(dir);
			}
		}
		return ret;
	}
}
