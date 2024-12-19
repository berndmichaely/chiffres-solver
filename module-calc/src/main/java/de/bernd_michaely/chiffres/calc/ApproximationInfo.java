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
package de.bernd_michaely.chiffres.calc;

/**
 * Class to represent the approximation results of a calculation.
 *
 * @author Bernd Michaely
 */
class ApproximationInfo
{
	static final int INITIAL_DIFF_VALUE = Integer.MAX_VALUE;
	int diffGreater;
	int diffLess;
	boolean exactSolutionFound;
	int numFilteredSolutions;
	long counterRecursionCalls;

	ApproximationInfo()
	{
		this.diffGreater = INITIAL_DIFF_VALUE;
		this.diffLess = INITIAL_DIFF_VALUE;
	}

	void combine(ApproximationInfo other)
	{
		if (other != null)
		{
			if (other.diffLess < this.diffLess)
			{
				this.diffLess = other.diffLess;
			}
			if (other.diffGreater < this.diffGreater)
			{
				this.diffGreater = other.diffGreater;
			}
			this.exactSolutionFound |= other.exactSolutionFound;
			this.numFilteredSolutions += other.numFilteredSolutions;
			this.counterRecursionCalls += other.counterRecursionCalls;
		}
	}
}
