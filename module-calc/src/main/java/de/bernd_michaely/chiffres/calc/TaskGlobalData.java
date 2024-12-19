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
import java.util.concurrent.CompletionService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Class to describe the read only data global to all recursive subtasks.
 *
 * @author Bernd Michaely
 */
class TaskGlobalData
{
	private final CompletionService<SubTaskResult> completionService;
	private final BiConsumer<Integer, Integer> onProgress;
	final Consumer<SolutionCandidate> onIntermediateResult;
	final int target;
	final int numOperands;
	final boolean testMode;
	private int counterProgress;
	private int progressMax;
	private volatile int counterSubTasks;
	volatile boolean cancelled = false;

	TaskGlobalData(int target, int numOperands, CompletionService<SubTaskResult> completionService,
		BiConsumer<Integer, Integer> onProgress, Consumer<SolutionCandidate> onIntermediateResult,
		boolean testMode)
	{
		this.target = target;
		this.numOperands = numOperands;
		this.completionService = completionService;
		this.onProgress = onProgress;
		this.onIntermediateResult = onIntermediateResult;
		this.testMode = testMode;
	}

	/**
	 * Submit a new subtask and count the submitted tasks. This does not need to
	 * be synchronized because it is called only from the main thread.
	 *
	 * @param callable the subtask to submit
	 */
	void submit(Callable<SubTaskResult> callable)
	{
		this.counterSubTasks++;
		this.completionService.submit(callable);
	}

	int getCounterSubTasks()
	{
		return this.counterSubTasks;
	}

	synchronized void setProgressMax(int progressMax)
	{
		this.progressMax = progressMax;
	}

	void incrementProgress()
	{
		if (this.onProgress != null)
		{
			synchronized (this)
			{
				this.onProgress.accept(++this.counterProgress, this.progressMax);
			}
		}
	}
}
