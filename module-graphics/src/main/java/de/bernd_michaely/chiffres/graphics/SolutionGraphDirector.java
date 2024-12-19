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

import de.bernd_michaely.chiffres.calc.Operand;
import de.bernd_michaely.chiffres.calc.Operation;
import de.bernd_michaely.chiffres.calc.Operator;
import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import java.util.Arrays;
import java.util.Map;

import static de.bernd_michaely.chiffres.calc.Operator.*;
import static de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign.*;
import static de.bernd_michaely.chiffres.graphics.NodeType.*;

/**
 * Director for abstract solution graph builder,
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class SolutionGraphDirector
{
	private static final Map<Operator, Sign> OPERATOR_SYMBOLS = Map.of(
		ADD, PLUS_SIGN, SUB, MINUS_SIGN, MUL, MULTIPLICATION_SIGN, DIV, DIVISION_SIGN);
	private final AbstractSolutionGraphBuilder builder;
	private final TreeNodeFactory treeNodeFactory;
	private final int numOperands;
	private double fontSize, height, nodeHeight, vGap;

	private class TreeNodeFactory
	{
		private SolutionTree tree;
		private int numGraphLevels;

		private TreeNodeFactory()
		{
		}

		private void setSolution(Solution solution)
		{
			if (solution != null)
			{
				this.tree = new SolutionTree(solution.getRootOperation());
				builder.setTreeDepth(this.tree.getMaxDepth());
				this.numGraphLevels = 2 * builder.getTreeDepth() - 1;
				builder.setGraphWidth(builder.calcWidth(builder.getHGap()));
			}
			else
			{
				this.tree = null;
				builder.setTreeDepth(0);
				this.numGraphLevels = 0;
				builder.setGraphWidth(0);
			}
		}

		/**
		 * Starts creation of the solution graph.
		 */
		private void createSolutionGraph()
		{
			if (this.tree != null)
			{
				createSolutionGraph(this.tree.getRootOperation(), null);
			}
		}

		private void createSolutionGraph(Operand operand, AbstractTreeNode nodeParent)
		{
			final Operation operation = (operand instanceof Operation) ?
				(Operation) operand : null;
			final SolutionTree.NodeInfo nodeInfo = this.tree.getNodeInfo(operand);
			final int numNodesOnLevel = this.tree.getNumNodesOnLevel(nodeInfo.getLevel());
			final NodeType nodeType = (nodeParent == null) ? ROOT_NODE :
				((operation != null) ? INNER_NODE : LEAF_NODE);
			final int colIndexValue = this.numGraphLevels - 2 * nodeInfo.getLevel();
			final AbstractTreeNode nodeValue = builder.buildNodeTypeValue(
				builder.calcX(colIndexValue),
				calcY(nodeInfo.getLevelPosition(), numNodesOnLevel),
				builder.getNodeWidth(), nodeHeight, fontSize,
				(operand != null) ? "" + operand.getValue() : "",
				nodeType, nodeParent);
			builder.getMapOperationNodes().put(nodeValue, colIndexValue);
			final AbstractTreeNode nodeOperator;
			if (operation != null)
			{
				final SolutionTree.NodeInfo nodeInfo1 = this.tree.getNodeInfo(operation.getOperand1());
				final SolutionTree.NodeInfo nodeInfo2 = this.tree.getNodeInfo(operation.getOperand2());
				final int numNodesOnLevel1 = this.tree.getNumNodesOnLevel(nodeInfo1.getLevel());
				final double y1 = calcY(nodeInfo1.getLevelPosition(), numNodesOnLevel1);
				final double y2 = calcY(nodeInfo2.getLevelPosition(), numNodesOnLevel1);
				final int colIndexOperator = this.numGraphLevels - (2 * nodeInfo.getLevel() + 1);
				nodeOperator = builder.buildNodeTypeOperator(builder.calcX(colIndexOperator),
					(y1 + y2) / 2, builder.getNodeWidth(), nodeHeight * 5 / 4,
					fontSize, OPERATOR_SYMBOLS.get(operation.getOperator()), OPERATOR, nodeValue);
				builder.getMapOperationNodes().put(nodeOperator, colIndexOperator);
			}
			else
			{
				nodeOperator = null;
			}
			Arrays.asList(nodeValue, nodeOperator).forEach(builder::addEdge);
			if (operation != null)
			{
				createSolutionGraph(operation.getOperand1(), nodeOperator);
				createSolutionGraph(operation.getOperand2(), nodeOperator);
			}
			Arrays.asList(nodeValue, nodeOperator).forEach(builder::addNode);
		}

		private double calcY(int rowIndex, int rowNum)
		{
			final double rowHeight = height / rowNum;
			return rowHeight * (rowIndex + 0.5);
		}
	}

	/**
	 * Creates an instance of a solution graph director.
	 *
	 * @param builder     a concrete builder instance
	 * @param numOperands the number of operands the calculation is based on, e.g.
	 *                    {@link de.bernd_michaely.chiffres.calc.CalculationParams#getNumOperands() CalculationParams.getNumOperands()}
	 * @param fontSize    the font size
	 */
	public SolutionGraphDirector(AbstractSolutionGraphBuilder builder,
		int numOperands, double fontSize)
	{
		this.builder = builder;
		this.numOperands = numOperands;
		if (builder != null)
		{
			builder.setTreeDepth(numOperands);
		}
		setFontSize(fontSize);
		this.treeNodeFactory = new TreeNodeFactory();
		if (builder != null)
		{
			builder.initialize();
		}
	}

	/**
	 * Returns the font size.
	 *
	 * @return the font size
	 */
	public double getFontSize()
	{
		return fontSize;
	}

	/**
	 * Set the font size as a base unit for graph size.
	 *
	 * @param fontSize the font size
	 */
	public void setFontSize(double fontSize)
	{
		this.fontSize = fontSize;
		nodeHeight = fontSize * 5 / 3;
		vGap = nodeHeight / 2;
		height = numOperands * nodeHeight + (numOperands + 1) * vGap + getOffsetShadow();
		if (builder != null)
		{
			builder.setNodeWidth(fontSize * 7 / 2);
			builder.setHGapMin(builder.getNodeWidth() / 10);
			builder.setHGapPref(builder.getNodeWidth() * 2 / 3);
			builder.setHGap(builder.getHGapPref());
			builder.setGraphHeight(height);
			builder.setGraphWidth(builder.calcWidth(builder.getHGap()));
		}
	}

	/**
	 * Returns a default offset for drop shadows based on the initial font size.
	 *
	 * @return a default offset for drop shadows
	 */
	public double getOffsetShadow()
	{
		return getOffsetShadow(this.fontSize);
	}

	/**
	 * Returns a default offset for drop shadows based on the given font size.
	 *
	 * @param fontSize the given font size
	 * @return a default offset for drop shadows
	 */
	public static double getOffsetShadow(double fontSize)
	{
		return fontSize / 2;
	}

	/**
	 * Set (only) the solution without actually constructing it to precalculate
	 * graph size. See the concrete builder to query the graph size.
	 *
	 * @param solution the solution to query graph size for
	 */
	public void setSolution(Solution solution)
	{
		if (builder != null)
		{
			treeNodeFactory.setSolution(solution);
		}
	}

	/**
	 * Constructs a graphic for the given solution using the provided builder.
	 *
	 * @see #setSolution(Solution)
	 */
	public void construct()
	{
		if (builder != null)
		{
			treeNodeFactory.createSolutionGraph();
		}
	}

	/**
	 * Constructs a graphic for the given solution using the provided builder.
	 *
	 * @param solution the solution to construct a graphic for
	 */
	public void construct(Solution solution)
	{
		setSolution(solution);
		construct();
	}
}
