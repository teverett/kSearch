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

import java.util.concurrent.*;

import org.apache.logging.log4j.*;

import com.khubla.ksearch.*;
import com.khubla.ksearch.SearchConfiguration.*;
import com.khubla.ksearch.progress.*;

public class IndexScheduler {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(IndexScheduler.class);
	/**
	 * callback
	 */
	private final ProgressCallback progressCallback;

	public IndexScheduler(ProgressCallback progressCallback) throws Exception {
		this.progressCallback = progressCallback;
	}

	public void start() {
		try {
			final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(SearchConfiguration.getInstance().getIndex_threads());
			for (final SearchIndex searchIndex : SearchConfiguration.getInstance().getIndices().values()) {
				scheduledExecutorService.scheduleAtFixedRate(new Indexer(searchIndex, progressCallback), 0, searchIndex.getRefresh_minutes(), TimeUnit.MINUTES);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
