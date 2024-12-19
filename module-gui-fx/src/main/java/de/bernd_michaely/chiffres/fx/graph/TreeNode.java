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

import de.bernd_michaely.chiffres.graphics.AbstractTreeNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import static de.bernd_michaely.chiffres.graphics.SolutionGraphDirector.getOffsetShadow;

/**
 * Class to represent a node in a SolutionGraph.
 *
 * @author Bernd Michaely
 */
abstract class TreeNode implements AbstractTreeNode
{
	private final DoubleProperty _x_;
	private final DoubleProperty _y_;
	private final TreeNode treeNodeParent;
	private final DropShadow dropShadow;
	private final Pane node;
	private final Line edgeParent;

	protected TreeNode(double x, double y, double width, double height,
		double fontSize, TreeNode treeNodeParent)
	{
		this.dropShadow = new DropShadow();
		final double offsetShadow = getOffsetShadow(fontSize);
		dropShadow.setOffsetX(offsetShadow);
		dropShadow.setOffsetY(offsetShadow);
		this.treeNodeParent = treeNodeParent;
		if (isRootNode())
		{
			_x_ = new SimpleDoubleProperty();
			_y_ = new SimpleDoubleProperty();
			this.edgeParent = null;
		}
		else
		{
			this._x_ = null;
			this._y_ = null;
			this.edgeParent = new Line();
			this.edgeParent.setEffect(getDropShadow());
			final TreeNode tnp = getTreeNodeParent();
			this.edgeParent.endXProperty().bind(tnp.xProperty());
			this.edgeParent.endYProperty().bind(tnp.yProperty());
		}
		setX(x);
		setY(y);
		this.node = new StackPane();
		getNode().setPadding(Insets.EMPTY);
		getNode().setPrefSize(width, height);
		getNode().translateXProperty().bind(
			xProperty().add(getNode().widthProperty().multiply(-0.5)));
		getNode().translateYProperty().bind(
			yProperty().add(getNode().heightProperty().multiply(-0.5)));
	}

	protected Pane getNode()
	{
		return this.node;
	}

	protected DropShadow getDropShadow()
	{
		return dropShadow;
	}

	@Override
	public TreeNode getTreeNodeParent()
	{
		return this.treeNodeParent;
	}

	protected Line getEdgeParent()
	{
		return this.edgeParent;
	}

	public DoubleProperty xProperty()
	{
		return isRootNode() ? this._x_ : edgeParent.startXProperty();
	}

	public double getX()
	{
		return xProperty().getValue();
	}

	public void setX(double x)
	{
		xProperty().setValue(x);
	}

	public DoubleProperty yProperty()
	{
		return isRootNode() ? this._y_ : edgeParent.startYProperty();
	}

	public double getY()
	{
		return yProperty().getValue();
	}

	public void setY(double y)
	{
		yProperty().setValue(y);
	}
}
