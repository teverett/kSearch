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
package com.khubla.ksearch.service;

public class ServiceFactory {
	private static ServiceFactory instance = null;

	public static ServiceFactory getInstance() throws Exception {
		if (null == instance) {
			instance = new ServiceFactory();
		}
		return instance;
	}

	private ServiceFactory() {
	}

	public ElasticService getElasticService() throws Exception {
		return new ElasticService();
	}

	public FileService getFileService() throws Exception {
		return new FileService();
	}
}
