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
package de.bernd_michaely.chiffres.fx.canvas;

import de.bernd_michaely.chiffres.graphics.AbstractTreeNode;
import de.bernd_michaely.chiffres.graphics.NodeType;

/**
 * Class to represent a node in a canvas based SolutionGraph representation.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class CanvasTreeNode implements AbstractTreeNode
{
	private final double x, y, width, height;
	private final double fontSize;
	private final NodeType nodeType;
	private final AbstractTreeNode treeNodeParent;

	CanvasTreeNode(double x, double y, double width, double height,
		double fontSize, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.fontSize = fontSize;
		this.nodeType = nodeType;
		this.treeNodeParent = treeNodeParent;
	}

	double getX()
	{
		return x;
	}

	double getY()
	{
		return y;
	}

	double getWidth()
	{
		return width;
	}

	double getHeight()
	{
		return height;
	}

	double getFontSize()
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
