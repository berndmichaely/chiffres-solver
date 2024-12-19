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

import de.bernd_michaely.chiffres.calc.CalculationResult;
import de.bernd_michaely.chiffres.calc.Calculator;
import java.util.function.Consumer;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;

/**
 * A task to run a calculator.
 *
 * @author Bernd Michaely
 */
public class CalculatorTask extends Task<CalculationResult>
{
	private final Calculator calculator;

	public CalculatorTask(Calculator calculator)
	{
		this(calculator, null, null);
	}

	public CalculatorTask(Calculator calculator, Consumer<CalculationResult> consumer)
	{
		this(calculator, consumer, null);
	}

	public CalculatorTask(Calculator calculator, Consumer<CalculationResult> consumer,
		DoubleProperty progressProperty)
	{
		if (calculator == null)
		{
			throw new IllegalArgumentException("calculator is null");
		}
		this.calculator = calculator;
		setOnCancelled(event -> calculator.cancel());
		if (consumer != null)
		{
			this.setOnSucceeded(event -> consumer.accept(getValue()));
		}
		if (progressProperty != null)
		{
			calculator.setOnProgress((progressValue, progressMax) ->
				updateProgress(progressValue, progressMax));
			progressProperty.bind(this.progressProperty());
		}
	}

	@Override
	protected CalculationResult call()
	{
		return this.calculator.call();
	}
}
