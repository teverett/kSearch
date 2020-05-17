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

import org.apache.logging.log4j.*;

import com.khubla.ksearch.domain.*;
import com.khubla.ksearch.indexer.action.*;
import com.khubla.ksearch.service.*;

public class IndexCleanerAction extends AbstractElasticAction implements FileIterator {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(IndexCleanerAction.class);

	/**
	 * ctor
	 *
	 * @param file
	 * @throws Exception
	 */
	public IndexCleanerAction() throws Exception {
		super();
	}

	@Override
	public void doAction() {
		try {
			logger.info("Running IndexCleanerAction");
			elasticService.iterateAll(this);
		} catch (final Exception e) {
			logger.error("Exception in IndexCleanerAction", e);
		}
	}

	@Override
	public void file(FileDataSource fileDataSource) {
		try {
			final String fn = fileDataSource.getFile_absolute_path();
			/*
			 * if file is no longer on filesystem, remove it
			 */
			final File file = new File(fn);
			if (false == file.exists()) {
				logger.info("Deleting: " + fn);
				elasticService.delete(fn);
			}
			/*
			 * if file is not on a path we index, then maybe we changed the index paths, we need to remove the file from elastic
			 */
			boolean foundPath = false;
			for (final String path : com.khubla.ksearch.Configuration.getConfiguration().getDirs()) {
				if (fn.startsWith(path)) {
					foundPath = true;
					break;
				}
			}
			if (foundPath == false) {
				logger.info("Deleting: " + fn);
				elasticService.delete(fn);
			}
		} catch (final Exception e) {
			logger.error("Exception in IndexCleanerAction for: '" + fileDataSource.getFile_absolute_path() + "'", e);
		}
	}
}
