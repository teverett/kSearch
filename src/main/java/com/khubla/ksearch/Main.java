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
package com.khubla.ksearch;

import org.apache.commons.cli.*;

import com.khubla.ksearch.httpserver.*;
import com.khubla.ksearch.indexer.*;
import com.khubla.ksearch.progress.impl.*;

/**
 * @author tom
 */
public class Main {
	/**
	 * file option
	 */
	private static final String CONFIG_OPTION = "config";

	public static void main(String[] args) {
		try {
			System.out.println("khubla.com kSearch");
			/*
			 * options
			 */
			final Options options = new Options();
			final Option oo = Option.builder().argName(CONFIG_OPTION).longOpt(CONFIG_OPTION).type(String.class).hasArg().required(true).desc("configuration properties file").build();
			options.addOption(oo);
			/*
			 * parse
			 */
			final CommandLineParser parser = new DefaultParser();
			CommandLine cmd = null;
			try {
				cmd = parser.parse(options, args);
			} catch (final Exception e) {
				e.printStackTrace();
				final HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("posix", options);
				System.exit(0);
			}
			/*
			 * get the config file
			 */
			final String configFilename = cmd.getOptionValue(CONFIG_OPTION);
			if (null != configFilename) {
				/*
				 * set the name
				 */
				Configuration.propertiesFile = configFilename;
				/*
				 * indexer
				 */
				System.out.println("Starting Indexer");
				final Indexer indexer = new Indexer();
				indexer.index(new DefaultProgressImpl());
				/*
				 * http
				 */
				System.out.println("Starting HTTP server on port: " + Configuration.getConfiguration().getHttpPort());
				final WWWServer wwwServer = new WWWServer(Configuration.getConfiguration().getHttpPort());
				wwwServer.start();
				wwwServer.join();
			} else {
				throw new Exception("File was not supplied");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}