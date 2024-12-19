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

import de.bernd_michaely.chiffres.fx.display.OperandDisplay.DisplayType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import static de.bernd_michaely.chiffres.fx.display.OperandDisplay.createSpace;

/**
 * A display showing target and operands in the solution display.
 *
 * @author Bernd Michaely
 */
public class CalculationOperandsDisplay implements DisplayComponent
{
	private final HBox hBox;
	private final OperandDisplay operandDisplayTarget;
	private final OperandDisplay[] operandDisplays;
	private final Region[] spaces;

	public CalculationOperandsDisplay(int numOperands, int numDigitsTarget, int numDigitsOperands)
	{
		this.hBox = new HBox();
		this.hBox.setAlignment(Pos.CENTER);
		this.hBox.setPadding(new Insets(15, 5, 15, 5));
		this.operandDisplayTarget = new OperandDisplay(DisplayType.TARGET, numDigitsTarget);
		this.operandDisplays = new OperandDisplay[numOperands];
		this.spaces = new Region[numOperands];
		for (int i = 0; i < numOperands; i++)
		{
			this.operandDisplays[i] = new OperandDisplay(DisplayType.OPERAND, numDigitsOperands);
			this.spaces[i] = createSpace();
		}
	}

	@Override
	public Region getDisplay()
	{
		return this.hBox;
	}

	public OperandDisplay getOperandDisplayTarget()
	{
		return this.operandDisplayTarget;
	}

	public int getNumOperandDisplays()
	{
		return this.operandDisplays.length;
	}

	public OperandDisplay getOperandDisplay(int index)
	{
		if ((index >= 0) && (index < this.operandDisplays.length))
		{
			return this.operandDisplays[index];
		}
		else
		{
			return null;
		}
	}

	public void setAllOperandsVisible()
	{
		setNumOperandsVisible(getNumOperandDisplays());
	}

	public void setNumOperandsVisible(int numOperands)
	{
		final List<Node> nodes = new ArrayList<>();
		nodes.add(getOperandDisplayTarget().getDisplay());
		for (int i = 0; i < numOperands; i++)
		{
			nodes.add(this.spaces[i]);
			nodes.add(getOperandDisplay(i).getDisplay());
		}
		this.hBox.getChildren().setAll(nodes);
	}

	public Stream<OperandDisplay> operandStream()
	{
		return Arrays.stream(this.operandDisplays);
	}

	public Stream<OperandDisplay> targetAndOperandStream()
	{
		return Stream.concat(Stream.of(this.operandDisplayTarget), operandStream());
	}
}
