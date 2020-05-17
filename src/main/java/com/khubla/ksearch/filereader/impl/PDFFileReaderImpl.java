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
package com.khubla.ksearch.filereader.impl;

import java.io.*;

import org.apache.logging.log4j.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;

import com.khubla.ksearch.filereader.FileReader;

public class PDFFileReaderImpl implements FileReader {
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(PDFFileReaderImpl.class);

	public PDFFileReaderImpl() {
		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
	}

	/**
	 * read file as text
	 *
	 * @param file
	 * @return file text
	 * @throws IOException
	 */
	@Override
	public String read(File file) throws Exception {
		PDDocument pdDocument = null;
		try {
			pdDocument = PDDocument.load(file);
			final PDFTextStripper pdfTextStripper = new PDFTextStripper();
			return pdfTextStripper.getText(pdDocument);
		} catch (final Exception e) {
			logger.error("Error parsing file " + file.getAbsolutePath(), e);
			throw e;
		} finally {
			if (null != pdDocument) {
				pdDocument.close();
			}
		}
	}
}
