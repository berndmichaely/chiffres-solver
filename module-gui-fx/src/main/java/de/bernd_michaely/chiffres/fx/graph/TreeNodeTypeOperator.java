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

import de.bernd_michaely.chiffres.fx.util.MathSymbol;
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Tree node type for operators.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class TreeNodeTypeOperator extends TreeNode
{
	TreeNodeTypeOperator(double x, double y, double width, double height,
		double fontSize, Sign symbol, Color color, TreeNode treeNodeParent)
	{
		super(x, y, width, height, fontSize, treeNodeParent);
		final Shape shape = new Circle(height / 2, color);
		shape.setEffect(getDropShadow());
		final MathSymbol mathSymbol = new MathSymbol(fontSize);
		mathSymbol.setSign(symbol);
		getNode().getChildren().addAll(shape, mathSymbol.getNode());
	}
}
