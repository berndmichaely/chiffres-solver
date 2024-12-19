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
 * Immutable class to represent an operand.
 *
 * @author Bernd Michaely
 */
public class Operand
{
	protected final int value;

	Operand(int value)
	{
		this.value = value;
	}

	/**
	 * Returns the operand value.
	 *
	 * @return the operand value
	 */
	public int getValue()
	{
		return this.value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Operand) ?
			(this.value == ((Operand) obj).value) : false;
	}

	@Override
	public int hashCode()
	{
		return this.value;
	}

	@Override
	public String toString()
	{
		return "" + this.value;
	}
}
