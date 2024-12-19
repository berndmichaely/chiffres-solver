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

import de.bernd_michaely.chiffres.calc.CalculationParams;
import de.bernd_michaely.chiffres.calc.CalculationResult;
import de.bernd_michaely.chiffres.calc.Calculator;
import de.bernd_michaely.chiffres.fx.display.CalculationCtrl;
import de.bernd_michaely.chiffres.fx.display.CalculationCtrlParams;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplay;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplayExtended;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplayStandard;
import de.bernd_michaely.chiffres.fx.display.CalculationOperandsDisplay;
import de.bernd_michaely.chiffres.fx.display.CalculatorTask;
import de.bernd_michaely.chiffres.fx.display.OperandDisplay;
import de.bernd_michaely.chiffres.fx.display.ProgressDisplay;
import de.bernd_michaely.chiffres.fx.display.ScaleBox;
import de.bernd_michaely.chiffres.fx.display.SolutionDisplay;
import de.bernd_michaely.chiffres.fx.table.SolutionDisplayTable;
import de.bernd_michaely.chiffres.fx.util.TextFactory;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;
import static javafx.scene.control.TabPane.TabDragPolicy.REORDER;

/**
 * Class for controlling calculation tabs in main window.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class CalculationRunner
{
	private static final Logger logger = Logger.getLogger(CalculationRunner.class.getName());

	private final TabPane tabPane;
	private final Runnable updateSortButtonState;

	public CalculationRunner(Runnable updateSortButtonState)
	{
		this.updateSortButtonState = updateSortButtonState;
		this.tabPane = new TabPane();
		this.tabPane.setTabDragPolicy(REORDER);
	}

	private static Region createTextPane(String text)
	{
		final TextFactory textFactory = new TextFactory(Color.FIREBRICK, 18);
		final Region textNode = textFactory.createTextNode(text);
		final VBox vBox = new VBox(textNode);
		VBox.setMargin(textNode, new Insets(8));
		vBox.setAlignment(Pos.CENTER);
		return vBox;
	}

	private CalculationOperandsDisplay createOperandsDisplay(
		TabReference tabReference, CalculationCtrlParams params)
	{
		final Tab tab = tabReference.getTab();
		final TabUserData tabUserData = getTabUserData(tab);
		final CalculationDisplay calculationDisplay = tabUserData.getCalculationDisplay();
		final CalculationOperandsDisplay operandsDisplay =
			new CalculationOperandsDisplay(params.getNumOperands(),
				calculationDisplay.getMaxNumDigitsTarget(),
				calculationDisplay.getMaxNumDigitsOperand());
		operandsDisplay.getOperandDisplayTarget().setText("" + params.getTarget());
		for (int i = 0; i < params.getNumOperands(); i++)
		{
			operandsDisplay.getOperandDisplay(i).setText("" + params.getOperand(i));
		}
		operandsDisplay.setAllOperandsVisible();
		return operandsDisplay;
	}

	private SolutionDisplay createSolutionDisplay(
		TabReference tabReference, CalculationCtrlParams params)
	{
		final Tab tab = tabReference.getTab();
		final TabUserData tabUserData = getTabUserData(tab);
		final SolutionDisplay solutionDisplay = new SolutionDisplayTable(params);
		tabUserData.setSolutionDisplay(solutionDisplay);
		return solutionDisplay;
	}

	private Tab getCurrentTab()
	{
		return (this.tabPane.getTabs().isEmpty()) ?
			null : this.tabPane.getSelectionModel().getSelectedItem();
	}

	private void onCalculationStart(TabReference tabReference, CalculationCtrlParams params)
	{
		final Tab tab = tabReference.getTab();
		final TabUserData tabUserData = getTabUserData(tab);
		tabUserData.setCalculationCtrlParams(params);
		logger.log(Level.FINE, "Number of calculation threads : {0}", params.getNumThreads());
		logger.log(Level.FINE, "Showing intermediate results  : {0}", params.isShowingIntermediateResult());
		final Calculator calculator = new Calculator(new CalculationParams(
			params.getTarget(), params.getOperands()));
		calculator.setNumThreads(params.getNumThreads());
		if (tabUserData.isCalculationDisplayExtended() && !params.isShowingIntermediateResult())
		{
			final Consumer<CalculationResult> consumerResult = result ->
			{
				if (!result.isCancelled())
				{
					final CalculationOperandsDisplay operandsDisplay = createOperandsDisplay(
						tabReference, params);
					final SolutionDisplay solutionDisplay = createSolutionDisplay(tabReference, params);
					final BorderPane borderPane = new BorderPane(solutionDisplay.getDisplay());
					borderPane.setTop(new ScaleBox(operandsDisplay.getDisplay()).getDisplay());
					tab.setContent(borderPane);
					if (result.isExactSolutionFound())
					{
						solutionDisplay.setSolutions(result.getSolutions());
						operandsDisplay.getOperandDisplayTarget().setDisplayState(
							OperandDisplay.DisplayState.SUCCESS);
					}
					else
					{
						operandsDisplay.getOperandDisplayTarget().setDisplayState(
							OperandDisplay.DisplayState.ERROR);
						runApproximativeCalculation(result, params, tabUserData, borderPane);
					}
					updateSortButtonState.run();
				}
			};
			final ProgressDisplay progressDisplay =
				tabUserData.getCalculationDisplayExtended().getProgressDisplay();
			final Task<CalculationResult> task = new CalculatorTask(calculator,
				consumerResult, progressDisplay.progressProperty());
			progressDisplay.setOnCancel(() -> task.cancel());
			CompletableFuture.runAsync(task);
		}
		else
		{
			final CalculationOperandsDisplay operandsDisplay = createOperandsDisplay(tabReference, params);
			final SolutionDisplay solutionDisplay = createSolutionDisplay(tabReference, params);
			final BorderPane borderPane = new BorderPane(solutionDisplay.getDisplay());
			borderPane.setTop(new ScaleBox(operandsDisplay.getDisplay()).getDisplay());
			tab.setContent(borderPane);
			if (params.isShowingIntermediateResult())
			{
				calculator.setOnIntermediateResult(solutionCandidate ->
					solutionDisplay.handleIntermediateResult(solutionCandidate));
			}
			final Consumer<CalculationResult> consumerResult = result ->
			{
				if (result.isExactSolutionFound())
				{
					solutionDisplay.setSolutions(result.getSolutions());
					operandsDisplay.getOperandDisplayTarget().setDisplayState(
						OperandDisplay.DisplayState.SUCCESS);
				}
				else
				{
					operandsDisplay.getOperandDisplayTarget().setDisplayState(
						OperandDisplay.DisplayState.ERROR);
					runApproximativeCalculation(result, params, tabUserData, borderPane);
				}
				updateSortButtonState.run();
			};
			final Task<CalculationResult> task = new CalculatorTask(calculator,
				consumerResult, solutionDisplay.progressProperty());
			solutionDisplay.setOnCancel(() ->
			{
				task.cancel();
				solutionDisplay.cancel();
			});
			CompletableFuture.runAsync(task);
		}
	}

	private void runApproximativeCalculation(CalculationResult result,
		CalculationCtrlParams params, TabUserData tabUserData, BorderPane borderPane)
	{
		final String titleLowerApproximation = "Lower Approximation";
		final String titleUpperApproximation = "Upper Approximation";
		final boolean isSmallerPossible = result.isLowerApproximationPossible();
		final boolean isGreaterPossible = result.isUpperApproximationPossible();
		final int target = params.getTarget();
		final int diffLess = result.getDiffLess();
		final int valueLess = target - diffLess;
		final int diffGreater = result.getDiffGreater();
		final int valueGreater = target + diffGreater;
		final String titleSmaller;
		final String titleGreater;
		final SolutionDisplayTable solutionDisplaySmaller, solutionDisplayGreater;
		final Region contentSmaller, contentGreater;
		final Task<CalculationResult> taskSmaller, taskGreater;
		if (isSmallerPossible)
		{
			titleSmaller = titleLowerApproximation +
				" (" + valueLess + " = " + target + " - " + diffLess + ")";
			solutionDisplaySmaller = new SolutionDisplayTable(
				new CalculationCtrlParams(valueLess, params));
			contentSmaller = solutionDisplaySmaller.getDisplay();
			final Calculator calculator = new Calculator(
				new CalculationParams(valueLess, params.getOperands()));
			final Consumer<CalculationResult> consumer = resultSmaller ->
			{
				solutionDisplaySmaller.setSolutions(resultSmaller.getSolutions());
			};
			taskSmaller = new CalculatorTask(calculator, consumer,
				solutionDisplaySmaller.progressProperty());
			solutionDisplaySmaller.setOnCancel(() ->
			{
				taskSmaller.cancel();
				solutionDisplaySmaller.cancel();
			});
		}
		else
		{
			titleSmaller = titleLowerApproximation + " :-(";
			solutionDisplaySmaller = null;
			contentSmaller = createTextPane("A lower approximation is not possible.");
			taskSmaller = null;
		}
		if (isGreaterPossible)
		{
			titleGreater = titleUpperApproximation +
				" (" + valueGreater + " = " + target + " + " + diffGreater + ")";
			solutionDisplayGreater = new SolutionDisplayTable(
				new CalculationCtrlParams(valueGreater, params));
			contentGreater = solutionDisplayGreater.getDisplay();
			final Calculator calculator = new Calculator(
				new CalculationParams(valueGreater, params.getOperands()));
			final Consumer<CalculationResult> consumer = resultGreater ->
			{
				solutionDisplayGreater.setSolutions(resultGreater.getSolutions());
			};
			taskGreater = new CalculatorTask(calculator, consumer,
				solutionDisplayGreater.progressProperty());
			solutionDisplayGreater.setOnCancel(() ->
			{
				taskGreater.cancel();
				solutionDisplayGreater.cancel();
			});
		}
		else
		{
			titleGreater = titleUpperApproximation + " :-(";
			solutionDisplayGreater = null;
			contentGreater = createTextPane("An upper approximation is not possible.");
			taskGreater = null;
		}
		final Tab tabSmaller = new Tab(titleSmaller, contentSmaller);
		tabSmaller.setUserData(new TabUserData(solutionDisplaySmaller, isSmallerPossible));
		final Tab tabGreater = new Tab(titleGreater, contentGreater);
		tabGreater.setUserData(new TabUserData(solutionDisplayGreater, isGreaterPossible));
		final Tab tabFirst, tabSecond;
		final TabPane tabPaneApprox = new TabPane();
		tabPaneApprox.setTabClosingPolicy(UNAVAILABLE);
		final boolean smallerFirst = diffLess <= diffGreater;
		final boolean bothApproxPossible = isSmallerPossible && isGreaterPossible;
		final Task<CalculationResult> task1, task2;
		if (smallerFirst)
		{
			tabFirst = tabSmaller;
			tabSecond = tabGreater;
			task1 = taskSmaller;
			task2 = bothApproxPossible ? taskGreater : null;
		}
		else
		{
			tabFirst = tabGreater;
			tabSecond = tabSmaller;
			tabPaneApprox.getTabs().setAll(tabGreater, tabSmaller);
			task1 = taskGreater;
			task2 = bothApproxPossible ? taskSmaller : null;
		}
		tabPaneApprox.getTabs().setAll(tabFirst, tabSecond);
		tabPaneApprox.getSelectionModel().selectedItemProperty().addListener(
			(observable, tabPrevious, tabCurrent) -> updateSortButtonState.run());
		borderPane.setCenter(tabPaneApprox);
		tabUserData.setTabPaneApprox(tabPaneApprox);
		if (bothApproxPossible)
		{
			if (params.getNumThreads() > 1)
			{
				// if using more than one CPU core, run one task after the other:
				CompletableFuture.runAsync(task1).thenRunAsync(task2);
			}
			else
			{
				// run both approximation tasks concurrent like in version 1:
				CompletableFuture.allOf(
					CompletableFuture.runAsync(task1),
					CompletableFuture.runAsync(task2));
			}
		}
		else
		{
			if (task1 != null)
			{
				CompletableFuture.runAsync(task1);
			}
			if (task2 != null)
			{
				CompletableFuture.runAsync(task2);
			}
		}
	}

	void cloneCurrentTab()
	{
		final TabUserData tabUserData = getTabUserData(getCurrentTab());
		if (tabUserData.isCalculationCtrl())
		{
			final CalculationCtrlParams calculationCtrlParams = tabUserData.
				getCalculationCtrl().getCalculationCtrlParams();
			final CalculationDisplay calculationDisplay;
			if (tabUserData.isExtendedMode())
			{
				calculationDisplay = new CalculationDisplayExtended(
					calculationCtrlParams);
			}
			else
			{
				calculationDisplay = new CalculationDisplayStandard(
					calculationCtrlParams.getOperands());
			}
			startNewCalculation(calculationDisplay);
		}
	}

	SolutionDisplayTable getCurrentSolutionDisplayTable()
	{
		final TabUserData tabUserData = getTabUserData(getCurrentSolutionTab());
		return (tabUserData != null) ? tabUserData.getSolutionDisplayTable() : null;
	}

	Tab getCurrentSolutionTab()
	{
		final Tab currentTab = getCurrentTab();
		final TabUserData tabUserData = getTabUserData(currentTab);
		if (tabUserData != null)
		{
			final TabPane tabPaneApprox = tabUserData.getTabPaneApprox();
			if (tabPaneApprox != null)
			{
				try
				{
					return tabPaneApprox.getSelectionModel().selectedItemProperty().get();
				}
				catch (NullPointerException ex)
				{
					return null;
				}
			}
			else if (tabUserData.isSolutionDisplayTable())
			{
				return currentTab;
			}
		}
		return null;
	}

	TabPane getTabPane()
	{
		return tabPane;
	}

	/**
	 * Returns the TabUserData for the given tab.
	 *
	 * @param tab the given tab
	 * @return never null, but {@link  CalculationTabInfo.EMPTY} instead
	 * @see TabUserData#isEmpty()
	 */
	TabUserData getTabUserData(Tab tab)
	{
		if (tab != null)
		{
			final Object userData = tab.getUserData();
			if (userData instanceof TabUserData)
			{
				return ((TabUserData) userData);
			}
		}
		return TabUserData.EMPTY;
	}

	void onTabClose(Tab tab)
	{
		final TabUserData tabUserData = getTabUserData(tab);
		final CalculationCtrl calculationCtrl = tabUserData.getCalculationCtrl();
		if (calculationCtrl != null)
		{
			calculationCtrl.onClose();
		}
	}

	void startNewCalculation(CalculationDisplay calculationDisplay)
	{
		final int counter = this.tabPane.getTabs().stream().
			map(tab -> tab.getUserData()).
			filter(userData -> userData instanceof TabUserData).
			mapToInt(userData -> ((TabUserData) userData).getTabIndex()).
			max().orElse(0) + 1;
		final Tab tab = new Tab("Calculation " + counter, calculationDisplay.getDisplay());
		tab.setOnCloseRequest(event -> onTabClose(tab));
		calculationDisplay.setOnStart(new TabReference(tab), this::onCalculationStart);
		tab.setUserData(new TabUserData(counter, calculationDisplay));
		this.tabPane.getTabs().add(tab);
		this.tabPane.getSelectionModel().select(tab);
		calculationDisplay.requestInitalFocus();
	}

	void startNewEmptyCalculation()
	{
		startNewCalculation(new CalculationDisplayStandard(false));
	}
}
