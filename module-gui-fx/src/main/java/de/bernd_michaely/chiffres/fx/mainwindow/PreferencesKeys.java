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
package de.bernd_michaely.chiffres.fx.mainwindow;

import de.bernd_michaely.chiffres.fx.JChiffresFXApplication;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * Keys for Preferences.userNodeForPackage(JChiffresFXApplication.class).
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public enum PreferencesKeys
{
	// Start options:
	ID_PREF_OPTION_ON_START,
	// Default options:
	ID_PREF_MODE_DEFAULT_NUM_THREADS,
	ID_PREF_MODE_DEFAULT_INTERMEDIATE,
	// Extended options:
	ID_PREF_MODE_EXTENDED_NUM_THREADS,
	ID_PREF_MODE_EXTENDED_INTERMEDIATE,
	ID_PREF_MODE_EXTENDED_NUM_OPERANDS,
	// TrackableWindowState options:
	ID_PREF_WINDOW_POS_X,
	ID_PREF_WINDOW_POS_Y,
	ID_PREF_WINDOW_WITH,
	ID_PREF_WINDOW_HEIGHT,
	ID_PREF_WINDOW_MAXIMIZED,
	ID_PREF_WINDOW_ICONIFIED,
	// Image options:
	ID_PREF_IMAGE_FILE_FORMAT,
	ID_PREF_IMAGE_TRANSPARENT,
	ID_PREF_IMAGE_FONT_SIZE,
	// Save options:
	ID_PREF_PATH_SAVE_IMG,
	ID_PREF_PATH_SAVE_CSV;

	/**
	 * Global preferences.
	 */
	public static Preferences preferences = Preferences
		.userNodeForPackage(JChiffresFXApplication.class);

	/**
	 * All enum constant names should have this prefix.
	 */
	private static final String COMMON_PREFIX = "ID_PREF_";

	static String nameToKey(String name)
	{
		if ((name == null) || name.isBlank())
		{
			throw new IllegalArgumentException("null or blank argument to nameToKey");
		}
		final StringBuilder result = new StringBuilder();
		final String key = (name.startsWith(COMMON_PREFIX) && !name.equals(COMMON_PREFIX)) ?
			name.substring(COMMON_PREFIX.length()) : name;
		final String[] parts = key.split("_");
		final int n = parts.length;
		if (n > 0)
		{
			result.append(parts[0].toLowerCase(Locale.ROOT));
			for (int i = 1; i < n; i++)
			{
				final String p = parts[i];
				result.append(p.substring(0, 1).toUpperCase(Locale.ROOT));
				if (p.length() > 1)
				{
					result.append(p.substring(1).toLowerCase(Locale.ROOT));
				}
			}
		}
		else
		{
			result.append(key);
		}
		return result.toString();
	}

	public String key()
	{
		return key(null);
	}

	public String key(String postfix)
	{
		final String name = ((postfix != null) && !postfix.isBlank()) ?
			(name() + '_' + postfix.trim()) : name();
		return nameToKey(name);
	}
}
