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

import de.bernd_michaely.chiffres.fx.display.CalculationState.State;
import de.bernd_michaely.chiffres.fx.mainwindow.PaneOptionsStandardGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import static de.bernd_michaely.chiffres.fx.display.ButtonCtrl.ButtonKey.*;
import static de.bernd_michaely.chiffres.fx.display.CalculationState.STATES_OPERAND;
import static de.bernd_michaely.chiffres.fx.display.CalculationState.STATES_TARGET;
import static de.bernd_michaely.chiffres.fx.display.CalculationState.State.*;

/**
 * Class to create the UI for a calculation.
 *
 * @author Bernd Michaely
 */
public class CalculationDisplayStandard extends AbstractCalculationDisplay
{
	/**
	 * The number of operands according to the standard game rules.
	 */
	public static final int NUM_OPERANDS = 6;
	private static final Logger logger = Logger.getLogger(CalculationDisplayStandard.class.getName());
	private static final String ID_OPERAND_DISPLAY_NUM_PREFIX = "OPERAND_DISPLAY_NUM_#";
	private static final String ID_OPERAND_DISPLAY_RESULT = "OPERAND_DISPLAY_RESULT";
	private final CalculationState calculationState = new CalculationState();
	private final BorderPane borderPane;
	private final CalculationOperandsDisplay calculationOperandsDisplay;
	private final ObservableIntegerArray operands;
	private final ReadOnlyBooleanWrapper operandArrayCompletePropertyWrapper;
	private int indexFocused;
	private final NumPadDisplay numPadDisplay;

	/**
	 * Creates a calculation parameters entry display.
	 *
	 * @param createRandomCalculationParams if true, operands and target are
	 *                                      preset with random numbers and empty
	 *                                      otherwise
	 */
	public CalculationDisplayStandard(boolean createRandomCalculationParams)
	{
		this(createRandomCalculationParams ? createRandomTarget() : 0,
			createRandomCalculationParams ? createRandomOperands() : null);
	}

	private static int createRandomTarget()
	{
		return ((int) (Math.random() * 899)) + 101;
	}

	private static ObservableIntegerArray createRandomOperands()
	{
		final List<Integer> list = new ArrayList<>(24);
		for (int i = 0; i < 2; i++)
		{
			for (int k = 1; k <= 10; k++)
			{
				list.add(k);
			}
		}
		list.add(25);
		list.add(50);
		list.add(75);
		list.add(100);
		Collections.shuffle(list);
		final int[] randomOperands = new int[NUM_OPERANDS];
		for (int i = 0; i < randomOperands.length; i++)
		{
			randomOperands[i] = list.get(i);
		}
		return FXCollections.observableIntegerArray(randomOperands);
	}

	/**
	 * Creates a calculation parameters entry display for the standard game rules
	 * with the given operands.
	 *
	 * @param operands the given operands (can be null for an empty display)
	 */
	public CalculationDisplayStandard(int... operands)
	{
		this(0, FXCollections.observableIntegerArray(operands));
	}

	/**
	 * Creates a calculation parameters entry display for the standard game rules
	 * with the given operands and target.
	 *
	 * @param target   a given calculation target or zero for an empty display
	 * @param operands the given operands or null for an empty display
	 */
	public CalculationDisplayStandard(int target, ObservableIntegerArray operands)
	{
		this.numPadDisplay = new NumPadDisplay(event -> handleActionEvent(event));
		this.calculationOperandsDisplay = new CalculationOperandsDisplay(NUM_OPERANDS, 3, 3);
		this.calculationOperandsDisplay.getOperandDisplayTarget().setEventHandler(this::handleButtonCommand);
		for (int i = 0; i < this.calculationOperandsDisplay.getNumOperandDisplays(); i++)
		{
			final OperandDisplay operandDisplay = this.calculationOperandsDisplay.getOperandDisplay(i);
			operandDisplay.setEventHandler(this::handleOperandCommand);
			operandDisplay.setHighlighted(i == 0);
			operandDisplay.getDisplay().setUserData(ID_OPERAND_DISPLAY_NUM_PREFIX + i);
		}
		this.calculationOperandsDisplay.getOperandDisplayTarget().getDisplay().
			setUserData(ID_OPERAND_DISPLAY_RESULT);
		setOperandActionEnabled(true);
		this.calculationOperandsDisplay.setAllOperandsVisible();
		final HBox paneInner = new HBox(this.numPadDisplay.getDisplay());
		paneInner.setAlignment(Pos.TOP_CENTER);
		paneInner.setFillHeight(false);
		paneInner.setPadding(new Insets(50, 15, 15, 15));
		this.borderPane = new BorderPane(new ScaleBox(paneInner).getDisplay());
		this.borderPane.setMinSize(1, 1);
		this.borderPane.setTop(new ScaleBox(this.calculationOperandsDisplay.getDisplay()).getDisplay());
		if ((operands != null) && (operands.size() >= NUM_OPERANDS))
		{
			this.operands = FXCollections.observableIntegerArray();
			this.operands.addAll(operands, 0, NUM_OPERANDS);
			for (int i = 0; i < NUM_OPERANDS; i++)
			{
				handleButtonNumeric(operands.get(i));
			}
		}
		else
		{
			this.operands = FXCollections.observableIntegerArray(new int[NUM_OPERANDS]);
			updateState();
		}
		if ((target > 100) && (target < 1000))
		{
			handleButtonNumeric(target);
		}
		this.operandArrayCompletePropertyWrapper = new ReadOnlyBooleanWrapper(isOperandArrayComplete());
		this.operands.addListener((ObservableIntegerArray observableArray, boolean sizeChanged, int from, int to) ->
		{
			operandArrayCompletePropertyWrapper.set(isOperandArrayComplete());
		});
	}

	@Override
	public Region getDisplay()
	{
		return this.borderPane;
	}

	@Override
	public int getMaxNumDigitsOperand()
	{
		return 3;
	}

	@Override
	public int getMaxNumDigitsTarget()
	{
		return 3;
	}

	private OperandDisplay getOperandDisplayTarget()
	{
		return this.calculationOperandsDisplay.getOperandDisplayTarget();
	}

	private OperandDisplay getOperandDisplay(int index)
	{
		return this.calculationOperandsDisplay.getOperandDisplay(index);
	}

	/**
	 * Returns the current state. The return value will never be null.
	 *
	 * @return the current state
	 */
	private State getState()
	{
		return this.calculationState.getState();
	}

	/**
	 * Sets the new state.
	 *
	 * @param state the new state. A null value will be ignored.
	 */
	private void setState(State state)
	{
		this.calculationState.setState(state);
	}

	private void setOperandActionEnabled(boolean enabled)
	{
		getOperandDisplayTarget().setMouseActionEnabled(enabled);
		this.calculationOperandsDisplay.operandStream().forEach(operandDisplay ->
			operandDisplay.setMouseActionEnabled(enabled));
	}

	private void handleActionEvent(ActionEvent event)
	{
		if (event.getSource() instanceof Button)
		{
			final Button button = (Button) event.getSource();
			final Object userData = button.getUserData();
			final String id = (userData instanceof String) ? userData.toString() : "";
			if (id.startsWith(ButtonCtrl.NUM_BUTTON_ID_PREFIX))
			{
				handleButtonNumeric(Integer.parseInt(
					id.substring(ButtonCtrl.NUM_BUTTON_ID_PREFIX.length())));
			}
			else
			{
				handleButtonCommand(id);
			}
		}
	}

	private int getFirstUnsetIndex()
	{
		int index = 0;
		while (index < this.calculationOperandsDisplay.getNumOperandDisplays())
		{
			if (getOperandDisplay(index).getText().isEmpty())
			{
				return index;
			}
			index++;
		}
		return index;
	}

	private int getLastSetIndex()
	{
		int index = NUM_OPERANDS;
		if (!getOperandDisplayTarget().getText().isEmpty())
		{
			return index;
		}
		else
		{
			while (--index >= 0)
			{
				if (!getOperandDisplay(index).getText().isEmpty())
				{
					return index;
				}
			}
			return index;
		}
	}

	private void updateIndexFocused(int index)
	{
		if (this.indexFocused != index)
		{
			// undo current state:
			if ((this.indexFocused >= 0) && (this.indexFocused < NUM_OPERANDS))
			{
				getOperandDisplay(this.indexFocused).setHighlighted(false);
			}
			else if (this.indexFocused == NUM_OPERANDS)
			{
				getOperandDisplayTarget().setHighlighted(false);
			}
			// create next state:
			if ((this.indexFocused >= -1) && (this.indexFocused <= NUM_OPERANDS))
			{
				this.indexFocused = index;
				if ((this.indexFocused >= 0) && (this.indexFocused < NUM_OPERANDS))
				{
					getOperandDisplay(this.indexFocused).setHighlighted(true);
				}
				else if (this.indexFocused == NUM_OPERANDS)
				{
					getOperandDisplayTarget().setHighlighted(true);
				}
			}
		}
	}

	private void updateState()
	{
		updateIndexFocused(getFirstUnsetIndex());
		if (this.indexFocused >= 0)
		{
			if (getLastSetIndex() < 0)
			{
				setState(INITIAL);
			}
			else if (this.indexFocused < NUM_OPERANDS)
			{
				setState(OPERANDS);
			}
			else
			{
				final String text = getOperandDisplayTarget().getText();
				if (text.isEmpty())
				{
					setState(TARGET_EMPTY);
				}
				else if (text.equals("10"))
				{
					setState(TARGET_10X);
				}
				else if (text.length() == 3)
				{
					setState(READY);
				}
				else
				{
					setState(TARGET_NOT_10X);
				}
			}
		}
		this.numPadDisplay.getButtonCtrl().updateButtonStates(getState());
	}

	private void clearOperand(int index)
	{
		if ((index >= 0) && (index < NUM_OPERANDS))
		{
			final OperandDisplay operandDisplay = getOperandDisplay(index);
			operandDisplay.setText("");
			this.operands.set(index, 0);
		}
		else if (index == NUM_OPERANDS)
		{
			if (getOperandDisplayTarget().getText().isEmpty())
			{
				clearOperand(NUM_OPERANDS - 1);
			}
			else
			{
				getOperandDisplayTarget().setText("");
			}
		}
	}

	private void setOperand(int index, int value)
	{
		if (value >= 0)
		{
			if ((index >= 0) && (index < NUM_OPERANDS))
			{
				final OperandDisplay operandDisplay = getOperandDisplay(index);
				operandDisplay.setText("" + value);
				this.operands.set(index, value);
			}
			else if (index == NUM_OPERANDS)
			{
				getOperandDisplayTarget().setText(
					getOperandDisplayTarget().getText() + value);
			}
		}
	}

	private void handleButtonNumeric(int numValue)
	{
		if (STATES_OPERAND.contains(getState()))
		{
			setOperand(this.indexFocused, numValue);
		}
		else if (STATES_TARGET.contains(getState()))
		{
			setOperand(NUM_OPERANDS, numValue);
		}
		updateState();
	}

	private void handleButtonCommand(String idCmd)
	{
		if (idCmd == null)
		{
			return;
		}
		if (getState().compareTo(STARTED) >= 0)
		{
			return;
		}
		// handle command id:
		if (idCmd.equals(BUTTON_START.toString()))
		{
			if (this.indexFocused == NUM_OPERANDS)
			{
				runCalculation();
			}
		}
		else if (idCmd.equals(BUTTON_CLEAR.toString()))
		{
			clearOperand(getLastSetIndex());
		}
		else if (idCmd.equals(BUTTON_CLEAR_ALL.toString()))
		{
			for (int i = NUM_OPERANDS; i >= 0; i--)
			{
				clearOperand(i);
			}
		}
		else if (idCmd.equals(ID_OPERAND_DISPLAY_RESULT))
		{
			clearOperand(NUM_OPERANDS);
		}
		else
		{
			logger.log(Level.INFO, "BUTTON {0} PRESSED", idCmd);
		}
		updateState();
	}

	private void handleOperandCommand(String idOperandDisplay)
	{
		if (getState().compareTo(STARTED) >= 0)
		{
			return;
		}
		if (idOperandDisplay.startsWith(ID_OPERAND_DISPLAY_NUM_PREFIX))
		{
			try
			{
				clearOperand(Integer.parseInt(idOperandDisplay.substring(
					ID_OPERAND_DISPLAY_NUM_PREFIX.length())));
				updateState();
			}
			catch (NumberFormatException exception)
			{
				logger.warning("Invalid OperandDisplay index");
			}
		}
	}

	private int[] getOperands()
	{
		final int[] arOperands = new int[NUM_OPERANDS];
		return this.operands.toArray(0, arOperands, NUM_OPERANDS);
	}

	/**
	 * Returns true, if the user has entered all operands.
	 *
	 * @return true, if the user has entered all operands
	 */
	private boolean isOperandArrayComplete()
	{
		if (this.operands.size() != NUM_OPERANDS)
		{
			return false;
		}
		for (int i = 0; i < NUM_OPERANDS; i++)
		{
			if (this.operands.get(i) <= 0)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public ReadOnlyBooleanProperty operandArrayCompleteProperty()
	{
		return this.operandArrayCompletePropertyWrapper.getReadOnlyProperty();
	}

	private int getTarget()
	{
		try
		{
			return Integer.parseInt(getOperandDisplayTarget().getText());
		}
		catch (NumberFormatException ex)
		{
			return 0;
		}
	}

	@Override
	public CalculationCtrlParams getCalculationCtrlParams()
	{
		return new CalculationCtrlParams(
			PaneOptionsStandardGame.isShowingIntermediateResult(),
			PaneOptionsStandardGame.getNumThreads(),
			getTarget(), getOperands());
	}

	private void runCalculation()
	{
		setState(STARTED);
		setOperandActionEnabled(false);
		updateIndexFocused(-1);
		if (getOnStart() != null)
		{
			getOnStart().accept(getTabReference(), getCalculationCtrlParams());
		}
	}
}
