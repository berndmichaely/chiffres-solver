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
package de.bernd_michaely.chiffres.fx.graph;

import de.bernd_michaely.chiffres.graphics.AbstractSolutionGraphBuilder;
import de.bernd_michaely.chiffres.graphics.AbstractTreeNode;
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import de.bernd_michaely.chiffres.graphics.NodeType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Concrete JavaFX shape builder for solution graph graphics.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class SolutionGraphBuilder extends AbstractSolutionGraphBuilder
{
	private final Pane paneOuter, paneInner;
	private final Group group;

	SolutionGraphBuilder()
	{
		this.group = new Group();
		this.paneInner = new Pane(this.group);
		this.paneOuter = new BorderPane(this.paneInner);
		BorderPane.setAlignment(this.paneInner, Pos.CENTER);
		paneOuter.widthProperty().addListener((observable, oldValue, newValue) ->
			updateGraphWidth());
	}

	@Override
	protected AbstractTreeNode buildNodeTypeValue(double x, double y, double width, double height,
		double fontSize, String text, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		return new TreeNodeTypeValue(x, y, width, height, fontSize, text,
			Color.web(nodeType.getColorCode()), (TreeNode) treeNodeParent);
	}

	@Override
	protected AbstractTreeNode buildNodeTypeOperator(double x, double y,
		double width, double height, double fontSize, Sign symbol, NodeType nodeType,
		AbstractTreeNode treeNodeParent)
	{
		return new TreeNodeTypeOperator(x, y, width, height, fontSize, symbol,
			Color.web(nodeType.getColorCode()), (TreeNode) treeNodeParent);
	}

	@Override
	protected void addNode(AbstractTreeNode abstractTreeNode)
	{
		if (abstractTreeNode instanceof TreeNode)
		{
			final TreeNode treeNode = (TreeNode) abstractTreeNode;
			final Node node = treeNode.getNode();
			if (node != null)
			{
				group.getChildren().add(node);
			}
		}
	}

	@Override
	protected void addEdge(AbstractTreeNode abstractTreeNode)
	{
		if (abstractTreeNode instanceof TreeNode)
		{
			final TreeNode treeNode = (TreeNode) abstractTreeNode;
			final Line edgeParent = treeNode.getEdgeParent();
			if (edgeParent != null)
			{
				group.getChildren().add(edgeParent);
			}
		}
	}

	@Override
	protected void setGraphHeight(double height)
	{
		this.paneInner.setMinHeight(height);
		this.paneInner.setPrefHeight(height);
		this.paneInner.setMaxHeight(height);
		this.paneOuter.setMinSize(0, 0);
		this.paneOuter.setPrefHeight(height);
		this.paneOuter.setMaxSize(Integer.MAX_VALUE, height);
	}

	@Override
	protected void setGraphWidth(double width)
	{
		this.paneInner.setMinWidth(width);
		this.paneInner.setPrefWidth(width);
		this.paneInner.setMaxWidth(width);
		this.paneOuter.setPrefWidth(width);
	}

	void updateGraphWidth()
	{
		final double width = this.paneOuter.getWidth();
		final double w = width / (2 * getTreeDepth() - 1) - getNodeWidth();
		setHGap(Math.min(Math.max(w, getHGapMin()), getHGapPref()));
		setGraphWidth(calcWidth(getHGap()));
		getMapOperationNodes().forEach((treeNode, colIndex) ->
			((TreeNode) treeNode).setX(calcX(colIndex)));
		this.paneInner.setScaleX(Math.min(1.0, width / calcWidth(getHGapMin())));
	}

	void clear()
	{
		group.getChildren().clear();
	}

	Pane getPane()
	{
		return paneOuter;
	}
}
