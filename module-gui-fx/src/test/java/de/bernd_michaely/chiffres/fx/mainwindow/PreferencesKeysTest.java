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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PreferencesKeys.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class PreferencesKeysTest
{
	private void testNameToKey(String name, String expected)
	{
		final String result = PreferencesKeys.nameToKey(name);
		System.out.format("test PreferencesKeys.nameToKey(»%s«) = »%s«%n", name, result);
		assertEquals(expected, result);
	}

	@Test
	public void testNameToKey1()
	{
		testNameToKey("ID_PREF_ONE", "one");
	}

	@Test
	public void testNameToKey123()
	{
		testNameToKey("ID_PREF_ONE_TWO_THREE", "oneTwoThree");
	}

	@Test
	public void testNameToKeyDefaultPrefix()
	{
		testNameToKey("ID_PREF_", "idPref");
	}

	@Test
	public void testNameToKeyEmpty1()
	{
		testNameToKey("_", "_");
	}

	@Test
	public void testNameToKeyEmpty2()
	{
		testNameToKey("__", "__");
	}

	@Test
	public void testNameToKeyCustomPrefix()
	{
		testNameToKey("ID_MY_KEY", "idMyKey");
	}

	@Test
	public void testNameToKeyCustomPostfix()
	{
		final String postfix = "XYZ";
		final String result = PreferencesKeys.ID_PREF_PATH_SAVE_IMG.key(postfix);
		System.out.format("test PreferencesKeys.key(»%s«) = »%s«%n", postfix, result);
		final String expected = "pathSaveImgXyz";
		assertEquals(expected, result);
	}
}
