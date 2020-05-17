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

public class FileData {
	/**
	 * _index
	 */
	private String _index;
	/**
	 * _type
	 */
	private String _type;
	/**
	 * _id
	 */
	private String _id;
	/**
	 * _score
	 */
	private String _score;
	/**
	 * _source
	 */
	private FileDataSource _source;

	public String get_id() {
		return _id;
	}

	public String get_index() {
		return _index;
	}

	public String get_score() {
		return _score;
	}

	public FileDataSource get_source() {
		return _source;
	}

	public String get_type() {
		return _type;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public void set_index(String _index) {
		this._index = _index;
	}

	public void set_score(String _score) {
		this._score = _score;
	}

	public void set_source(FileDataSource _source) {
		this._source = _source;
	}

	public void set_type(String _type) {
		this._type = _type;
	}
}
