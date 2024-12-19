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

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * First single threaded version of the Calculator class. Being well tested, it
 * is kept for cross-checking newer parallelized versions of the algorithm.
 *
 * @author Bernd Michaely
 * @see Calculator
 */
class CalculatorST implements Runnable
{
	/**
	 * Class to represent the results of a calculation.
	 */
	static class ResultInfo
	{
		private final int target;
		private final SortedSet<Solution> solutions = new TreeSet<>();
		private boolean exactSolutionFound = false;
		private int diffGreater = Integer.MAX_VALUE;
		private int diffLess = Integer.MAX_VALUE;
		private int numFilteredSolutions;
		private long counterRecursionCalls;
		private boolean canceled = false;
		private boolean outOfMemory = false;

		private ResultInfo(int target)
		{
			this.target = target;
		}

		/**
		 * Returns the set of solutions found.
		 *
		 * @return the set of solutions found
		 */
		SortedSet<Solution> getSolutions()
		{
			return this.solutions;
		}

		/**
		 * Returns true, if an exact solution was found, false otherwise.
		 *
		 * @return true, if an exact solution was found, false otherwise
		 */
		boolean isExactSolutionFound()
		{
			return this.exactSolutionFound;
		}

		/**
		 * If an exact solution was found, returns 0, otherwise the difference to
		 * the best lower approximation.
		 *
		 * @return the difference to the best lower approximation
		 */
		int getDiffLess()
		{
			return this.exactSolutionFound ? 0 : this.diffLess;
		}

		/**
		 * Returns the value of the best lower approximation.
		 *
		 * @return the value of the best lower approximation
		 */
		int getLowerApproximation()
		{
			return this.target - getDiffLess();
		}

		/**
		 * If an exact solution was found, returns 0, otherwise the difference to
		 * the best upper approximation.
		 *
		 * @return the difference to the best upper approximation
		 */
		int getDiffGreater()
		{
			return this.exactSolutionFound ? 0 : this.diffGreater;
		}

		/**
		 * Returns the value of the best upper approximation or -1, if an upper
		 * approximation is not possible.
		 *
		 * @return the value of the best upper approximation
		 * @see #isUpperApproximationPossible()
		 */
		int getUpperApproximation()
		{
			return isUpperApproximationPossible() ?
				this.target + getDiffGreater() : -1;
		}

		/**
		 * Returns true, if an upper approximation is possible, false otherwise.
		 *
		 * @return true, if an upper approximation is possible
		 */
		boolean isUpperApproximationPossible()
		{
			return isExactSolutionFound() || (this.diffGreater < Integer.MAX_VALUE);
		}

		/**
		 * Returns the number of filtered redundant solutions.
		 *
		 * @return the number of filtered redundant solutions
		 */
		int getNumFilteredSolutions()
		{
			return this.numFilteredSolutions;
		}

		/**
		 * Returns the number of recursive method calls.
		 *
		 * @return the number of recursive method calls
		 */
		long getCounterRecursionCalls()
		{
			return this.counterRecursionCalls;
		}

		/**
		 * Returns true, if the calculation thread was canceled. If the return value
		 * is true, the calculation results are incomplete.
		 *
		 * @return true, if the calculation thread was canceled
		 */
		boolean isCanceled()
		{
			return this.canceled;
		}

		/**
		 * Returns true, if the calculation was running out of memory. If the return
		 * value is true, all calculation results are deleted to free memory and the
		 * return value of {@link #isCanceled()} will also be true.
		 *
		 * @return true, if the calculation was running out of memory
		 */
		boolean isOutOfMemory()
		{
			return this.outOfMemory;
		}
	}
	private final ResultInfo resultInfo;
	private final CalculationParams calculationParams;
	private final Operand[] operands;
	private final Operation[] operations;
	private final SortedMap<Solution.EquivalenceClass, Solution> equivalenceClasses;
	private final Consumer<ResultInfo> onFinished;
	private final Consumer<Double> onProgress;
	private final double numProgressMax;
	private int counterProgress;
	private boolean testMode;

	/**
	 * Creates a Calculator for a given calculation.
	 *
	 * @param calculationParams the given calculation
	 * @param onFinished a Consumer to handle the results
	 */
	CalculatorST(CalculationParams calculationParams, Consumer<ResultInfo> onFinished)
	{
		this(calculationParams, onFinished, null);
	}

	/**
	 * Creates a Calculator for a given calculation.
	 *
	 * @param calculationParams the given calculation
	 * @param onFinished a Consumer to handle the results
	 * @param onProgress a callback object to notify clients about calculation
	 * progress
	 */
	CalculatorST(CalculationParams calculationParams, Consumer<ResultInfo> onFinished,
		Consumer<Double> onProgress)
	{
		if (calculationParams == null)
		{
			throw new IllegalArgumentException("Calculator : Calculation is null");
		}
		this.calculationParams = calculationParams;
		this.onFinished = onFinished;
		this.onProgress = onProgress;
		this.equivalenceClasses = new TreeMap<>();
		this.resultInfo = new ResultInfo(calculationParams.getTarget());
		final int numOperands = calculationParams.getNumOperands();
		this.numProgressMax = (numOperands * numOperands - numOperands) / 2;
		this.operands = new Operand[numOperands];
		this.operations = new Operation[numOperands - 1];
		for (int i = 0; i < numOperands; i++)
		{
			final int op = calculationParams.getOperand(i);
			this.operands[i] = new Operand(op);
			if (op < 1)
			{
				throw new IllegalArgumentException("Operands must be grater than zero");
			}
		}
	}

	/**
	 * Returns the original calculation parameters.
	 *
	 * @return the original calculation parameters
	 */
	CalculationParams getCalculationParams()
	{
		return this.calculationParams;
	}

	/**
	 * Cancels the calculation, if it is running in a separate thread.
	 *
	 * @see ResultInfo#isCanceled()
	 */
	void cancel()
	{
		this.resultInfo.canceled = true;
	}

	ResultInfo getResultInfo()
	{
		return this.resultInfo;
	}

	boolean isTestMode()
	{
		return this.testMode;
	}

	void setTestMode(boolean testMode)
	{
		this.testMode = testMode;
	}

	@Override
	public void run()
	{
		try
		{
			calculate(this.operands);
			this.resultInfo.solutions.addAll(this.equivalenceClasses.values());
			this.equivalenceClasses.clear();
		}
		catch (OutOfMemoryError ex)
		{
			this.resultInfo.outOfMemory = true;
			this.resultInfo.canceled = true;
			this.resultInfo.solutions.clear();
			this.equivalenceClasses.clear();
		}
		finally
		{
			if (this.onFinished != null)
			{
				this.onFinished.accept(this.resultInfo);
			}
		}
	}

	private void calculate(Operand[] operands)
	{
		if (this.resultInfo.canceled)
		{
			return;
		}
		this.resultInfo.counterRecursionCalls++;
		final int depth = this.operands.length - operands.length;
		final int numOp = operands.length;
		final int numOpDecr = numOp - 1;
		for (int i = 0; i < numOpDecr; i++)
		{
			for (int k = i + 1; k < numOp; k++)
			{
				final Operand op1, op2;
				if (operands[i].value > operands[k].value)
				{
					op1 = operands[i];
					op2 = operands[k];
				}
				else
				{
					op1 = operands[k];
					op2 = operands[i];
				}
				for (Operator operator : Operator.values())
				{
					final int result = operator.calculate(op1.value, op2.value);
					final Operation operation = new Operation(op1, operator, op2, result);
					this.operations[depth] = operation;
					if (result > 0) // intermediate result is valid
					{
						final boolean targetFound = (result == resultInfo.target);
						if (targetFound)
						{
							resultInfo.exactSolutionFound = true;
							final Solution solution = new Solution(depth + 1, this.operations);
							if (solution.isRedundant())
							{
								this.resultInfo.numFilteredSolutions++;
							}
							else
							{
								final Solution.EquivalenceClass equivalenceClass = new Solution.EquivalenceClass(solution);
								final Solution existing = this.equivalenceClasses.get(equivalenceClass);
								// store a canonical representative solution for each
								// equivalence class (e.g. the smallest)
								if ((existing == null) || (solution.compareTo(existing) < 0))
								{
									this.equivalenceClasses.put(equivalenceClass, solution);
								}
							}
						}
						if (!targetFound || this.testMode)
						{
							if (!resultInfo.exactSolutionFound)
							{
								if (result < resultInfo.target)
								{
									final int diff = resultInfo.target - result;
									if (diff < resultInfo.diffLess)
									{
										resultInfo.diffLess = diff;
									}
								}
								else // if (result > resultInfo.target)
								{
									final int diff = result - resultInfo.target;
									if (diff < resultInfo.diffGreater)
									{
										resultInfo.diffGreater = diff;
									}
								}
							}
							if (numOpDecr > 1)
							{
								final Operand[] operandsRecursion = new Operand[numOpDecr];
								for (int m = 0; m < numOpDecr; m++)
								{
									operandsRecursion[m] = (m == i) ? operation :
										((m == k) ? operands[numOpDecr] : operands[m]);
								}
								calculate(operandsRecursion);
							}
						}
					}
				}
				if ((this.onProgress != null) && (depth == 0))
				{
					this.onProgress.accept(++this.counterProgress / this.numProgressMax);
//					try
//					{
//						Thread.sleep(100);
//					}
//					catch (InterruptedException ex)
//					{
//						cancel();
//					}
				}
			}
		}
	}
}
