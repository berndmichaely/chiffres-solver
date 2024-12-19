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
package de.bernd_michaely.chiffres.fx.display;

import de.bernd_michaely.chiffres.fx.mainwindow.TabReference;
import java.util.function.BiConsumer;
import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Super-interface for misc types of displays for calculation parameter editing.
 *
 * @author Bernd Michaely
 */
public interface CalculationDisplay extends CalculationCtrl
{
	int MIN_WIDTH = 500;
	int MIN_HEIGHT = 333;

	/**
	 * Set action to trigger for starting the calculation.
	 *
	 * @param tabReference a WeakReference to the tab to pass through
	 * @param onStart      action to trigger for starting the calculation
	 */
	void setOnStart(TabReference tabReference, BiConsumer<TabReference, CalculationCtrlParams> onStart);

	/**
	 * A property to indicate, if editing of operands is complete and cloning is
	 * possible.
	 *
	 * @return a property indicating, if editing of operands is complete
	 */
	ReadOnlyBooleanProperty operandArrayCompleteProperty();

	/**
	 * Return the maximum number of digits for operands.
	 *
	 * @return the maximum number of digits for operands
	 */
	int getMaxNumDigitsOperand();

	/**
	 * Return the maximum number of digits for the target.
	 *
	 * @return the maximum number of digits for the target
	 */
	int getMaxNumDigitsTarget();

	/**
	 * Requests a control to be focused when this display is initially shown.
	 */
	void requestInitalFocus();
}
