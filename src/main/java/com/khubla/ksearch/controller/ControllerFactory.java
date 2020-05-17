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
package com.khubla.ksearch.controller;

import com.khubla.ksearch.controller.impl.*;

public class ControllerFactory {
	private static ControllerFactory instance;

	public static ControllerFactory getInstance() {
		if (null == instance) {
			instance = new ControllerFactory();
		}
		return instance;
	}

	private ControllerFactory() {
	}

	public DoSearchControllerImpl getDoSearchController() {
		return new DoSearchControllerImpl();
	}

	public IndexControllerImpl getIndexController() {
		return new IndexControllerImpl();
	}
}
