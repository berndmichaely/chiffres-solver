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
package de.bernd_michaely.chiffres.fx.table;

import de.bernd_michaely.chiffres.calc.Operation;
import de.bernd_michaely.chiffres.calc.Operator;
import de.bernd_michaely.chiffres.fx.util.MathSymbol;
import java.util.EnumMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static de.bernd_michaely.chiffres.calc.Operator.*;
import static de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign.*;

/**
 * A TableCell to display Operations.
 *
 * @author Bernd Michaely
 */
class OperationTableCell extends TableCell<SolutionRow, Operation> implements SolutionDisplayTableCell
{
	private static final double SYMBOL_SIZE = MathSymbol.DEFAULT_FONT_SIZE * 5 / 6;
	private static final Color COLOR_BG_OPERATOR_ADD = Color.rgb(255, 255, 204);
	private static final Color COLOR_BG_OPERATOR_SUB = Color.rgb(255, 204, 204);
	private static final Color COLOR_BG_OPERATOR_MUL = Color.rgb(204, 255, 230);
	private static final Color COLOR_BG_OPERATOR_DIV = Color.rgb(204, 230, 255);
	private static final Color COLOR_FG_OPERAND_INITIAL = Color.rgb(204, 0, 0);
	private static final Color COLOR_FG_OPERAND_CALCULATED = Color.rgb(0, 0, 204);
	private final Text textOp1 = new Text();
	private final MathSymbol symbolOperator;
	private final Text textOp2 = new Text();
	private final MathSymbol symbolEquals;
	private final Text textRes = new Text();
	private final HBox hBox;
	private static final EnumMap<Operator, Background> mapBackgrounds =
		new EnumMap<>(Operator.class);

	static
	{
		mapBackgrounds.put(ADD, new Background(new BackgroundFill(COLOR_BG_OPERATOR_ADD, null, null)));
		mapBackgrounds.put(SUB, new Background(new BackgroundFill(COLOR_BG_OPERATOR_SUB, null, null)));
		mapBackgrounds.put(MUL, new Background(new BackgroundFill(COLOR_BG_OPERATOR_MUL, null, null)));
		mapBackgrounds.put(DIV, new Background(new BackgroundFill(COLOR_BG_OPERATOR_DIV, null, null)));
	}

	OperationTableCell()
	{
		this.symbolOperator = new MathSymbol(SYMBOL_SIZE);
		this.symbolEquals = new MathSymbol(SYMBOL_SIZE);
		this.symbolEquals.setSign(EQUALS_SIGN);
		this.hBox = new HBox(this.textOp1, this.symbolOperator.getNode(),
			this.textOp2, this.symbolEquals.getNode(), this.textRes);
		this.hBox.setAlignment(Pos.CENTER);
		this.hBox.setPadding(new Insets(2));
		this.hBox.setSpacing(4);
	}

	@Override
	protected void updateItem(Operation item, boolean empty)
	{
		super.updateItem(item, empty);
		if (empty || (item == null))
		{
			setText(null);
			setGraphic(null);
			setBackground(BACKGROUND_TABLECELL_EMPTY);
		}
		else
		{
			setGraphic(this.hBox);
			setPadding(Insets.EMPTY);
			this.textOp1.setText("" + item.getOperand1().getValue());
			this.textOp1.setFill(item.isOp1Calculated() ?
				COLOR_FG_OPERAND_CALCULATED : COLOR_FG_OPERAND_INITIAL);
			switch (item.getOperator())
			{
				case ADD:
					this.symbolOperator.setSign(PLUS_SIGN);
					break;
				case SUB:
					this.symbolOperator.setSign(MINUS_SIGN);
					break;
				case MUL:
					this.symbolOperator.setSign(MULTIPLICATION_SIGN);
					break;
				case DIV:
					this.symbolOperator.setSign(DIVISION_SIGN);
					break;
				default:
					throw new AssertionError("Invalid Symbol");
			}
			this.textOp2.setText("" + item.getOperand2().getValue());
			this.textOp2.setFill(item.isOp2Calculated() ?
				COLOR_FG_OPERAND_CALCULATED : COLOR_FG_OPERAND_INITIAL);
			this.textRes.setText("" + item.getValue());
			this.hBox.setBackground(mapBackgrounds.get(item.getOperator()));
		}
	}
}
