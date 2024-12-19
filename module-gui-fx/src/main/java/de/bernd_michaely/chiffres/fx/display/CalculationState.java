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

import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * Class to describe the internal state of the standard calculation input.
 *
 * @see CalculationDisplayStandard
 * @author Bernd Michaely
 */
class CalculationState
{
	private static final Logger logger = Logger.getLogger(CalculationState.class.getName());

	enum State
	{
		INITIAL, OPERANDS,
		TARGET_EMPTY, TARGET_10X, TARGET_NOT_10X,
		READY, STARTED, FINISHED;
	}
	static final EnumSet<State> STATES_OPERAND =
		EnumSet.of(State.INITIAL, State.OPERANDS);
	static final EnumSet<State> STATES_TARGET =
		EnumSet.of(State.TARGET_EMPTY, State.TARGET_NOT_10X, State.TARGET_10X);
	private State state = State.INITIAL;

	/**
	 * Returns the current state. The return value will never be null.
	 *
	 * @return the current state
	 */
	State getState()
	{
		return this.state;
	}

	/**
	 * Sets the new state.
	 *
	 * @param state the new state. A null value will be ignored.
	 */
	void setState(State state)
	{
		if (state != null)
		{
			this.state = state;
		}
		else
		{
			logger.warning("CalculationState is null");
		}
	}
}
