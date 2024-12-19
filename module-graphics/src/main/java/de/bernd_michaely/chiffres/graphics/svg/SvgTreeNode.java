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
 * Class to represent a node in a SVG SolutionGraph representation.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class SvgTreeNode implements AbstractTreeNode
{
	private final float x, y, width, height;
	private final float fontSize;
	private final NodeType nodeType;
	private final AbstractTreeNode treeNodeParent;

	SvgTreeNode(double x, double y, double width, double height,
		double fontSize, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		this.x = (float) x;
		this.y = (float) y;
		this.width = (float) width;
		this.height = (float) height;
		this.fontSize = (float) fontSize;
		this.nodeType = nodeType;
		this.treeNodeParent = treeNodeParent;
	}

	float getX()
	{
		return x;
	}

	float getY()
	{
		return y;
	}

	float getWidth()
	{
		return width;
	}

	float getHeight()
	{
		return height;
	}

	float getFontSize()
	{
		return fontSize;
	}

	NodeType getNodeType()
	{
		return nodeType;
	}

	@Override
	public AbstractTreeNode getTreeNodeParent()
	{
		return treeNodeParent;
	}
}
