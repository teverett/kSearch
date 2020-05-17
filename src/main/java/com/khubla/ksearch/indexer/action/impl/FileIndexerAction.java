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

public class FileIndexerAction extends AbstractElasticAction {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(FileIndexerAction.class);
	/**
	 * File
	 */
	private final String filePath;

	/**
	 * ctor
	 *
	 * @param file
	 * @throws Exception
	 */
	public FileIndexerAction(String filePath) throws Exception {
		super();
		this.filePath = filePath;
	}

	@Override
	public void doAction() {
		try {
			logger.info("Indexing: " + filePath);
			/*
			 * exists?
			 */
			if (elasticService.exists(filePath)) {
				/*
				 * needs update?
				 */
				final FileDataSource fileDataSource = elasticService.getMetadata(filePath);
				final long filedate = fileDataSource.getModified_date();
				if (filedate < lastModified()) {
					logger.info("Updating: " + filePath);
					elasticService.update(FileDataSource.buildFileDataSource(filePath));
				} else {
					logger.info("Skipped: " + filePath);
				}
			} else {
				logger.info("Writing: " + filePath);
				elasticService.write(FileDataSource.buildFileDataSource(filePath));
			}
		} catch (final Exception e) {
			logger.error("Exception indexing '" + filePath + "'", e);
		}
	}

	private long lastModified() {
		return new File(filePath).lastModified();
	}
}
