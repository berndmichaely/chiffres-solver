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

import java.util.Objects;

/**
 * Immutable class to represent a single basic arithmetic operation.
 */
public class Operation extends Operand implements Comparable<Operation>
{
	// fields have package access for performance reasons
	final Operand operand1, operand2;
	final Operator operator;

	Operation(Operand operand1, Operator operator, Operand operand2, int result)
	{
		super(result);
		if (operand1 == null)
		{
			throw new IllegalArgumentException("operand1 is null");
		}
		if (operator == null)
		{
			throw new IllegalArgumentException("operator is null");
		}
		if (operand2 == null)
		{
			throw new IllegalArgumentException("operand2 is null");
		}
		this.operand1 = operand1;
		this.operator = operator;
		this.operand2 = operand2;
	}

	/**
	 * Returns the first operand.
	 *
	 * @return the first operand
	 */
	public Operand getOperand1()
	{
		return this.operand1;
	}

	/**
	 * Returns true, if operand1 was calculated and false, if it was originally
	 * given.
	 *
	 * @return true, if operand1 was calculated and false, if it was originally
	 * given
	 */
	public boolean isOp1Calculated()
	{
		return this.operand1 instanceof Operation;
	}

	/**
	 * Returns the second operand.
	 *
	 * @return the second operand
	 */
	public Operator getOperator()
	{
		return this.operator;
	}

	/**
	 * Returns the second operand.
	 *
	 * @return the second operand
	 */
	public Operand getOperand2()
	{
		return this.operand2;
	}

	/**
	 * Returns true, if operand2 was calculated and false, if it was originally
	 * given.
	 *
	 * @return true, if operand2 was calculated and false, if it was originally
	 * given
	 */
	public boolean isOp2Calculated()
	{
		return this.operand2 instanceof Operation;
	}

	/**
	 * Return a String-description of the operation.
	 *
	 * @return a String-description of the operation
	 */
	@Override
	public String toString()
	{
		return String.format("%d %s %d = %d",
			this.operand1.value, this.operator, this.operand2.value, this.value);
	}

	@Override
	public int compareTo(Operation other)
	{
		if (other == null)
		{
			return 1;
		}
		final int comp1 = this.operator.compareTo(other.operator);
		if (comp1 != 0)
		{
			return comp1;
		}
		else
		{
			final int comp2 = Integer.compare(this.operand1.value, other.operand1.value);
			if (comp2 != 0)
			{
				return comp2;
			}
			else
			{
				return Integer.compare(this.operand2.value, other.operand2.value);
			}
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Operation) ?
			compareTo((Operation) obj) == 0 : false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.operand1, this.operator, this.operand2);
	}
}
