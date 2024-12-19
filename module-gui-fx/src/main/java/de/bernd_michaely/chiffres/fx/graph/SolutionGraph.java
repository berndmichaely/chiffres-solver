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

import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.fx.display.DisplayComponent;

/**
 * Interface to describe a component to display a graphic of a solution tree.
 *
 * @author Bernd Michaely
 */
public interface SolutionGraph extends DisplayComponent
{
	/**
	 * Returns the solution to display.
	 *
	 * @return the solution to display
	 */
	Solution getSolution();

	/**
	 * Sets the solution to display.
	 *
	 * @param solution the solution to display
	 */
	void setSolution(Solution solution);
}
