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
package com.khubla.ksearch.indexer;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.*;
import org.apache.logging.log4j.*;

import com.khubla.ksearch.*;
import com.khubla.ksearch.SearchConfiguration.*;
import com.khubla.ksearch.indexer.action.impl.*;
import com.khubla.ksearch.progress.*;

/**
 * index and clean a specific SearchIndex
 *
 * @author tom
 */
public class Indexer implements Runnable {
	/**
	 * custom IOFileFilter
	 *
	 * @author tom
	 */
	private static class IndexerFilter implements IOFileFilter {
		/**
		 * logger
		 */
		private static final Logger logger = LogManager.getLogger(IndexerFilter.class);
		/**
		 * search index
		 */
		private final SearchIndex searchIndex;

		public IndexerFilter(SearchIndex searchIndex) {
			super();
			this.searchIndex = searchIndex;
		}

		@Override
		public boolean accept(File file) {
			try {
				if (false == file.isDirectory()) {
					final String[] extensions = searchIndex.getExtensions();
					final String extension = FilenameUtils.getExtension(file.getName());
					if (null != extension) {
						for (final String e : extensions) {
							if (e.toLowerCase().compareTo(extension.toLowerCase()) == 0) {
								return true;
							}
						}
					}
					return false;
				} else {
					return true;
				}
			} catch (final Exception e) {
				logger.error(e.getMessage(), e);
				return false;
			}
		}

		/**
		 * accept all dirs
		 */
		@Override
		public boolean accept(File dir, String name) {
			System.out.println("Dir " + dir.getAbsolutePath());
			return true;
		}
	}

	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(Indexer.class);
	/**
	 * callback
	 */
	private final ProgressCallback progressCallback;
	/**
	 * search index
	 */
	private final SearchIndex searchIndex;

	public Indexer(SearchIndex searchIndex, ProgressCallback progressCallback) throws Exception {
		this.progressCallback = progressCallback;
		this.searchIndex = searchIndex;
	}

	/**
	 * get list of files in directory
	 *
	 * @param dir
	 * @return list of files
	 * @throws Exception
	 */
	private List<String> listFiles(File dir) throws Exception {
		final List<String> ret = new ArrayList<String>();
		if (dir.exists()) {
			if (dir.isDirectory()) {
				final Collection<File> files = FileUtils.listFiles(dir, new IndexerFilter(searchIndex), new IndexerFilter(searchIndex));
				if (null != files) {
					for (final File f : files) {
						ret.add(f.getAbsolutePath());
					}
				}
			} else {
				ret.add(dir.getAbsolutePath());
			}
		}
		return ret;
	}

	@Override
	public void run() {
		try {
			logger.info("Beginning Indexing");
			final ExecutorService executor = Executors.newFixedThreadPool(SearchConfiguration.getInstance().getFile_threads());
			/*
			 * start the cleaner
			 */
			executor.submit(new IndexCleanerAction(searchIndex));
			/*
			 * walk the files
			 */
			final String[] dirNames = searchIndex.getDirs();
			for (final String dirName : dirNames) {
				final File dir = new File(dirName);
				if (dir.exists()) {
					/*
					 * find files
					 */
					if (null != progressCallback) {
						progressCallback.status("Finding files in " + dir.getPath());
					}
					final List<String> files = listFiles(dir);
					if (null != progressCallback) {
						progressCallback.status("Found '" + files.size() + "'  files in " + dir.getPath());
					}
					/*
					 * walk the files
					 */
					for (final String filePath : files) {
						final FileIndexerAction fileIndexer = new FileIndexerAction(searchIndex, filePath);
						executor.submit(fileIndexer);
					}
					/*
					 * wait
					 */
					executor.shutdown();
					executor.awaitTermination(30, TimeUnit.HOURS);
				} else {
					if (null != progressCallback) {
						progressCallback.status("Directory " + dir.getPath() + " does not exist");
					}
				}
			}
			logger.info("Completed Indexing");
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
