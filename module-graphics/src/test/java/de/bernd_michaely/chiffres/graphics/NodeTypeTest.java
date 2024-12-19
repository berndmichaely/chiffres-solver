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
package de.bernd_michaely.chiffres.graphics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class NodeTypeTest
{
	@Test
	public void testGetRGB()
	{
		System.out.println("NodeTypeTest#testGetRGB");
		final NodeType nodeType = NodeType.LEAF_NODE;
		assertEquals(0xe9, nodeType.getRed());
		assertEquals(0x96, nodeType.getGreen());
		assertEquals(0x7a, nodeType.getBlue());
	}

	@Test
	public void testGetColorCode()
	{
		System.out.println("NodeTypeTest#testGetColorCode");
		final String expResult = "#e9967a";
		final String result = NodeType.LEAF_NODE.getColorCode();
		assertEquals(expResult, result);
	}
}
