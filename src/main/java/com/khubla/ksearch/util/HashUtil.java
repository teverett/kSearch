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
package com.khubla.ksearch.util;

import java.security.*;

import org.apache.commons.codec.binary.*;

public class HashUtil {
	/**
	 * hash algo
	 */
	private final static String HASH_ALGO = "MD5";

	public static String createChecksum(byte[] data) throws Exception {
		final MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGO);
		messageDigest.update(data, 0, data.length);
		return Base64.encodeBase64String(messageDigest.digest());
	}
}
