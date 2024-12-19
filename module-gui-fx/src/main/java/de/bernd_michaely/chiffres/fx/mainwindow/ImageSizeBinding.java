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
package de.bernd_michaely.chiffres.fx.mainwindow;

import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.fx.canvas.CanvasSizeProvider;
import de.bernd_michaely.chiffres.graphics.SolutionGraphDirector;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;

/**
 * StringBinding to display pre-calculated image size.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class ImageSizeBinding extends StringBinding
{
	private final ObservableValue<Integer> observableValue;
	private final CanvasSizeProvider builder;
	private final SolutionGraphDirector director;

	ImageSizeBinding(ObservableValue<Integer> observableValue, int numOperands, Solution solution)
	{
		this.observableValue = observableValue;
		this.builder = new CanvasSizeProvider();
		this.director = new SolutionGraphDirector(builder, numOperands, 1);
		director.setSolution(solution);
		bind(observableValue);
	}

	@Override
	protected String computeValue()
	{
		director.setFontSize(observableValue.getValue());
		return String.format("Resulting image size : %d x %d",
			builder.getCanvasWidth(), builder.getCanvasHeight());
	}
}
