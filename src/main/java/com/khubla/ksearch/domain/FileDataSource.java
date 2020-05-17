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
package com.khubla.ksearch.domain;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;

import com.khubla.ksearch.service.*;

public class FileDataSource {
	/**
	 * data
	 */
	public static final String DATA = "data";
	/**
	 * modified date
	 */
	public static final String MODIFIED_DATE = "modified_date";
	/**
	 * file is hidden?
	 */
	public static final String HIDDEN = "hidden";
	/**
	 * date added to index
	 */
	public static final String ADDITION_DATE = "addition_date";
	/**
	 * file extension
	 */
	public static final String FILE_EXTENSION = "file_extension";

	/**
	 * create a FileData domain object from a File
	 *
	 * @return FileData object
	 * @throws Exception
	 */
	public static FileDataSource buildFileDataSource(File file) throws Exception {
		final FileDataSource ret = new FileDataSource();
		final String fileData = ServiceFactory.getInstance().getFileReaderService().readFile(file);
		ret.setData(fileData);
		ret.setAddition_date(new Date().getTime());
		ret.setModified_date(file.lastModified());
		ret.setHidden(file.isHidden());
		ret.setFile_extension(FilenameUtils.getExtension(file.getName()));
		ret.setName(file.getName());
		/*
		 * beware, this is the PK!
		 */
		ret.setFile_absolute_path(file.getAbsolutePath());
		return ret;
	}

	/**
	 * file data
	 */
	private String data;
	/**
	 * file modification date
	 */
	private long modified_date;
	/**
	 * file addition date
	 */
	private long addition_date;
	/**
	 * file is hidden
	 */
	private boolean hidden;
	/**
	 * file extension
	 */
	private String file_extension;
	/**
	 * filename
	 */
	private String file_absolute_path;
	/**
	 * name
	 */
	private String name;

	public long getAddition_date() {
		return addition_date;
	}

	public String getData() {
		return data;
	}

	public String getFile_absolute_path() {
		return file_absolute_path;
	}

	public String getFile_extension() {
		return file_extension;
	}

	public long getModified_date() {
		return modified_date;
	}

	public String getName() {
		return name;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setAddition_date(long addition_date) {
		this.addition_date = addition_date;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setFile_absolute_path(String file_absolute_path) {
		this.file_absolute_path = file_absolute_path;
	}

	public void setFile_extension(String file_extension) {
		this.file_extension = file_extension;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setModified_date(long modified_date) {
		this.modified_date = modified_date;
	}

	public void setName(String name) {
		this.name = name;
	}
}
