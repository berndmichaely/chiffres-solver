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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to provide a tree representation of a calculation stack.
 *
 * @author Bernd Michaely
 */
class SolutionTree
{
	private final List<List<Operand>> operands = new ArrayList<>();
	private final Map<Operand, NodeInfo> mapNodeInfo = new IdentityHashMap<>();

	/**
	 * Class to encapsulate information about the position of operand nodes in the
	 * tree.
	 */
	static class NodeInfo
	{
		private final int level;
		private final int levelPosition;

		NodeInfo(int level, int levelPosition)
		{
			this.level = level;
			this.levelPosition = levelPosition;
		}

		/**
		 * Returns the tree level. Numbering starts at zero.
		 *
		 * @return the tree level
		 */
		int getLevel()
		{
			return this.level;
		}

		/**
		 * Returns the position within the tree level. The value is in the range of
		 * [0,
		 * {@link #getNumNodesOnLevel(int) getNumNodesOnLevel(getMaxDepth()-1)}].
		 *
		 * @return the position within the tree level
		 */
		int getLevelPosition()
		{
			return this.levelPosition;
		}
	}

	/**
	 * Creates a SolutionTree from a root Operation.
	 *
	 * @param rootOperation the given root Operation
	 */
	SolutionTree(Operation rootOperation)
	{
		if (rootOperation != null)
		{
			dispatchOperationsToTreeLevels(rootOperation, 0);
		}
	}

	private void dispatchOperationsToTreeLevels(Operand operand, int level)
	{
		if (this.operands.size() <= level)
		{
			this.operands.add(new ArrayList<>());
		}
		final List<Operand> treeLevel = this.operands.get(level);
		final NodeInfo nodeInfo = new NodeInfo(level, treeLevel.size());
		treeLevel.add(operand);
		this.mapNodeInfo.put(operand, nodeInfo);
		if (operand instanceof Operation)
		{
			final Operation operation = (Operation) operand;
			dispatchOperationsToTreeLevels(operation.getOperand1(), level + 1);
			dispatchOperationsToTreeLevels(operation.getOperand2(), level + 1);
		}
	}

	/**
	 * Returns the maximum depth of the generated tree, that is the number of tree
	 * levels.
	 *
	 * @return the number of tree levels
	 */
	int getMaxDepth()
	{
		return this.operands.size();
	}

	/**
	 * Returns the number of nodes in the given tree level.
	 *
	 * @param level the given tree level
	 * @return the number of nodes in the given tree level
	 */
	int getNumNodesOnLevel(int level)
	{
		return ((level >= 0) && (level < getMaxDepth())) ?
			this.operands.get(level).size() : 0;
	}

	/**
	 * Returns a tree representation of the Operation stack. The root element is
	 * at index 0 of the first outer list. Each inner list represents one tree
	 * level.
	 *
	 * @return a tree representation of the Operation stack
	 */
	List<List<Operand>> getTree()
	{
		return this.operands;
	}

	/**
	 * Returns the root operation.
	 *
	 * @return the root operation, if available, null otherwise
	 */
	Operation getRootOperation()
	{
		if (this.operands.size() > 0)
		{
			final List<Operand> rootLevel = this.operands.get(0);
			if (rootLevel.size() > 0)
			{
				final Operand operand = rootLevel.get(0);
				if (operand instanceof Operation)
				{
					return (Operation) operand;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a node info about the given operand.
	 *
	 * @param operand the given operand
	 * @return a node info about the given operand
	 */
	NodeInfo getNodeInfo(Operand operand)
	{
		return this.mapNodeInfo.get(operand);
	}
}
