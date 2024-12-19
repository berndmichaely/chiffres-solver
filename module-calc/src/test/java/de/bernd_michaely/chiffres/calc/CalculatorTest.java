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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for the Calculator class.
 *
 * @author Bernd Michaely
 */
@LongRunningTest
public class CalculatorTest
{
	/**
	 * Check a (possibly incomplete) list of solutions for a given calculation.
	 *
	 * @param calculationParams the given calculation
	 * @param solutions         a (possibly incomplete) list of solutions
	 */
	private void checkSolutions(CalculationParams calculationParams, Solution... solutions)
	{
		System.out.println();
		System.out.println("*** Check Calculation : " + calculationParams);
		for (Solution solution : solutions)
		{
			System.out.println();
			System.out.println("^^^ Check Solution    : " + solution);
			assertTrue(new Calculator(calculationParams).call().getSolutions().contains(solution));
		}
	}

	private static boolean compareSolver(CalculationResult result1, CalculatorST.ResultInfo result2, boolean testMode)
	{
		final boolean isExactSolutionFound = result1.isExactSolutionFound() == result2.isExactSolutionFound();
		final boolean lowerApproximation = result1.getLowerApproximation() == result2.getLowerApproximation();
		final boolean diffLess = result1.getDiffLess() == result2.getDiffLess();
		final boolean isUpperApproximationPossible = result1.isUpperApproximationPossible() == result2.isUpperApproximationPossible();
		final boolean upperApproximation = result1.getUpperApproximation() == result2.getUpperApproximation();
		final boolean diffGreater = result1.getDiffGreater() == result2.getDiffGreater();
		final boolean solutions = result1.getSolutions().equals(result2.getSolutions());
		final boolean result = isExactSolutionFound && lowerApproximation &&
			diffLess && isUpperApproximationPossible && upperApproximation &&
			diffGreater && solutions;
		assertTrue(isExactSolutionFound, "ERROR : isExactSolutionFound");
		assertTrue(lowerApproximation, "ERROR : lowerApproximation");
		assertTrue(diffLess, "ERROR : diffLess");
		assertTrue(isUpperApproximationPossible, "ERROR : isUpperApproximationPossible");
		assertTrue(upperApproximation, "ERROR : upperApproximation");
		assertTrue(diffGreater, "ERROR : diffGreater");
		assertTrue(solutions, "ERROR : solutions");
		assertTrue(result1.getCounterRecursionCalls() > 0);
		if (!testMode)
		{
			assertEquals(result1.getCounterRecursionCalls(), result2.getCounterRecursionCalls());
		}
		return result;
	}

	static boolean testCompareSolver(CalculationParams calculationParams,
		boolean modeParallel, boolean testMode)
	{
		System.out.println();
		System.out.print("*** Compare solver for : " + calculationParams + " in " +
			(modeParallel ? "PARALLEL" : "NON parallel") +
			(testMode ? " TEST mode" : " mode"));
		final Calculator calculator1 = new Calculator(calculationParams);
		calculator1.setModeParallel(modeParallel);
		calculator1.setTestMode(testMode);
		final CalculatorST calculator2 = new CalculatorST(calculationParams, null);
		final CalculationResult result1 = calculator1.call();
		calculator2.run();
		final CalculatorST.ResultInfo result2 = calculator2.getResultInfo();
		final boolean result = compareSolver(result1, result2, testMode);
		System.out.println(" ... " + (result ? "OK" : "ERROR"));
		return result;
	}

	private boolean testCompareSolver(CalculationParams calculationParams)
	{
		return testCompareSolver(calculationParams, false, false) &&
			testCompareSolver(calculationParams, true, false);
	}

	@Test
	public void testCalculator_001()
	{
		checkSolutions(new CalculationParams(1, 1, 1),
			// new Solution("1*1"), new Solution("1/1")
			// are both redundant
			new Solution[0]);
	}

	@Test
	public void testCalculator_002()
	{
		checkSolutions(new CalculationParams(2, 1, 1),
			new Solution("1+1"));
	}

	@Test
	public void testCalculator_003()
	{
		checkSolutions(new CalculationParams(4, 2, 2),
			new Solution("2+2"), new Solution("2*2"));
	}

	@Test
	public void testCalculator_004()
	{
		checkSolutions(new CalculationParams(7, 1, 2, 4),
			new Solution("4+2,@0+1"), new Solution("2*4,@0-1"));
	}

	@Test
	public void testCalculator_005()
	{
		checkSolutions(new CalculationParams(729, 3, 3, 3, 3, 3, 3),
			new Solution("3*3,@0*3,@1*3,@2*3,@3*3"),
			new Solution("3*3,3*3,3*3,@0*@1,@3*@2")
		);
	}

	@Test
	public void testRedundancy()
	{
		final int target = 101;
		final int[] operands = new int[]
		{
			100, 1, 2, 3, 4, 5
		};
		final Calculator calculator1 = new Calculator(new CalculationParams(target, operands));
		final Calculator calculator2 = new Calculator(new CalculationParams(target, operands));
		calculator1.setTestMode(false);
		// in test mode, the calculator will continue recursion even if a solution
		// is found and thereby produce a lot of additional redundant solutions
		calculator2.setTestMode(true);
		final CalculationResult result1 = calculator1.call();
		final CalculationResult result2 = calculator2.call();
		final int numFilteredSolutions1 = result1.getNumFilteredSolutions();
		final int numFilteredSolutions2 = result2.getNumFilteredSolutions();
		System.out.println();
		System.out.println("testRedundancy() :");
		System.out.println(String.format(
			"calculator (TestMode : ON ) : resultInfo.getNumFilteredSolutions() : %6d",
			numFilteredSolutions2));
		System.out.println(String.format(
			"calculator (TestMode : OFF) : resultInfo.getNumFilteredSolutions() : %6d",
			numFilteredSolutions1));
		System.out.println("                                                                     ------");
		System.out.println(String.format(
			"                                                                     %6d",
			numFilteredSolutions2 - numFilteredSolutions1));
		assertEquals(result1.getSolutions(), result2.getSolutions());
		assertTrue(numFilteredSolutions2 > numFilteredSolutions1);
	}

	@Test
	public void testCompareSolver1()
	{
		assertTrue(testCompareSolver(new CalculationParams(729, 3, 3, 3, 3, 3, 3)));
	}

	@Test
	public void testCompareSolver2()
	{
		assertTrue(testCompareSolver(new CalculationParams(960, 1, 2, 3, 4, 5, 6)));
	}

	@Test
	public void testCompareSolver3()
	{
		assertTrue(testCompareSolver(new CalculationParams(961, 1, 2, 3, 4, 5, 6)));
	}

	@Test
	public void testCompareSolver4()
	{
		assertTrue(testCompareSolver(new CalculationParams(999, 1, 2, 3, 4, 5, 6)));
	}

	@Test
	public void testCompareSolver5()
	{
		assertTrue(testCompareSolver(new CalculationParams(1079, 1, 2, 3, 4, 5, 6)));
	}

	@Test
	public void testCompareSolver6()
	{
		assertTrue(testCompareSolver(new CalculationParams(1080, 1, 2, 3, 4, 5, 6)));
	}

	@Test
	public void testCompareSolver7()
	{
		assertTrue(testCompareSolver(new CalculationParams(999, 1, 1, 2, 2, 3, 3)));
	}

	@Test
	public void testCompareSolver8()
	{
		assertTrue(testCompareSolver(new CalculationParams(321, 9, 8, 7, 6, 5, 4)));
	}

	@Test
	public void testTime_7_Op_Parallel()
	{
		final CalculationParams params = new CalculationParams(9999, 1, 2, 3, 4, 5, 6, 7);
		final Calculator calculator = new Calculator(params);
		calculator.setModeParallel(true);
		System.out.println();
		System.out.println("*** Parallel execution of " + params);
		final CalculationResult result = calculator.call();
		System.out.println("--> # recursion calls : " + result.getCounterRecursionCalls());
	}

	@Test
	public void testTime_7_Op_Non_Parallel()
	{
		final CalculationParams params = new CalculationParams(9999, 1, 2, 3, 4, 5, 6, 7);
		final Calculator calculator = new Calculator(params);
		calculator.setModeParallel(false);
		System.out.println();
		System.out.println("*** Parallel execution (SingleThreadExecutor) of " + params);
		final CalculationResult result = calculator.call();
		System.out.println("--> # recursion calls : " + result.getCounterRecursionCalls());
	}

	@Test
	public void testTime_7_Op_Non_Parallel_1()
	{
		final CalculationParams params = new CalculationParams(9999, 1, 2, 3, 4, 5, 6, 7);
		final CalculatorST calculator = new CalculatorST(params, null);
		System.out.println();
		System.out.println("*** NON parallel execution of " + params);
		calculator.run();
		System.out.println("--> # recursion calls : " +
			calculator.getResultInfo().getCounterRecursionCalls());
	}

//	@Test
	public void testTime_8_Op_Parallel()
	{
		final CalculationParams params = new CalculationParams(98765, 1, 2, 3, 4, 5, 6, 7, 8);
		final Calculator calculator = new Calculator(params);
		calculator.setModeParallel(true);
		System.out.println();
		System.out.println("*** Parallel execution of " + params);
		final CalculationResult result = calculator.call();
		System.out.println("--> # recursion calls : " + result.getCounterRecursionCalls());
	}

//	@Test
	public void testTime_8_Op_Non_Parallel()
	{
		final CalculationParams params = new CalculationParams(98765, 1, 2, 3, 4, 5, 6, 7, 8);
		final Calculator calculator = new Calculator(params);
		calculator.setModeParallel(false);
		System.out.println();
		System.out.println("*** NON parallel execution of " + params);
		final CalculationResult result = calculator.call();
		System.out.println("--> # recursion calls : " + result.getCounterRecursionCalls());
	}

//	@Test
	public void testTime_8_Op_Non_Parallel_1()
	{
		final CalculationParams params = new CalculationParams(98765, 1, 2, 3, 4, 5, 6, 7, 8);
		final CalculatorST calculator = new CalculatorST(params, null);
		System.out.println();
		System.out.println("*** NON parallel execution of " + params);
		calculator.run();
		System.out.println("--> # recursion calls : " +
			calculator.getResultInfo().getCounterRecursionCalls());
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/chiffres-samples.txt", numLinesToSkip = 1)
	public void testRealSampleCalculationModeNonParallel(
		@AggregateWith(CalculationParamAggregator.class) CalculationParams calculationParams)
	{
		assertTrue(testCompareSolver(calculationParams, false, false));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/chiffres-samples.txt", numLinesToSkip = 1)
	public void testRealSampleCalculationModeParallel(
		@AggregateWith(CalculationParamAggregator.class) CalculationParams calculationParams)
	{
		assertTrue(testCompareSolver(calculationParams, true, false));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/chiffres-samples.txt", numLinesToSkip = 1)
	public void testRealSampleCalculationModeParallelAndTest(
		@AggregateWith(CalculationParamAggregator.class) CalculationParams calculationParams)
	{
		assertTrue(testCompareSolver(calculationParams, true, true));
	}
}
