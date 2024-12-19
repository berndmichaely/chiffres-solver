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

/**
 * Enum to define types and default colors for solution graph nodes.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public enum NodeType
{
	/**
	 * javafx.scene.paint.Color.LIGHTGREEN
	 */
	ROOT_NODE(0x90, 0xee, 0x90),
	/**
	 * javafx.scene.paint.Color.LIGHTBLUE
	 */
	INNER_NODE(0xad, 0xd8, 0xe6),
	/**
	 * javafx.scene.paint.Color.DARKSALMON
	 */
	LEAF_NODE(0xe9, 0x96, 0x7a),
	/**
	 * javafx.scene.paint.Color.WHITESMOKE
	 */
	OPERATOR(0xf5, 0xf5, 0xf5);

	private final int red, green, blue;

	private NodeType(int red, int green, int blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int getRed()
	{
		return red;
	}

	public int getGreen()
	{
		return green;
	}

	public int getBlue()
	{
		return blue;
	}

	/**
	 * Returns a default color code for the node type.
	 *
	 * @return a string in the form #rrggbb
	 */
	public String getColorCode()
	{
		return String.format("#%02x%02x%02x", red, green, blue);
	}
}
