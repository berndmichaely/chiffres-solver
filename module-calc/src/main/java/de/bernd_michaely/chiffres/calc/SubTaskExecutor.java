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

import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Executor for subtasks.
 *
 * @author Bernd Michaely
 */
class SubTaskExecutor implements Executor
{
	private static final Logger logger = Logger.getLogger(SubTaskExecutor.class.getName());
	private final Semaphore semaphore;
	private volatile boolean shutdown;
	private volatile static int counter;

	SubTaskExecutor()
	{
		this(Runtime.getRuntime().availableProcessors());
	}

	SubTaskExecutor(int numThreads)
	{
		this.semaphore = new Semaphore(numThreads);
	}

	void shutdown()
	{
		this.shutdown = true;
	}

	@Override
	public void execute(Runnable command)
	{
		boolean submitted = false;
		while (!this.shutdown && !submitted)
		{
			try
			{
				this.semaphore.acquire();
				submitted = true;
				new Thread(() ->
				{
					logger.log(Level.FINEST, "{0} threads running", ++counter);
					try
					{
						command.run();
					}
					finally
					{
						semaphore.release();
						logger.log(Level.FINEST, "{0} threads running", --counter);
					}
				}).start();
			}
			catch (InterruptedException ex)
			{
				submitted = false;
				// recheck loop condition
			}
		}
	}
}
