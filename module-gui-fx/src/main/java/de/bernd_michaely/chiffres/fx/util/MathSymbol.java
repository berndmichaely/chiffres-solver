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
package de.bernd_michaely.chiffres.fx.util;

import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder;
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import de.bernd_michaely.chiffres.graphics.MathSymbolDirector;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

/**
 * A class to create various math symbols.
 *
 * @author Bernd Michaely
 */
public class MathSymbol
{
	/**
	 * The default font size.
	 */
	public static final double DEFAULT_FONT_SIZE = Font.getDefault().getSize();
	private final MathSymbolDirector director;
	private final ShapeBuilder builder;

	private static class ShapeBuilder extends MathSymbolBuilder
	{
		private final Group group;
		private final Pane pane;

		private ShapeBuilder()
		{
			this.group = new Group();
			this.pane = new Pane(this.group);
		}

		@Override
		protected void buildLine(double startX, double startY, double endX, double endY)
		{
			this.group.getChildren().add(new Line(startX, startY, endX, endY));
		}

		@Override
		protected void buildCircle(double centerX, double centerY, double radius)
		{
			this.group.getChildren().add(new Circle(centerX, centerY, radius));
		}
	}

	/**
	 * Creates a symbol factory instance with a default font size.
	 */
	public MathSymbol()
	{
		this(DEFAULT_FONT_SIZE);
	}

	/**
	 * Creates a symbol factory instance with the given font size.
	 *
	 * @param fontSize the given font size
	 */
	public MathSymbol(double fontSize)
	{
		this.builder = new ShapeBuilder();
		this.director = new MathSymbolDirector(this.builder);
		setFontSize(fontSize);
	}

	public double getFontSize()
	{
		return builder.getFontSize();
	}

	/**
	 * Set the font size.
	 *
	 * @param fontSize the font size
	 */
	public void setFontSize(double fontSize)
	{
		if ((fontSize > 0) && (fontSize != builder.getFontSize()))
		{
			builder.setFontSize(fontSize);
			builder.pane.setMinSize(fontSize, fontSize);
			builder.pane.setPrefSize(fontSize, fontSize);
			builder.pane.setMaxSize(fontSize, fontSize);
			update();
		}
	}

	public Sign getSign()
	{
		return builder.getSign();
	}

	/**
	 * Set the symbol to display.
	 *
	 * @param sign the symbol to display
	 */
	public void setSign(Sign sign)
	{
		if (sign != builder.getSign())
		{
			builder.setSign(sign);
			update();
		}
	}

	private void update()
	{
		builder.group.getChildren().clear();
		director.construct();
	}

	/**
	 * Returns the node to draw the symbol.
	 *
	 * @return the node to draw the symbol
	 */
	public Node getNode()
	{
		return builder.pane;
	}
}
