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
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import de.bernd_michaely.chiffres.graphics.MathSymbolDirector;
import de.bernd_michaely.chiffres.graphics.NodeType;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static de.bernd_michaely.chiffres.graphics.SolutionGraphDirector.getOffsetShadow;

/**
 * Concrete solution graph builder for bitmap based output formats.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class CanvasSolutionGraphBuilder extends CanvasSizeProvider
{
	private final double fontSize;
	private Canvas canvas;
	private GraphicsContext graphicsContext;
	private final DropShadow dropShadow;

	/**
	 * Create bitmap based image for solution graph based on given font size.
	 *
	 * @param fontSize the given font size
	 */
	public CanvasSolutionGraphBuilder(double fontSize)
	{
		this(fontSize, true);
	}

	/**
	 * Create bitmap based image for solution graph based on given font size.
	 *
	 * @param fontSize         the given font size
	 * @param createDropShadow set to true to create drop shadows
	 */
	private CanvasSolutionGraphBuilder(double fontSize, boolean createDropShadow)
	{
		this.fontSize = fontSize;
		if (createDropShadow)
		{
			this.dropShadow = new DropShadow();
			final double offsetShadow = getOffsetShadow(fontSize);
			dropShadow.setOffsetX(offsetShadow);
			dropShadow.setOffsetY(offsetShadow);
		}
		else
		{
			this.dropShadow = null;
		}
	}

	@Override
	protected AbstractTreeNode buildNodeTypeOperator(double x, double y, double width, double height,
		double fontSize, Sign symbol, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		return new CanvasTreeNodeTypeOperator(
			x, y, width, height, fontSize, symbol, nodeType, treeNodeParent);
	}

	@Override
	protected AbstractTreeNode buildNodeTypeValue(double x, double y, double width, double height,
		double fontSize, String text, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		return new CanvasTreeNodeTypeValue(
			x, y, width, height, fontSize, text, nodeType, treeNodeParent);
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		final Group group = new Group();
		final Scene scene = new Scene(group, getCanvasWidth(), getCanvasHeight());
		canvas = new Canvas(getCanvasWidth(), getCanvasHeight());
		graphicsContext = canvas.getGraphicsContext2D();
		graphicsContext.setFont(new Font(fontSize));
		group.getChildren().add(canvas);
	}

	@Override
	protected void addEdge(AbstractTreeNode abstractTreeNode)
	{
		if (abstractTreeNode instanceof CanvasTreeNode)
		{
			final CanvasTreeNode node = (CanvasTreeNode) abstractTreeNode;
			final AbstractTreeNode tnp = node.getTreeNodeParent();
			if (tnp instanceof CanvasTreeNode)
			{
				final CanvasTreeNode nodeParent = (CanvasTreeNode) tnp;
				graphicsContext.setFill(Color.BLACK);
				graphicsContext.setStroke(Color.BLACK);
				graphicsContext.setLineWidth(node.getFontSize() / 10);
				graphicsContext.setEffect(dropShadow);
				graphicsContext.strokeLine(node.getX(), node.getY(), nodeParent.getX(), nodeParent.getY());
				graphicsContext.setEffect(null);
			}
		}
	}

	@Override
	protected void addNode(AbstractTreeNode abstractTreeNode)
	{
		if (abstractTreeNode instanceof CanvasTreeNodeTypeValue)
		{
			final CanvasTreeNodeTypeValue node = (CanvasTreeNodeTypeValue) abstractTreeNode;
			graphicsContext.setFill(Color.web(node.getNodeType().getColorCode()));
			graphicsContext.setEffect(dropShadow);
			graphicsContext.fillRoundRect(
				node.getX() - node.getWidth() / 2,
				node.getY() - node.getHeight() / 2,
				node.getWidth(), node.getHeight(),
				node.getWidth() / 3, node.getHeight() / 3);
			graphicsContext.setEffect(null);
			graphicsContext.setTextAlign(TextAlignment.CENTER);
			graphicsContext.setTextBaseline(VPos.CENTER);
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.setStroke(Color.BLACK);
			graphicsContext.setLineWidth(1);
			graphicsContext.fillText(node.getText(), node.getX(), node.getY(), node.getWidth() / 0.9);
		}
		else if (abstractTreeNode instanceof CanvasTreeNodeTypeOperator)
		{
			final CanvasTreeNodeTypeOperator node = (CanvasTreeNodeTypeOperator) abstractTreeNode;
			final double d = node.getHeight();
			final double r = d / 2;
			graphicsContext.setFill(Color.web(node.getNodeType().getColorCode()));
			graphicsContext.setEffect(dropShadow);
			graphicsContext.fillOval(node.getX() - r, node.getY() - r, d, d);
			graphicsContext.setEffect(null);
			if (isCreatingMathSymbolGraphics())
			{
				final double r_2 = r / 2;
				final CanvasMathSymbolBuilder builder = new CanvasMathSymbolBuilder(
					graphicsContext, node.getFontSize(), node.getSymbol(),
					node.getX() - r_2, node.getY() - r_2);
				final MathSymbolDirector director = new MathSymbolDirector(builder);
				director.construct();
			}
			else
			{
				graphicsContext.setTextAlign(TextAlignment.CENTER);
				graphicsContext.setTextBaseline(VPos.CENTER);
				graphicsContext.setFill(Color.BLACK);
				graphicsContext.setStroke(Color.BLACK);
				graphicsContext.setLineWidth(1);
				graphicsContext.fillText("" + node.getSymbol().getUnicodeSymbol(),
					node.getX(), node.getY(), node.getWidth() / 0.9);
			}
		}
	}

	/**
	 * Creates an image of the solution graph. Convenience method for
	 * {@link #createImage(javafx.scene.paint.Paint)}.
	 *
	 * @param transparentFill true to create a transparent background, false to
	 *                        create an opaque white background
	 * @return an image of the solution graph
	 */
	public Image createImage(boolean transparentFill)
	{
		return transparentFill ?
			createImage(Color.web("#ffffff", 0.0)) : createImage(Color.WHITE);
	}

	/**
	 * Creates an image of the solution graph.
	 *
	 * @param fill the background fill
	 * @return an image of the solution graph
	 */
	public Image createImage(Paint fill)
	{
		final SnapshotParameters snapshotParameters = new SnapshotParameters();
		final Rectangle2D rectangle = new Rectangle2D(0, 0, getCanvasWidth(), getCanvasHeight());
		snapshotParameters.setViewport(rectangle);
		snapshotParameters.setFill(fill);
		return canvas.snapshot(snapshotParameters, null);
	}
}
