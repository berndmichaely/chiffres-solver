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

import de.bernd_michaely.chiffres.fx.display.ProgressDisplay.ProgressDisplayMode;
import de.bernd_michaely.chiffres.fx.util.ControlFactory;
import de.bernd_michaely.chiffres.fx.util.MathSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import static de.bernd_michaely.chiffres.fx.mainwindow.PreferencesKeys.*;
import static de.bernd_michaely.chiffres.fx.util.MathSymbol.DEFAULT_FONT_SIZE;

/**
 * Class to create an extended calculation parameters editing display.
 *
 * @author Bernd Michaely
 */
public class CalculationDisplayExtended extends AbstractCalculationDisplay
{
	private static final int NUM_OPERANDS_DEFAULT = CalculationDisplayStandard.NUM_OPERANDS + 1;
	private static final int NUM_OPERANDS_MAX = 9;
	private static final int VALUE_MAX_OPERANDS = 999;
	private static final int VALUE_MAX_OPERANDS_RANDOM = 100;
	private static final int VALUE_MAX_TARGET = 9999;
	private static final int VALUE_MAX_TARGET_RANDOM = 999;
	private final ObservableIntegerArray operands;
	private final ReadOnlyBooleanWrapper operandArrayCompletePropertyWrapper;
	private boolean inputValid;
	private final BorderPane borderPane;
	private final VBox vBox;
	private final Slider sliderNumOp;
	private final TextInputControl inputTarget;
	private final List<TextInputControl> inputOperands;
	private final Slider sliderNumThreads;
	private final CheckBox checkBoxIntermediateResults;
	private final Button buttonRandom;
	private final Button buttonClear;
	private final CalculationOperandsDisplay calculationOperandsDisplay;
	private final ProgressDisplay progressDisplay;

	private class OperandChangeListener implements ChangeListener<String>
	{
		private final int index;

		private OperandChangeListener(int index)
		{
			this.index = index;
		}

		@Override
		public void changed(ObservableValue<? extends String> observable,
			String oldValue, String newValue)
		{
			operands.set(index, getIntValue(newValue));
			validateInput();
		}
	}

	/**
	 * Creates an empty extended calculation parameters entry display.
	 */
	public CalculationDisplayExtended()
	{
		this(null);
	}

	/**
	 * Creates an extended calculation parameters entry display with the given
	 * operands.
	 *
	 * @param initialCtrlParams the initial control params (can be null for an
	 *                          empty display)
	 */
	public CalculationDisplayExtended(CalculationCtrlParams initialCtrlParams)
	{
		final int[] initialOperands =
			((initialCtrlParams != null) && (initialCtrlParams.getOperands() != null)) ?
			initialCtrlParams.getOperands() : null;
		final int numOperandsDefault = preferences.getInt(
			ID_PREF_MODE_EXTENDED_NUM_OPERANDS.key(), NUM_OPERANDS_DEFAULT);
		final int numOperandsInitial = (initialOperands != null) ?
			Math.min(initialOperands.length, NUM_OPERANDS_MAX) : numOperandsDefault;
		this.operands = FXCollections.observableIntegerArray(new int[NUM_OPERANDS_MAX]);
		this.operandArrayCompletePropertyWrapper = new ReadOnlyBooleanWrapper();
		this.progressDisplay = new ProgressDisplay(ProgressDisplayMode.EDITOR);
		this.sliderNumOp = new Slider(2, NUM_OPERANDS_MAX, numOperandsInitial);
		this.sliderNumOp.setTooltip(new Tooltip("Select the number of operands"));
		this.sliderNumOp.setShowTickMarks(true);
		this.sliderNumOp.setShowTickLabels(true);
		this.sliderNumOp.setMinorTickCount(0);
		this.sliderNumOp.setMajorTickUnit(1);
		this.sliderNumOp.setBlockIncrement(1);
		this.sliderNumOp.setSnapToTicks(true);
		this.sliderNumOp.disableProperty().bind(this.progressDisplay.runningProperty());
		final GridPane gridPane = new GridPane();
		gridPane.setHgap(8);
		gridPane.setVgap(20);
		this.inputTarget = ControlFactory.createNaturalNumberInput(VALUE_MAX_TARGET);
		this.inputTarget.setPromptText("Target");
		this.inputTarget.setTooltip(new Tooltip(
			"Enter a target number in the range of [1.." + VALUE_MAX_TARGET + "]"));
		this.calculationOperandsDisplay = new CalculationOperandsDisplay(NUM_OPERANDS_MAX, 4, 3);
		this.calculationOperandsDisplay.getOperandDisplayTarget().textProperty().
			bind(this.inputTarget.textProperty());
		this.inputTarget.disableProperty().bind(this.progressDisplay.runningProperty());
		if (initialCtrlParams != null)
		{
			setTarget(initialCtrlParams.getTarget());
		}
		// ====================== GRID START ======================================
		int rowIndex = -1;
		// ====================== GRID ROW ========================================
		rowIndex++;
		gridPane.add(this.inputTarget, 0, rowIndex, 3, 1);
		GridPane.setFillWidth(this.inputTarget, false);
		// ====================== GRID ROW ========================================
		rowIndex++;
		this.inputOperands = new ArrayList<>(NUM_OPERANDS_MAX);
		for (int i = 0; i < NUM_OPERANDS_MAX; i++)
		{
			final TextInputControl control = ControlFactory.createNaturalNumberInput(VALUE_MAX_OPERANDS);
			control.setTooltip(new Tooltip(
				"Enter an operand in the range of [1.." + VALUE_MAX_OPERANDS + "]"));
			this.inputOperands.add(control);
			if ((initialOperands != null) && (i < numOperandsInitial))
			{
				setOperand(i, initialOperands[i]);
			}
			control.textProperty().addListener(new OperandChangeListener(i));
			control.setPromptText("Op. #" + (i + 1));
			control.disableProperty().bind(this.progressDisplay.runningProperty().or(
				this.sliderNumOp.valueProperty().lessThanOrEqualTo(i)));
			gridPane.add(control, i, rowIndex);
			this.calculationOperandsDisplay.getOperandDisplay(i).textProperty().bind(control.textProperty());
		}
		// ====================== GRID ROW ========================================
		rowIndex++;
		final int colSpansliderNumOp = 1;
		gridPane.add(this.sliderNumOp, colSpansliderNumOp, rowIndex,
			NUM_OPERANDS_MAX - colSpansliderNumOp, 1);
		// ====================== GRID ROW ========================================
		rowIndex++;
		this.buttonRandom = new Button("R_andom");
		this.buttonRandom.setTooltip(new Tooltip("Create random input numbers"));
		this.buttonRandom.setOnAction(e -> setRandomParameters());
		this.buttonRandom.disableProperty().bind(this.progressDisplay.runningProperty());
		this.buttonClear = new Button("C_lear");
		this.buttonClear.setTooltip(new Tooltip("Clear all input fields"));
		this.buttonClear.setOnAction(e -> clearInput());
		this.buttonClear.disableProperty().bind(this.progressDisplay.runningProperty());
		final double gap = MathSymbol.DEFAULT_FONT_SIZE;
		this.buttonRandom.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		this.buttonClear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		final TilePane tilePane = new TilePane(Orientation.HORIZONTAL, gap, gap,
			this.buttonRandom, this.buttonClear);
		gridPane.add(tilePane, 0, rowIndex, NUM_OPERANDS_MAX, 1);
		GridPane.setHalignment(this.buttonRandom, HPos.LEFT);
		// ====================== GRID ROW ========================================
		rowIndex++;
		final int numThreadsMax = Runtime.getRuntime().availableProcessors();
		final int numThreadsDefault = Math.round(numThreadsMax * 3f / 8f);
		final int numThreads = (initialCtrlParams != null) ?
			initialCtrlParams.getNumThreads() :
			preferences.getInt(ID_PREF_MODE_EXTENDED_NUM_THREADS.key(), numThreadsDefault);
		this.sliderNumThreads = new Slider(1, numThreadsMax, numThreads);
		this.sliderNumThreads.setBlockIncrement(1);
		this.sliderNumThreads.setMajorTickUnit(1);
		this.sliderNumThreads.setMinorTickCount(0);
		this.sliderNumThreads.setShowTickLabels(true);
		this.sliderNumThreads.setShowTickMarks(true);
		this.sliderNumThreads.setSnapToTicks(true);
		this.sliderNumThreads.valueProperty().addListener((observable, oldValue, newValue) ->
			preferences.putInt(ID_PREF_MODE_EXTENDED_NUM_THREADS.key(), newValue.intValue()));
		this.sliderNumThreads.disableProperty().bind(this.progressDisplay.runningProperty());
		final Label labelSlider = new Label("_Number of threads to use for computation:");
		labelSlider.setMnemonicParsing(true);
		labelSlider.setLabelFor(this.sliderNumThreads);
		labelSlider.disableProperty().bind(this.progressDisplay.runningProperty());
		final VBox vBoxSlider = new VBox(DEFAULT_FONT_SIZE * 2 / 3, labelSlider, this.sliderNumThreads);
		gridPane.add(vBoxSlider, 0, rowIndex, NUM_OPERANDS_MAX, 1);
		// ====================== GRID ROW ========================================
		rowIndex++;
		this.checkBoxIntermediateResults = new CheckBox("Show _intermediate results during calculation");
		this.checkBoxIntermediateResults.setSelected((initialCtrlParams != null) ?
			initialCtrlParams.isShowingIntermediateResult() :
			preferences.getBoolean(ID_PREF_MODE_EXTENDED_INTERMEDIATE.key(), false));
		this.checkBoxIntermediateResults.selectedProperty().addListener((observable, oldValue, newValue) ->
			preferences.putBoolean(ID_PREF_MODE_EXTENDED_INTERMEDIATE.key(), newValue));
		this.checkBoxIntermediateResults.disableProperty().bind(this.progressDisplay.runningProperty());
		gridPane.add(this.checkBoxIntermediateResults, 0, rowIndex, NUM_OPERANDS_MAX, 1);
		// ====================== GRID ROW ========================================
		rowIndex++;
		gridPane.add(this.progressDisplay.getDisplay(), 0, rowIndex, NUM_OPERANDS_MAX, 1);
		GridPane.setHalignment(this.progressDisplay.getDisplay(), HPos.CENTER);
		this.progressDisplay.setOnStart(() ->
		{
			if (getOnStart() != null)
			{
				getOnStart().accept(getTabReference(), getCalculationCtrlParams());
			}
		});
		// ====================== GRID END ========================================
		this.vBox = new VBox(gridPane);
		this.vBox.setOnMouseClicked(mouseEvent ->
		{
			if (mouseEvent.getClickCount() == 2)
			{
				if (!mouseEvent.isShiftDown() && mouseEvent.isControlDown() && mouseEvent.isAltDown())
				{
					gridPane.setGridLinesVisible(!gridPane.isGridLinesVisible());
				}
			}
		});
		this.vBox.setFillWidth(false);
		this.vBox.setAlignment(Pos.CENTER);
		this.vBox.setMinSize(MIN_WIDTH, MIN_HEIGHT);
		this.borderPane = new BorderPane(new ScaleBox(this.vBox).getDisplay());
		updateOperandDisplays(true);
		this.borderPane.setTop(new ScaleBox(this.calculationOperandsDisplay.getDisplay()).getDisplay());
		// add event handler:
		this.sliderNumOp.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			preferences.putInt(ID_PREF_MODE_EXTENDED_NUM_OPERANDS.key(), newValue.intValue());
			validateInput();
			updateOperandDisplays(false);
		});
		this.inputTarget.textProperty().addListener((observable, oldValue, newValue) ->
			validateInput());
		validateInput();
		updateOperandDisplays(false);
	}

	@Override
	public Region getDisplay()
	{
		return this.borderPane;
	}

	public ProgressDisplay getProgressDisplay()
	{
		return this.progressDisplay;
	}

	@Override
	protected Node getInitiallyFocusedNode()
	{
		return this.inputTarget;
	}

	@Override
	public void onClose()
	{
		this.progressDisplay.cancel();
	}

	@Override
	public int getMaxNumDigitsOperand()
	{
		return Integer.toString(VALUE_MAX_OPERANDS).length();
	}

	@Override
	public int getMaxNumDigitsTarget()
	{
		return Integer.toString(VALUE_MAX_TARGET).length();
	}

	@Override
	public CalculationCtrlParams getCalculationCtrlParams()
	{
		return new CalculationCtrlParams(
			this.checkBoxIntermediateResults.isSelected(),
			(int) this.sliderNumThreads.getValue(),
			getTarget(), getOperands());
	}

	@Override
	public ReadOnlyBooleanProperty operandArrayCompleteProperty()
	{
		return this.operandArrayCompletePropertyWrapper.getReadOnlyProperty();
	}

	private int getTarget()
	{
		return getIntValue(this.inputTarget.getText());
	}

	private void setTarget(int value)
	{
		if ((value > 0) && (value <= VALUE_MAX_TARGET))
		{
			this.inputTarget.setText("" + value);
		}
	}

	private int getNumOperands()
	{
		return this.sliderNumOp.valueProperty().intValue();
	}

	private void setOperand(int index, int value)
	{
		if ((value > 0) && (value <= VALUE_MAX_OPERANDS))
		{
			this.inputOperands.get(index).setText("" + value);
			this.operands.set(index, value);
		}
	}

	private int[] getOperands()
	{
		final int numOperands = getNumOperands();
		final int[] arOperands = new int[numOperands];
		return this.operands.toArray(0, arOperands, numOperands);
	}

	private static int getIntValue(String string)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch (NumberFormatException ex)
		{
			return 0;
		}
	}

	private void validateInput()
	{
		final boolean operandArrayComplete = IntStream.range(0, getNumOperands()).
			allMatch(i -> operands.get(i) > 0);
		final boolean targetValid = getTarget() > 0;
		this.inputValid = targetValid && operandArrayComplete;
		this.progressDisplay.setStartDisabled(!this.inputValid);
		this.operandArrayCompletePropertyWrapper.set(operandArrayComplete);
	}

	private void clearInput()
	{
		this.inputTarget.clear();
		this.inputOperands.forEach(t -> t.clear());
	}

	private void setRandomParameters()
	{
		final Random random = new Random();
		this.inputTarget.setText("" + (1 + random.nextInt(VALUE_MAX_TARGET_RANDOM)));
		final int n = getNumOperands();
		for (int i = 0; i < NUM_OPERANDS_MAX; i++)
		{
			if (i < n)
			{
				setOperand(i, 1 + random.nextInt(VALUE_MAX_OPERANDS_RANDOM));
			}
			else
			{
				this.inputOperands.get(i).clear();
			}
		}
	}

	private void updateOperandDisplays(boolean addAll)
	{
		final int n = addAll ? NUM_OPERANDS_MAX : getNumOperands();
		this.calculationOperandsDisplay.setNumOperandsVisible(n);
	}
}
