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

import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * A TableCell to show and edit a selection status.
 *
 * @author Bernd Michaely
 */
class SelectionTableCell extends CheckBoxTableCell<SolutionRow, Boolean> implements SolutionDisplayTableCell
{
	SelectionTableCell(Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
	{
		super(getSelectedProperty);
	}

	@Override
	public void updateItem(Boolean item, boolean empty)
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
			setBackground(null);
		}
	}
}
