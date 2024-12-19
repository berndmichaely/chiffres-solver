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

import java.util.concurrent.Callable;

/**
 * Class representing a subtask of the parallelized main algorithm.
 *
 * @author Bernd Michaely
 */
class SubTask implements Callable<SubTaskResult>
{
	private final TaskGlobalData taskGlobalData;
	private final Operand[] operandsInitial;
	private final Operation[] operations;
	private final SubTaskResult subTaskResult;

	/**
	 * Constructor for main task.
	 *
	 * @param taskGlobalData data global to all tasks
	 * @param operands the initial operands
	 */
	SubTask(TaskGlobalData taskGlobalData, Operand[] operands)
	{
		this(taskGlobalData, operands, new Operation[operands.length - 1]);
	}

	/**
	 * Constructor for subtasks.
	 *
	 * @param taskGlobalData data global to all tasks
	 * @param operands the remaining operands
	 * @param operations the already calculated operations
	 */
	private SubTask(TaskGlobalData taskGlobalData, Operand[] operands,
		Operation[] operations)
	{
		this.taskGlobalData = taskGlobalData;
		this.operandsInitial = operands;
		this.operations = operations;
		this.subTaskResult = new SubTaskResult();
	}

	@Override
	public SubTaskResult call()
	{
		calculate(this.operandsInitial);
		return this.subTaskResult;
	}

	private void calculate(Operand[] operands)
	{
		this.subTaskResult.counterRecursionCalls++;
		final int depth = this.taskGlobalData.numOperands - operands.length;
		final int numOp = operands.length;
		final int numOpDecr = numOp - 1;
		for (int i = 0; i < numOpDecr; i++)
		{
			for (int k = i + 1; !this.taskGlobalData.cancelled && (k < numOp); k++)
			{
				final Operand op1;
				final Operand op2;
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
					if (result > 0)
					{
						final boolean targetFound = result == this.taskGlobalData.target;
						if (targetFound)
						{
							this.subTaskResult.exactSolutionFound = true;
							final Solution solution = new Solution(depth + 1, this.operations);
							if (solution.isRedundant())
							{
								this.subTaskResult.numFilteredSolutions++;
							}
							else
							{
								final SolutionCandidate solutionCandidate = new SolutionCandidate(solution);
								if (this.taskGlobalData.onIntermediateResult != null)
								{
									this.taskGlobalData.onIntermediateResult.accept(solutionCandidate);
								}
								this.subTaskResult.solutionCandidates.add(solutionCandidate);
							}
						}
						if (!targetFound || this.taskGlobalData.testMode)
						{
							if (!this.subTaskResult.exactSolutionFound)
							{
								if (result < this.taskGlobalData.target)
								{
									final int diff = this.taskGlobalData.target - result;
									if (diff < this.subTaskResult.diffLess)
									{
										this.subTaskResult.diffLess = diff;
									}
								}
								else // if (result > this.taskGlobalData.target)
								{
									final int diff = result - this.taskGlobalData.target;
									if (diff < this.subTaskResult.diffGreater)
									{
										this.subTaskResult.diffGreater = diff;
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
								if (depth == Calculator.THRESHOLD_DEPTH_FORK)
								{
									this.taskGlobalData.submit(new SubTask(
										this.taskGlobalData, operandsRecursion, this.operations.clone()));
								}
								else
								{
									calculate(operandsRecursion);
								}
							}
						}
					}
					if (depth == Calculator.THRESHOLD_DEPTH_PROGRESS)
					{
//						try
//						{
//							Thread.sleep(1);
//						}
//						catch (InterruptedException ex)
//						{
//							java.util.logging.Logger.getLogger(Calculator.class.getName()).
//								warning(ex.getMessage());
//						}
						this.taskGlobalData.incrementProgress();
//						System.out.println("Progress Value : " + this.taskGlobalData.counterProgress);
					}
				}
			}
		}
	}
}
