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

import java.util.*;

import spark.*;
import spark.template.freemarker.*;

public abstract class AbstractController implements Controller {
	/**
	 * attributes
	 */
	private final Map<String, Object> attributes = new HashMap<>();

	protected void addAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	protected Object renderFTL(String ftl) throws Exception {
		/*
		 * org name
		 */
		addAttribute("orgname", com.khubla.ksearch.Configuration.getConfiguration().getOrgname());
		return new FreeMarkerEngine().render(new ModelAndView(attributes, ftl));
	}
}