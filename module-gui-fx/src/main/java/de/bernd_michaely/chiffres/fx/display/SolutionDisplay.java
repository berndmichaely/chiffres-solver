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
package de.bernd_michaely.chiffres.fx.display;

import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.calc.SolutionCandidate;
import java.util.SortedSet;
import javafx.beans.property.DoubleProperty;

/**
 * Interface for classes to display solutions of a calculation.
 *
 * @author Bernd Michaely
 */
public interface SolutionDisplay extends CalculationCtrl
{
	void setSolutions(SortedSet<Solution> solutions);

	DoubleProperty progressProperty();

	default double getProgress()
	{
		return progressProperty().get();
	}

	default void setProgress(double value)
	{
		progressProperty().set(value);
	}

	void setOnCancel(Runnable runnable);

	void cancel();

	/**
	 * Adds a single intermediate result. This method may be called from a worker
	 * thread.
	 *
	 * @param solutionCandidate the solution candidate
	 */
	void handleIntermediateResult(SolutionCandidate solutionCandidate);

	/**
	 * Calls the cancel method by default.
	 *
	 * @see #cancel()
	 */
	@Override
	default void onClose()
	{
		cancel();
	}
}
