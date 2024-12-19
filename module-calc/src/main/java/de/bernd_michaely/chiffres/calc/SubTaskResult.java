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

/**
 * Class representing a subtask result of the parallelized main algorithm.
 *
 * @author Bernd Michaely
 */
class SubTaskResult extends ApproximationInfo
{
	final ConcatCollection<SolutionCandidate> solutionCandidates;

	SubTaskResult()
	{
		this.solutionCandidates = new ConcatCollection<>();
	}

	@Override
	void combine(ApproximationInfo other)
	{
		super.combine(other);
		if (other instanceof SubTaskResult)
		{
			final SubTaskResult otherResult = (SubTaskResult) other;
			this.solutionCandidates.concat(otherResult.solutionCandidates);
		}
	}
}
