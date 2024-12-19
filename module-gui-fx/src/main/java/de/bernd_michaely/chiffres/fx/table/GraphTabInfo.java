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

/**
 * Class for user data associated with graph tabs.
 *
 * @author Bernd Michaely
 */
class GraphTabInfo
{
	private int solutionRowIndex;
	private final boolean fixed;

	/**
	 * Constructor to create a tab info for the single graph tab representing the
	 * table view single row selection.
	 */
	GraphTabInfo()
	{
		this.fixed = false;
	}

	/**
	 * Constructor to create a tab info for graph tabs with a fixed solution.
	 *
	 * @param solutionRowIndex
	 */
	GraphTabInfo(int solutionRowIndex)
	{
		this.solutionRowIndex = solutionRowIndex;
		this.fixed = true;
	}

	boolean isFixed()
	{
		return this.fixed;
	}

	int getSolutionRowIndex()
	{
		return this.solutionRowIndex;
	}

	void setSolutionRowIndex(int solutionRowIndex)
	{
		if (!this.fixed)
		{
			this.solutionRowIndex = solutionRowIndex;
		}
	}
}
