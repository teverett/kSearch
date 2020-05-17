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
package com.khubla.ksearch.indexer.action;

import org.apache.logging.log4j.*;

import com.khubla.ksearch.service.*;

public abstract class AbstractElasticAction implements Runnable {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(AbstractElasticAction.class);
	/**
	 * elastic
	 */
	protected final ElasticService elasticService;

	public AbstractElasticAction() throws Exception {
		elasticService = ServiceFactory.getInstance().getElasticService();
	}

	protected abstract void doAction();

	@Override
	public void run() {
		try {
			doAction();
		} catch (final Exception e) {
			logger.error("Exception in run()", e);
		}
	}
}
