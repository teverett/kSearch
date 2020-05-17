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
package com.khubla.ksearch.httpserver;

import java.net.*;
import java.util.*;

import javax.servlet.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;

import spark.servlet.*;

/*
 * https://github.com/jetty-project/embedded-jetty-jsp
 */
/*
 * https://www.eclipse.org/jetty/documentation/9.3.x/advanced-extras.html
 */
public class WWWServer {
	/**
	 * http port
	 */
	private final int port;
	/**
	 * server
	 */
	private Server server = null;

	public WWWServer(int port) throws ServletException, MalformedURLException {
		this.port = port;
	}

	public void join() throws InterruptedException {
		if (null != server) {
			server.join();
		}
	}

	public void start() throws Exception {
		/*
		 * server
		 */
		server = new Server(port);
		/*
		 * add servlet handler
		 */
		final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setWelcomeFiles(new String[] { "index" });
		servletContextHandler.setContextPath("/");
		server.setHandler(servletContextHandler);
		/*
		 * add default servlet
		 */
		final DefaultServlet defaultServlet = new DefaultServlet();
		final ServletHolder defaultServletHolder = new ServletHolder("default", defaultServlet);
		servletContextHandler.addServlet(defaultServletHolder, "/");
		/*
		 * add spark filter
		 */
		final FilterHolder sparkFilterHolder = new FilterHolder();
		final SparkFilter sparkFilter = new SparkFilter();
		sparkFilterHolder.setFilter(sparkFilter);
		sparkFilterHolder.setInitParameter("applicationClass", KSearchSparkApplication.class.getName());
		servletContextHandler.addFilter(sparkFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
		/*
		 * go
		 */
		server.start();
	}

	public void stop() throws Exception {
		if (null != server) {
			server.stop();
		}
	}
}