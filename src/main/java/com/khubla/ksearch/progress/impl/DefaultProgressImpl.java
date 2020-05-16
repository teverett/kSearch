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
package com.khubla.ksearch.progress.impl;

import java.util.concurrent.*;

import com.khubla.ksearch.progress.*;

public class DefaultProgressImpl implements ProgressCallback {
	private final static int TT = 10;
	private final long starttine = System.currentTimeMillis();

	private String convert(long miliSeconds) {
		final int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
		final int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
		final int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
		return String.format("%02d:%02d:%02d", hrs, min, sec);
	}

	@Override
	public void progress(int progress, int total) {
		if ((progress % TT) == 0) {
			final double elapsedMS = System.currentTimeMillis() - starttine;
			final double timePerIncrementMS = elapsedMS / progress;
			final long remainingIncrements = total - progress;
			final double remainingtimeMS = timePerIncrementMS * remainingIncrements;
			System.out.println(progress + "/" + total + " remaining time: " + convert((long) remainingtimeMS));
		}
	}

	@Override
	public void status(String message) {
		System.out.println(message);
	}
}