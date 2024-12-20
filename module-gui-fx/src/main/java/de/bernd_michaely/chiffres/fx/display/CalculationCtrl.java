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

/**
 * Supertype for all display components related to calculation.
 *
 * @author Bernd Michaely
 */
public interface CalculationCtrl extends DisplayComponent
{
	/**
	 * Returns the CalculationCtrlParams.
	 *
	 * @return the CalculationCtrlParams
	 */
	CalculationCtrlParams getCalculationCtrlParams();

	/**
	 * Called, when the component is closed. Can be used to properly clean up
	 * resources like shutting down executer services and so on. Does nothing by
	 * default.
	 */
	default void onClose()
	{
	}
}
