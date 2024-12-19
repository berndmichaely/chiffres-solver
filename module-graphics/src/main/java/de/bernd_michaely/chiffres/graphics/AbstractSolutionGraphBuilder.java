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

import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract builder for solution graph graphics.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public abstract class AbstractSolutionGraphBuilder
{
	private int treeDepth;
	private double nodeWidth;
	private double hGap, hGapMin, hGapPref;
	private final Map<AbstractTreeNode, Integer> mapOperationNodes = new HashMap<>();

	/**
	 * Initializes the builder. By default clears internal data structures. This
	 * method is called after the graph size has been set.
	 */
	protected void initialize()
	{
		mapOperationNodes.clear();
	}

	protected int getTreeDepth()
	{
		return treeDepth;
	}

	void setTreeDepth(int treeDepth)
	{
		this.treeDepth = treeDepth;
	}

	protected double getNodeWidth()
	{
		return nodeWidth;
	}

	void setNodeWidth(double nodeWidth)
	{
		this.nodeWidth = nodeWidth;
	}

	protected double getHGap()
	{
		return hGap;
	}

	protected void setHGap(double hGap)
	{
		this.hGap = hGap;
	}

	protected double getHGapMin()
	{
		return hGapMin;
	}

	void setHGapMin(double hGapMin)
	{
		this.hGapMin = hGapMin;
	}

	protected double getHGapPref()
	{
		return hGapPref;
	}

	void setHGapPref(double hGapPref)
	{
		this.hGapPref = hGapPref;
	}

	protected double calcWidth(double hGap)
	{
		return (2 * getTreeDepth() - 1) * (getNodeWidth() + hGap);
	}

	protected double calcX(int colIndex)
	{
		final double col = colIndex - 0.5;
		return col * (getNodeWidth() + getHGap());
	}

	protected Map<AbstractTreeNode, Integer> getMapOperationNodes()
	{
		return mapOperationNodes;
	}

	/**
	 * Indicates the method to create math symbols.
	 *
	 * @return true, if math symbols are created as graphics (which is the
	 *         default), false, if math symbols are created as text characters
	 */
	protected boolean isCreatingMathSymbolGraphics()
	{
		return true;
	}

	/**
	 * Returns true, if drop shadows are to be created.
	 *
	 * @return true, if drop shadows are to be created (which is the default)
	 */
	protected boolean isCreatingDropShadows()
	{
		return true;
	}

	protected abstract void setGraphHeight(double height);

	protected abstract void setGraphWidth(double width);

	protected abstract AbstractTreeNode buildNodeTypeValue(double x, double y,
		double width, double height, double fontSize, String text, NodeType nodeType,
		AbstractTreeNode treeNodeParent);

	protected abstract AbstractTreeNode buildNodeTypeOperator(double x, double y,
		double width, double height, double fontSize, Sign symbol, NodeType nodeType,
		AbstractTreeNode treeNodeParent);

	protected abstract void addNode(AbstractTreeNode abstractTreeNode);

	protected abstract void addEdge(AbstractTreeNode abstractTreeNode);
}
