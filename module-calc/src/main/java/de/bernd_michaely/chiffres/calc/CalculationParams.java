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

import java.util.Arrays;

/**
 * Immutable class to describe the parameters of a calculation.
 *
 * @author Bernd Michaely
 */
public class CalculationParams
{
	private final int target;
	private final int[] operands;

	/**
	 * Creates a new instance.
	 *
	 * @param target   the target value of the calculation
	 * @param operands the operands to use for the calculation
	 */
	public CalculationParams(int target, int... operands)
	{
		this.target = target;
		this.operands = operands.clone();
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
	 * Returns the number of operands of the calculation.
	 *
	 * @return the number of operands
	 */
	public int getNumOperands()
	{
		return this.operands.length;
	}

	/**
	 * Returns the operand at given index.
	 *
	 * @param index the given index
	 * @return the given index
	 * @throws IndexOutOfBoundsException if index is invalid
	 */
	public int getOperand(int index)
	{
		if ((index < 0) || (index >= getNumOperands()))
		{
			throw new IndexOutOfBoundsException(getClass().getName() +
				".getOperand(int) : index " + index + " out of range");
		}
		return this.operands[index];
	}

	@Override
	public String toString()
	{
		return "Calculation (" + this.target + " | " +
			Arrays.toString(this.operands) + ")";
	}
}
