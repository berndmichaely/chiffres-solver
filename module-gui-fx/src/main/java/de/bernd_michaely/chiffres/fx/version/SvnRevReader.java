/*
 * Copyright (C) 2024 Bernd Michaely (info@bernd-michaely.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.bernd_michaely.chiffres.fx.version;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads a subversion revision info from a resource file.
 *
 * @author Bernd Michaely
 */
public class SvnRevReader
{
	private static final Logger logger = Logger.getLogger(
		SvnRevReader.class.getName());
	private static final String RESOURCE_SVN_REV = "svnrev";

	/**
	 * Returns a String containing a subversion revision info read from a resource
	 * file.
	 *
	 * @return a String containing a subversion revision info or null, if the
	 *         resource could not be found
	 */
	public static String readSvnRev()
	{
		try (LineNumberReader reader = new LineNumberReader(
			new InputStreamReader(SvnRevReader.class.getResourceAsStream(RESOURCE_SVN_REV))))
		{
			return reader.readLine();
		}
		catch (IOException | NullPointerException ex)
		{
			logger.log(Level.WARNING, "svn revision resource file »{0}« not found",
				SvnRevReader.class.getPackageName() + '.' + RESOURCE_SVN_REV);
			return null;
		}
	}
}
