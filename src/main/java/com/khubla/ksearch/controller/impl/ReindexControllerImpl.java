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
package com.khubla.ksearch.controller.impl;

import org.apache.logging.log4j.*;

import com.khubla.ksearch.*;
import com.khubla.ksearch.SearchConfiguration.*;
import com.khubla.ksearch.controller.*;
import com.khubla.ksearch.service.*;

import spark.*;

public class ReindexControllerImpl extends AbstractController {
	public static class HackyThread extends Thread {
		/**
		 * logger
		 */
		private static final Logger logger = LogManager.getLogger(HackyThread.class);
		private final String indexName;

		public HackyThread(String indexName) {
			super();
			this.indexName = indexName;
		}

		@Override
		public void run() {
			try {
				final SearchIndex searchIndex = SearchConfiguration.getInstance().getIndices().get(indexName);
				if (null != searchIndex) {
					final ElasticService elasticService = ServiceFactory.getInstance().getElasticService();
					elasticService.deleteAll(indexName);
					final IndexService indexService = ServiceFactory.getInstance().getIndexService();
					indexService.runIndexer(searchIndex);
				}
			} catch (final Exception e) {
				logger.error("Error in HackyThread", e);
			}
		}
	}

	@Override
	public Object renderGET(Request request, Response response) throws Exception {
		final String indexName = getIndexName(request);
		/*
		 * spin a thread to do that....
		 */
		new Thread(new HackyThread(indexName)).start();
		response.redirect("/index");
		return null;
	}
}