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
package de.bernd_michaely.chiffres.graphics.svg;

import de.bernd_michaely.chiffres.graphics.AbstractTreeNode;
import de.bernd_michaely.chiffres.graphics.NodeType;

/**
 * Tree node type for operands and numeric values.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class SvgTreeNodeTypeValue extends SvgTreeNode
{
	private final String text;
	private final float dx, dy;

	SvgTreeNodeTypeValue(double x, double y, double width, double height,
		double fontSize, String text, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		super(x, y, width, height, fontSize, nodeType, treeNodeParent);
		this.dx = (float) (x - width / 2);
		this.dy = (float) (y - height / 2);
		this.text = text;
	}

	String getText()
	{
		return text;
	}

	float getDx()
	{
		return dx;
	}

	float getDy()
	{
		return dy;
	}
}
