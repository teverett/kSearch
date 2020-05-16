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
package com.khubla.ksearch.index;

import java.io.*;

import org.slf4j.*;

public class FileIndexer implements Runnable {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileIndexer.class);
	/**
	 * File
	 */
	private final File file;

	public FileIndexer(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		try {
			System.out.println(file.getName());
		} catch (final Exception e) {
			logger.error("Exception indexing '" + file.getAbsolutePath() + "'");
		}
	}
}
