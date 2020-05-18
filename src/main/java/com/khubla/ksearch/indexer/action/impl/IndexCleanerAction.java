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
import java.util.*;

import org.apache.logging.log4j.*;

import com.khubla.ksearch.SearchConfiguration.*;
import com.khubla.ksearch.domain.*;
import com.khubla.ksearch.indexer.action.*;

/**
 * clean the index for a specific SearchIndex
 *
 * @author tom
 */
public class IndexCleanerAction extends AbstractElasticAction {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(IndexCleanerAction.class);
	/**
	 * search index
	 */
	private final SearchIndex searchIndex;

	/**
	 * ctor
	 *
	 * @param file
	 * @throws Exception
	 */
	public IndexCleanerAction(SearchIndex searchIndex) throws Exception {
		super();
		this.searchIndex = searchIndex;
	}

	@Override
	public void doAction() {
		try {
			logger.info("Cleannig index " + searchIndex.getName());
			try {
				/*
				 * pages of 100
				 */
				final int pagesize = 100;
				int idx = 0;
				List<FileDataSource> filedataSources = null;
				do {
					filedataSources = elasticService.getAll(searchIndex.getElasticIndexName(), idx, pagesize);
					idx += filedataSources.size();
					for (final FileDataSource fileDataSource : filedataSources) {
						processFile(fileDataSource);
					}
				} while (filedataSources.size() == pagesize);
			} catch (final Exception e) {
				logger.error("Exception in processIndex", e);
			}
		} catch (final Exception e) {
			logger.error("Exception in IndexCleanerAction", e);
		}
	}

	/**
	 * process a file
	 *
	 * @param fileDataSource
	 */
	private void processFile(FileDataSource fileDataSource) {
		try {
			final String fn = fileDataSource.getFile_absolute_path();
			/*
			 * if file is no longer on filesystem, remove it
			 */
			final File file = new File(fn);
			if (false == file.exists()) {
				logger.info("Deleting: " + fn);
				elasticService.delete(searchIndex.getElasticIndexName(), fn);
			}
			/*
			 * if file is not on a path we index, then maybe we changed the index paths, we need to remove the file from elastic
			 */
			boolean foundPath = false;
			for (final String path : searchIndex.getDirs()) {
				if (fn.startsWith(path)) {
					foundPath = true;
					break;
				}
			}
			if (foundPath == false) {
				logger.info("Deleting: " + fn);
				elasticService.delete(searchIndex.getElasticIndexName(), fn);
			}
		} catch (final Exception e) {
			logger.error("Exception in IndexCleanerAction for: '" + fileDataSource.getFile_absolute_path() + "'", e);
		}
	}
}
