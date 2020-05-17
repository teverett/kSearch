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

import org.slf4j.*;

import com.khubla.ksearch.indexer.action.*;

public class FileIndexerAction extends AbstractElasticAction {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileIndexerAction.class);
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

	@Override
	public void doAction() {
		try {
			logger.info("Indexing: " + file.getAbsolutePath());
			/*
			 * exists?
			 */
			if (elasticService.exists(file)) {
				/*
				 * needs update?
				 */
				final long filedate = elasticService.filedate(file);
				if (filedate < file.lastModified()) {
					logger.info("Updating: " + file.getAbsolutePath());
					elasticService.update(file);
				} else {
					logger.info("Skipped: " + file.getAbsolutePath());
				}
			} else {
				logger.info("Writing: " + file.getAbsolutePath());
				elasticService.write(file);
			}
		} catch (final Exception e) {
			logger.error("Exception indexing '" + file.getAbsolutePath() + "'", e);
		}
	}
}