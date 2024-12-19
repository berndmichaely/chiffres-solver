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

import java.util.stream.DoubleStream;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/**
 * Class to create an icon for an options toolbar button.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class IconOptions
{
	private final Pane node;

	public IconOptions()
	{
		final double size = MathSymbol.DEFAULT_FONT_SIZE;
		this.node = new Pane();
		this.node.setMinSize(size, size);
		this.node.setPrefSize(size, size);
		this.node.setMaxSize(size, size);
		final double strokeWidth = size / 10;
		final double x1 = size * 0.2;
		final double x2 = size * 0.8;
		DoubleStream.of(0.2, 0.5, 0.8).forEach(d ->
		{
			final double y = size * d;
			final Line line = new Line(x1, y, x2, y);
			line.setStrokeWidth(strokeWidth);
			this.node.getChildren().add(line);
		});
	}

	public Node getNode()
	{
		return node;
	}
}
