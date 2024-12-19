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

import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Concrete builder for bitmap based math symbol graphics.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class CanvasMathSymbolBuilder extends MathSymbolBuilder
{
	private final GraphicsContext graphicsContext;
	private final double offsetX, offsetY;

	public CanvasMathSymbolBuilder(GraphicsContext graphicsContext,
		double fontSize, Sign sign, double offsetX, double offsetY)
	{
		this.graphicsContext = graphicsContext;
		setFontSize(fontSize);
		setSign(sign);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	protected void buildCircle(double centerX, double centerY, double radius)
	{
		graphicsContext.setFill(Color.BLACK);
		final double d = 2 * radius;
		graphicsContext.fillOval(offsetX + centerX - radius, offsetY + centerY - radius, d, d);
	}

	@Override
	protected void buildLine(double startX, double startY, double endX, double endY)
	{
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(getFontSize() / 7);
		graphicsContext.strokeLine(offsetX + startX, offsetY + startY,
			offsetX + endX, offsetY + endY);
	}
}
