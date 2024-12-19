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
package de.bernd_michaely.chiffres.fx.graph;

import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.fx.util.MathSymbol;
import de.bernd_michaely.chiffres.graphics.SolutionGraphDirector;
import javafx.scene.layout.Region;

/**
 * Class to display a graphic of a solution tree. This implementation is based
 * on JavaFX Shapes.
 *
 * @author Bernd Michaely
 */
public class SolutionGraphShape implements SolutionGraph
{
	private SolutionGraphDirector director;
	private final SolutionGraphBuilder builder;
	private Solution solution;

	public SolutionGraphShape(int numOperands)
	{
		this(numOperands, MathSymbol.DEFAULT_FONT_SIZE);
	}

	private SolutionGraphShape(int numOperands, double fontSize)
	{
		this.builder = new SolutionGraphBuilder();
		this.director = new SolutionGraphDirector(builder, numOperands, fontSize);
	}

	@Override
	public Solution getSolution()
	{
		return this.solution;
	}

	@Override
	public void setSolution(Solution solution)
	{
		this.solution = solution;
		builder.clear();
		this.director.construct(solution);
		if (solution != null)
		{
			builder.updateGraphWidth();
		}
	}

	@Override
	public Region getDisplay()
	{
		return builder.getPane();
	}
}
