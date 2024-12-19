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

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

/**
 * Aggregator for test cases from file.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class CalculationParamAggregator implements ArgumentsAggregator
{
	@Override
	public CalculationParams aggregateArguments(ArgumentsAccessor arguments,
		ParameterContext context) throws ArgumentsAggregationException
	{
//		System.out.println("########## " + arguments.getString(7));
		return new CalculationParams(arguments.getInteger(0),
			arguments.getInteger(1),
			arguments.getInteger(2),
			arguments.getInteger(3),
			arguments.getInteger(4),
			arguments.getInteger(5),
			arguments.getInteger(6));
	}
}
