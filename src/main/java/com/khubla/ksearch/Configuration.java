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

import java.io.*;
import java.util.*;

public class Configuration {
	private String dir;
	private int threads;

	public String getDir() {
		return dir;
	}

	public int getThreads() {
		return threads;
	}

	public void load(String propertiesFile) throws FileNotFoundException, IOException {
		/*
		 * properties
		 */
		final Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		threads = Integer.parseInt(properties.getProperty("threads"));
		dir = properties.getProperty("dir");
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
