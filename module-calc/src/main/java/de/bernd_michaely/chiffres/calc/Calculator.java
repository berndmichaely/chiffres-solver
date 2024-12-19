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

import de.bernd_michaely.chiffres.common.util.ConcatCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Main class in this package finding solutions for the "chiffres"-game.
 */
public class Calculator implements Callable<CalculationResult>
{
	static final int THRESHOLD_DEPTH_FORK = 0;
	static final int THRESHOLD_DEPTH_PROGRESS = THRESHOLD_DEPTH_FORK + 1;

	private final CalculationParams calculationParams;
	private int numThreads = 1;
	private TaskGlobalData taskGlobalData;
	private final CalculationResult result;
	private final Operand[] operands;
	private BiConsumer<Integer, Integer> onProgress;
	private Consumer<SolutionCandidate> onIntermediateResult;
	private boolean testMode;

	/**
	 * Creates a Calculator for a given calculation.
	 *
	 * @param calculationParams the given calculation
	 */
	public Calculator(CalculationParams calculationParams)
	{
		if (calculationParams == null)
		{
			throw new IllegalArgumentException("Calculator : Calculation is null");
		}
		this.calculationParams = calculationParams;
		this.result = new CalculationResult(calculationParams.getTarget());
		final int numOperands = calculationParams.getNumOperands();
		this.operands = new Operand[numOperands];
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
	public CalculationParams getCalculationParams()
	{
		return this.calculationParams;
	}

	/**
	 * Used for unit tests.
	 *
	 * @param modeParallel if true, a number of threads equal to the number of
	 *                     available processors is used, but a minimum of two,
	 *                     otherwise one single thread is used
	 */
	void setModeParallel(boolean modeParallel)
	{
		setNumThreads(modeParallel ?
			Math.max(2, Runtime.getRuntime().availableProcessors()) : 1);
	}

	public int getNumThreads()
	{
		final int numThreadsLimit = 2 * Runtime.getRuntime().availableProcessors();
		return Math.max(1, Math.min(numThreadsLimit, this.numThreads));
	}

	public void setNumThreads(int numThreads)
	{
		this.numThreads = numThreads;
	}

	public void setOnProgress(BiConsumer<Integer, Integer> onProgress)
	{
		this.onProgress = onProgress;
	}

	public void setOnIntermediateResult(Consumer<SolutionCandidate> onIntermediateResult)
	{
		this.onIntermediateResult = onIntermediateResult;
	}

	/**
	 * Cancels the calculation, if it is running in a separate thread.
	 *
	 * @see CalculationResult#isCancelled()
	 */
	public void cancel()
	{
		if (this.taskGlobalData != null)
		{
			this.taskGlobalData.cancelled = true;
		}
		this.result.cancelled = true;
	}

	boolean isTestMode()
	{
		return this.testMode;
	}

	void setTestMode(boolean testMode)
	{
		this.testMode = testMode;
	}

	private int calcNumSubTasksMaxOnLevel(int depth)
	{
		final int n = getCalculationParams().getNumOperands() - depth;
		return 2 * n * (n - 1);
	}

	private int calcNumSubTasksMax(int maxDepth)
	{
		return IntStream.rangeClosed(0, maxDepth).
			reduce(1, (a, b) -> a * calcNumSubTasksMaxOnLevel(b));
	}

	private int calcNumSubTasksMax()
	{
		return calcNumSubTasksMax(THRESHOLD_DEPTH_FORK);
	}

	public int getMaxProgressValue()
	{
		return calcNumSubTasksMax(THRESHOLD_DEPTH_PROGRESS);
	}

	@Override
	public CalculationResult call()
	{
		try
		{
			final SubTaskResult resultMainTask;
			final int n = getNumThreads();
			final Executor executor = (n > 1) ?
				new SubTaskExecutor(n) : Executors.newSingleThreadExecutor();
			try
			{
				final CompletionService<SubTaskResult> completionService =
					new ExecutorCompletionService<>(executor);
				this.taskGlobalData = new TaskGlobalData(getCalculationParams().getTarget(),
					getCalculationParams().getNumOperands(), completionService,
					this.onProgress, this.onIntermediateResult, isTestMode());
				// set progressMax to theoretical upper bound as initial guess:
				this.taskGlobalData.setProgressMax(getMaxProgressValue());
				resultMainTask = new SubTask(this.taskGlobalData, this.operands).call();
				final int numSubTasks = this.taskGlobalData.getCounterSubTasks();
				// update progressMax to exact value:
				this.taskGlobalData.setProgressMax(numSubTasks *
					calcNumSubTasksMaxOnLevel(THRESHOLD_DEPTH_PROGRESS));
				for (int i = 0; i < numSubTasks; i++)
				{
					try
					{
						resultMainTask.combine(completionService.take().get());
					}
					catch (ExecutionException | InterruptedException ex)
					{
						this.result.cancelled = true;
					}
				}
			}
			finally
			{
				if (executor instanceof ExecutorService)
				{
					((ExecutorService) executor).shutdown();
				}
				else if (executor instanceof SubTaskExecutor)
				{
					((SubTaskExecutor) executor).shutdown();
				}
			}
			this.result.combine(resultMainTask);
			this.result.solutions.addAll(collectSolutions(resultMainTask.solutionCandidates));
		}
		catch (OutOfMemoryError ex)
		{
			this.result.outOfMemory = true;
			this.result.cancelled = true;
			this.result.solutions.clear();
			throw ex;
		}
		return this.result;
	}

	/**
	 * Returns a collection of final solutions from solutionCandidates. The
	 * returned collection contains no duplicates and is not sorted.
	 *
	 * @param solutionCandidates the solution candidates
	 * @return a collection of final solutions from solutionCandidates
	 */
	public static Collection<Solution> collectSolutions(
		ConcatCollection<SolutionCandidate> solutionCandidates)
	{
		final SortedMap<Solution.EquivalenceClass, Solution> equivalenceClasses = new TreeMap<>();
		final Iterator<SolutionCandidate> iterator = solutionCandidates.iterator();
		while (iterator.hasNext())
		{
			final SolutionCandidate s = iterator.next();
			// store a canonical representative solution for each
			// equivalence class (e.g. the smallest)
			final Solution existing = equivalenceClasses.get(s.equivalenceClass);
			if ((existing == null) || (s.solution.compareTo(existing) < 0))
			{
				equivalenceClasses.put(s.equivalenceClass, s.solution);
			}
			iterator.remove(); // regain memory immediately during iteration
		}
		return equivalenceClasses.values();
	}
}
