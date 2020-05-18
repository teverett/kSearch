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

import java.util.*;

import com.khubla.ksearch.controller.*;
import com.khubla.ksearch.domain.*;
import com.khubla.ksearch.service.*;

import spark.*;

public class DoSearchControllerImpl extends AbstractController {
	@Override
	public Object renderGET(Request request, Response response) throws Exception {
		final String searchterm = request.queryParams("searchterm");
		/*
		 * service
		 */
		final ElasticService elasticService = ServiceFactory.getInstance().getElasticService();
		/*
		 * page
		 */
		final String pageparam = request.queryParams("page");
		int page = 0;
		if (null != pageparam) {
			page = Integer.parseInt(pageparam);
		}
		/*
		 * get page
		 */
		final int pagesize = com.khubla.ksearch.Configuration.getConfiguration().getPage_size();
		final List<FileDataSource> results = elasticService.query(searchterm, (page * pagesize), pagesize);
		addAttribute("files", results);
		addAttribute("searchterm", searchterm);
		/*
		 * page data
		 */
		final int previouspage = (page > 0) ? page - 1 : 0;
		final int nextpage = page + 1;
		addAttribute("previouspage", previouspage);
		addAttribute("nextpage", nextpage);
		addAttribute("showprevious", (page > 0) ? true : false);
		addAttribute("shownext", (results.size() > 0));
		return renderFTL("results.ftl");
	}
}