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
 * Class to represent a solution candidate. This is a solution combined with its
 * equivalence class which is pre-calculated in the worker thread producing the
 * solution for performance reasons.
 *
 * @author Bernd Michaely
 */
public class SolutionCandidate
{
	final Solution solution;
	final Solution.EquivalenceClass equivalenceClass;

	SolutionCandidate(Solution solution)
	{
		this.solution = solution;
		// pre-calculate EquivalenceClass in worker thread:
		this.equivalenceClass = new Solution.EquivalenceClass(solution);
	} // pre-calculate EquivalenceClass in worker thread:

	public Solution getSolution()
	{
		return this.solution;
	}

	public Solution.EquivalenceClass getEquivalenceClass()
	{
		return this.equivalenceClass;
	}
}
