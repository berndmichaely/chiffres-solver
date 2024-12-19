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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

/**
 * A TableCell to display an index column.
 *
 * @author Bernd Michaely
 */
class IndexTableCell extends TableCell<SolutionRow, Number> implements SolutionDisplayTableCell
{
	@Override
	protected void updateItem(Number item, boolean empty)
	{
		super.updateItem(item, empty);
//		System.out.println("IndexTableCell is " + (empty ? "EMPTY" : "NOT EMPTY") +
//			" - item is " + ((item != null) ? "\"" + item + "\"" : "NULL"));
		if (empty || (item == null))
		{
			setText(null);
			setGraphic(null);
			setBackground(BACKGROUND_TABLECELL_EMPTY);
		}
		else
		{
			setBackground(null);
			setTextFill(getTableRow().getTextFill());
			setAlignment(Pos.CENTER_RIGHT);
			setPadding(new Insets(2, 8, 2, 8));
			final int index = item.intValue() + 1;
			setText("" + index);
		}
	}
}
