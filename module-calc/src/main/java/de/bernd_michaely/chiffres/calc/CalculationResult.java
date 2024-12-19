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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class to represent the results of a calculation.
 *
 * @author Bernd Michaely
 */
public class CalculationResult extends ApproximationInfo
{
	final int target;
	final SortedSet<Solution> solutions;
	boolean cancelled = false;
	boolean outOfMemory = false;

	CalculationResult(int target)
	{
		this.target = target;
		this.solutions = new TreeSet<>();
	}

	/**
	 * Returns the set of solutions found.
	 *
	 * @return the set of solutions found
	 */
	public SortedSet<Solution> getSolutions()
	{
		return this.solutions;
	}

	/**
	 * Returns true, if an exact solution was found, false otherwise.
	 *
	 * @return true, if an exact solution was found, false otherwise
	 */
	public boolean isExactSolutionFound()
	{
		return this.exactSolutionFound;
	}

	/**
	 * If an exact solution was found, returns 0, otherwise the difference to the
	 * best lower approximation.
	 *
	 * @return the difference to the best lower approximation
	 */
	public int getDiffLess()
	{
		return isExactSolutionFound() ? 0 : this.diffLess;
	}

	/**
	 * Returns the value of the best lower approximation.
	 *
	 * @return the value of the best lower approximation
	 */
	public int getLowerApproximation()
	{
		return this.target - getDiffLess();
	}

	/**
	 * Returns true, if a lower approximation is possible, false otherwise.
	 *
	 * @return true, if a lower approximation is possible
	 */
	public boolean isLowerApproximationPossible()
	{
		return isExactSolutionFound() || (this.diffLess < INITIAL_DIFF_VALUE);
	}

	/**
	 * If an exact solution was found, returns 0, otherwise the difference to the
	 * best upper approximation.
	 *
	 * @return the difference to the best upper approximation
	 */
	public int getDiffGreater()
	{
		return isExactSolutionFound() ? 0 : this.diffGreater;
	}

	/**
	 * Returns the value of the best upper approximation or -1, if an upper
	 * approximation is not possible.
	 *
	 * @return the value of the best upper approximation
	 * @see #isUpperApproximationPossible()
	 */
	public int getUpperApproximation()
	{
		return isUpperApproximationPossible() ? this.target + getDiffGreater() : -1;
	}

	/**
	 * Returns true, if an upper approximation is possible, false otherwise.
	 *
	 * @return true, if an upper approximation is possible
	 */
	public boolean isUpperApproximationPossible()
	{
		return isExactSolutionFound() || (this.diffGreater < INITIAL_DIFF_VALUE);
	}

	/**
	 * Returns the number of filtered redundant solutions.
	 *
	 * @return the number of filtered redundant solutions
	 */
	public int getNumFilteredSolutions()
	{
		return this.numFilteredSolutions;
	}

	/**
	 * Returns the number of recursive method calls.
	 *
	 * @return the number of recursive method calls
	 */
	public long getCounterRecursionCalls()
	{
		return this.counterRecursionCalls;
	}

	/**
	 * Returns true, if the calculation thread was canceled. If the return value
	 * is true, the calculation results are incomplete.
	 *
	 * @return true, if the calculation thread was canceled
	 */
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	/**
	 * Returns true, if the calculation was running out of memory. If the return
	 * value is true, all calculation results are deleted to free memory and the
	 * return value of {@link #isCancelled()} will also be true.
	 *
	 * @return true, if the calculation was running out of memory
	 */
	public boolean isOutOfMemory()
	{
		return this.outOfMemory;
	}
}
