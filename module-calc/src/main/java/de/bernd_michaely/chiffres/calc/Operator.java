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
 * Enumeration representing basic arithmetic operations.
 *
 * @author Bernd Michaely
 */
public enum Operator
{
	ADD
	{
		@Override
		public String toString()
		{
			return "+";
		}

		@Override
		public int calculate(int operand1, int operand2)
		{
			final long value = (long) operand1 + (long) operand2;
			return (value <= Integer.MAX_VALUE) ? (int) value : 0;
		}
	},
	SUB
	{
		@Override
		public String toString()
		{
			return "-";
		}

		@Override
		public int calculate(int operand1, int operand2)
		{
			return operand1 - operand2;
		}
	},
	MUL
	{
		@Override
		public String toString()
		{
			return "*";
		}

		@Override
		public int calculate(int operand1, int operand2)
		{
			final long value = (long) operand1 * (long) operand2;
			return (value <= Integer.MAX_VALUE) ? (int) value : 0;
		}
	},
	DIV
	{
		@Override
		public String toString()
		{
			return "/";
		}

		@Override
		public int calculate(int operand1, int operand2)
		{
			if (operand2 == 0)
			{
				return 0;
			}
			return (operand1 % operand2 == 0) ? operand1 / operand2 : 0;
		}
	};

	/**
	 * Performs a basic arithmetic operation. Returns the result of applying the
	 * binary operator represented by the enumeration value to the given operands.
	 * If an int overflow or division by zero occurs, zero is returned. A return
	 * value of zero will signal an invalid or uninteresting result to the
	 * calculation algorithm.
	 *
	 * @param operand1 first operand
	 * @param operand2 second operand
	 * @return the result of applying the binary operator represented by the
	 *         enumeration value to the operands, zero for invalid operations
	 */
	public abstract int calculate(int operand1, int operand2);
}
