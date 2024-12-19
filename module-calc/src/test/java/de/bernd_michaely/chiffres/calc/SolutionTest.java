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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for the Solution class.
 *
 * @author Bernd Michaely
 */
public class SolutionTest
{
	private String getBooleanString(boolean value)
	{
		return value ? "YES" : "NO";
	}

	private void checkSolution(boolean expected, Solution solution)
	{
		final boolean actual = solution.isRedundant();
		System.out.println();
		System.out.println("=========== Solution ===========" + solution);
		System.out.println("^^^^^^^^^^^^ is Redundant : " + getBooleanString(actual) +
			" (expected : " + getBooleanString(expected) + ")");
		assertEquals(expected, actual);
	}

	@Test
	public void testStringConstructor1()
	{
		final Solution solution;
		try
		{
			solution = new Solution("@1*3,1+1");
			System.out.println(solution);
			fail("Expected an IllegalArgumentException to be thrown!");
		}
		catch (IllegalArgumentException exception)
		{
			final String expected = "Invalid forward reference @1 in expression @1*3 at index 0";
			System.out.println("Expected : " + expected);
			assertEquals(exception.getMessage(), expected);
		}
	}

	@Test
	public void testStringConstructor2()
	{
		final Solution solution;
		try
		{
			solution = new Solution("25*10,4*@2,1+1");
			System.out.println(solution);
			fail("Expected an IllegalArgumentException to be thrown!");
		}
		catch (IllegalArgumentException exception)
		{
			final String expected = "Invalid forward reference @2 in expression 4*@2 at index 1";
			System.out.println("Expected : " + expected);
			assertEquals(exception.getMessage(), expected);
		}
	}

	@Test
	public void testNoRedundancy_SingleOp()
	{
		checkSolution(false, new Solution("100+25"));
	}

	@Test
	public void testNoRedundancy_MultipleOps()
	{
		checkSolution(false, new Solution("3*3,@0*3,@1*3,@2*3,@3*3"));
	}

	@Test
	public void testRedundancy_UnusedOp()
	{
		checkSolution(true, new Solution("5-3,100+25"));
	}

	@Test
	public void testRedundancy_IdentityOp_ChainStart()
	{
		// Note, that the first operand of the second operation
		// is marked to be calculated and therefore refers to the result of the
		// first operation, and not to the given operand
		checkSolution(true, new Solution("100/1,@0+25"));
	}

	@Test
	public void testRedundancy_IdentityOp_ChainMiddle()
	{
		checkSolution(true, new Solution("100+50,@0/1,@1+25"));
	}

	@Test
	public void testRedundancy_IdentityOp_ChainEnd()
	{
		checkSolution(true, new Solution("100+50,@0+25,@1/1"));
	}

	@Test
	public void testRedundancy_Loop_ChainStart()
	{
		// Note, that the first operand of the last operation
		// is marked to be calculated and therefore refers to the result of the
		// previous operation, and not to the given operand
		checkSolution(true, new Solution("100+10,@0-10,@1+50"));
	}

	@Test
	public void testRedundancy_Loop_ChainMiddle()
	{
		checkSolution(true, new Solution("100+50,@0+10,@1-10,@2*2"));
	}

	@Test
	public void testRedundancy_Loop_ChainEnd()
	{
		checkSolution(true, new Solution("100+50,@0+10,@1-10"));
	}

	@Test
	public void testRedundancy_Loop_3_Ops()
	{
		// Note, that the first operand of the last operation
		// is marked to be calculated and therefore refers to the result of the
		// previous operation, and not to the given operand
		checkSolution(true, new Solution("10*6,@0/3,@1-10,@2+75"));
	}
}
