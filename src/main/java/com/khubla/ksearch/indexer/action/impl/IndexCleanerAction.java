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
import com.khubla.ksearch.service.*;

public class IndexCleanerAction extends AbstractElasticAction implements FileIterator {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IndexCleanerAction.class);

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
	public void file(String filename, long filedate) {
		try {
			final File file = new File(filename);
			if (false == file.exists()) {
				logger.info("Deleting: " + filename);
				elasticService.delete(filename);
			}
		} catch (final Exception e) {
			logger.error("Exception in IndexCleanerAction for: '" + filename + "'", e);
		}
	}
}
