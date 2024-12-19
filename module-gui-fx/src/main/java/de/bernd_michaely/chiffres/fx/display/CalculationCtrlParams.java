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
 * Class to encapsulate the resulting parameters edited in a CalculationDisplay.
 *
 * @author Bernd Michaely
 */
public class CalculationCtrlParams
{
	private final int target;
	private final int[] operands;
	private final int numThreads;
	private final boolean showingIntermediateResult;

	public CalculationCtrlParams(int target, CalculationCtrlParams params)
	{
		this(params.isShowingIntermediateResult(),
			params.getNumThreads(), target, params.getOperands());
	}

	public CalculationCtrlParams(boolean showingIntermediateResult,
		int numThreads, int target, int... operands)
	{
		this.showingIntermediateResult = showingIntermediateResult;
		this.numThreads = numThreads;
		this.target = target;
		this.operands = operands;
	}

	/**
	 * Returns the calculation target.
	 *
	 * @return the calculation target
	 */
	public int getTarget()
	{
		return this.target;
	}

	/**
	 * Returns the number of operands.
	 *
	 * @return the number of operands
	 */
	public int getNumOperands()
	{
		return (this.operands != null) ? this.operands.length : 0;
	}

	/**
	 * Returns the operands entered by the user.
	 *
	 * @return the operands entered by the user
	 */
	public int[] getOperands()
	{
		return this.operands;
	}

	/**
	 * Returns the operand at the given index.
	 *
	 * @param index the given index
	 * @return the operand at the given index
	 */
	public int getOperand(int index)
	{
		return ((this.operands != null) && (index >= 0) && (index < this.operands.length)) ?
			this.operands[index] : 0;
	}

	/**
	 * Returns the number of threads to use for calculation.
	 *
	 * @return the number of threads to use for calculation
	 */
	public int getNumThreads()
	{
		return numThreads;
	}

	/**
	 * Returns true, if the solution display should show intermediate results as
	 * soon as they are found.
	 *
	 * @return true, if the solution display should show intermediate results
	 *         immediately
	 */
	public boolean isShowingIntermediateResult()
	{
		return this.showingIntermediateResult;
	}
}
