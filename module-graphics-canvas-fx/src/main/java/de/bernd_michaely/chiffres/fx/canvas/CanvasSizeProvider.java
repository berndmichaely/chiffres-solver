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

import de.bernd_michaely.chiffres.graphics.AbstractSolutionGraphBuilder;
import de.bernd_michaely.chiffres.graphics.AbstractTreeNode;
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder;
import de.bernd_michaely.chiffres.graphics.NodeType;

/**
 * Base class for a canvas based SolutionGraphBuilder to provide a
 * pre-calculated canvas size. All builder methods throw an
 * {@link UnsupportedOperationException}.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class CanvasSizeProvider extends AbstractSolutionGraphBuilder
{
	private int canvasWidth, canvasHeight;

	public int getCanvasWidth()
	{
		return canvasWidth;
	}

	public int getCanvasHeight()
	{
		return canvasHeight;
	}

	@Override
	protected void setGraphWidth(double width)
	{
		this.canvasWidth = (int) Math.ceil(width);
	}

	@Override
	protected void setGraphHeight(double height)
	{
		this.canvasHeight = (int) Math.ceil(height);
	}

	@Override
	protected void addEdge(AbstractTreeNode abstractTreeNode)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void addNode(AbstractTreeNode abstractTreeNode)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected AbstractTreeNode buildNodeTypeOperator(double x, double y, double width, double height,
		double fontSize, MathSymbolBuilder.Sign symbol, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected AbstractTreeNode buildNodeTypeValue(double x, double y, double width, double height,
		double fontSize, String text, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		throw new UnsupportedOperationException();
	}
}
