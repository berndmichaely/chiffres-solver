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

import de.bernd_michaely.chiffres.common.fx.beans.binding.ObservableListEmptyBinding;
import de.bernd_michaely.chiffres.common.fx.util.TrackableWindowState;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplayExtended;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplayStandard;
import de.bernd_michaely.chiffres.fx.display.ScaleBox;
import de.bernd_michaely.chiffres.fx.info.InfoPane;
import de.bernd_michaely.chiffres.fx.info.PaneInfoAbout;
import de.bernd_michaely.chiffres.fx.table.SolutionDisplayTable;
import de.bernd_michaely.chiffres.fx.util.IconOptions;
import de.bernd_michaely.chiffres.fx.util.ResizableDialog;
import de.bernd_michaely.chiffres.fx.util.TextFactory;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static de.bernd_michaely.chiffres.fx.JChiffresFXApplication.TITLE_APPLICATION;
import static de.bernd_michaely.chiffres.fx.mainwindow.PreferencesKeys.*;
import static de.bernd_michaely.chiffres.fx.util.MathSymbol.DEFAULT_FONT_SIZE;

/**
 * Main JavaFX GUI class for the JChiffresFX application.
 *
 * @author Bernd Michaely
 */
public class JChiffresFX implements ApplicationMainWindow
{
	private static final Logger logger = Logger.getLogger(JChiffresFX.class.getName());
	public static final String SUBTITLE_APPLICATION = "A Chiffres Game Solver";
	private static final String TITLE_WINDOW = TITLE_APPLICATION + " - " + SUBTITLE_APPLICATION;
	private static final DropShadow dropShadow = new DropShadow();
	private final MenuItem menuItemOptionsDefaultMode;
	private final MenuItem menuItemSort;
	private final MenuItem menuItemSaveImage;
	private final MenuItem menuItemSaveCSV;
	private final MenuItem menuItemInfoAbout;
	private final Button buttonTabNew;
	private final Button buttonTabRandom;
	private final Button buttonTabExtended;
	private final Button buttonTabClone;
	private final Button buttonSortForward;
	private final Button buttonSortBackward;
	private final Button buttonSortSelect;
	private final BorderPane root;
	private final CalculationRunner calculationRunner;
	private final ObservableListEmptyBinding tabPaneEmptyBinding;
	private TrackableWindowState windowState;
	private ResizableDialog dialogInfoAbout;
	private final Region defaultWindowLogo;

	private enum OnStartOptions
	{
		NO_ACTION, OPEN_STD_TAB, OPEN_EXT_TAB;

		private static OnStartOptions getDefaultStartOption()
		{
			return NO_ACTION;
		}
	}

	public JChiffresFX()
	{
		this.root = new BorderPane();
		this.defaultWindowLogo = createDefaultWindowLogo();
		this.menuItemOptionsDefaultMode = new MenuItem("Options for standard game …");
		this.menuItemSort = new MenuItem("Reset main tab sequence");
		this.menuItemSaveImage = new MenuItem("Save selected graph as Image …");
		this.menuItemSaveCSV = new MenuItem("Save solution table as CSV …");
		this.menuItemInfoAbout = new MenuItem("Info about …");
		this.buttonSortForward = new Button();
		this.buttonSortBackward = new Button();
		this.buttonSortSelect = new Button();
		this.buttonTabNew = new Button();
		this.buttonTabRandom = new Button();
		this.buttonTabExtended = new Button();
		this.buttonTabClone = new Button();
		this.calculationRunner = new CalculationRunner(this::updateSortButtonState);
		this.tabPaneEmptyBinding = new ObservableListEmptyBinding(
			calculationRunner.getTabPane().getTabs());
	}

	@Override
	public void init() throws Exception
	{
		this.buttonTabNew.setText("Standard _Game");
		this.buttonTabNew.setTooltip(new Tooltip(
			"Open a new empty calculation tab for a game according to standard rules"));
		this.buttonTabNew.setOnAction(event -> calculationRunner.startNewEmptyCalculation());
		this.buttonTabRandom.setText("_Random Game");
		this.buttonTabRandom.setTooltip(new Tooltip(
			"Open a new calculation tab with random operands"));
		this.buttonTabRandom.setOnAction(event ->
			calculationRunner.startNewCalculation(new CalculationDisplayStandard(true)));
		this.buttonTabExtended.setText("_Extended Mode");
		this.buttonTabExtended.setTooltip(new Tooltip(
			"Open a new empty calculation tab with extended options"));
		this.buttonTabExtended.setOnAction(event ->
			calculationRunner.startNewCalculation(new CalculationDisplayExtended()));
		this.buttonTabClone.setText("Cl_one Tab");
		this.buttonTabClone.setTooltip(new Tooltip(
			"Open a new calculation tab with a copy of the current operands"));
		this.buttonTabClone.setOnAction(event -> calculationRunner.cloneCurrentTab());
		this.buttonSortForward.setText("Sort _first … last");
		this.buttonSortForward.setTooltip(new Tooltip(
			"Sort table from first operation in first order to last operation in last order"));
		this.buttonTabClone.setDisable(true);
		this.buttonSortForward.setOnAction(event ->
		{
			final SolutionDisplayTable sdt = calculationRunner.getCurrentSolutionDisplayTable();
			if (sdt != null)
			{
				sdt.sortForward();
			}
		});
		this.buttonSortBackward.setText("Sort _last … first");
		this.buttonSortBackward.setTooltip(new Tooltip(
			"Sort table from last operation in first order to first operation in last order"));
		this.buttonSortBackward.setOnAction(event ->
		{
			final SolutionDisplayTable sdt = calculationRunner.getCurrentSolutionDisplayTable();
			if (sdt != null)
			{
				sdt.sortBackward();
			}
		});
		this.buttonSortSelect.setText("Sort _Sel.");
		this.buttonSortSelect.setTooltip(new Tooltip(
			"Sort table to display selected solutions first in indexed order"));
		this.buttonSortSelect.setOnAction(event ->
		{
			final SolutionDisplayTable sdt = calculationRunner.getCurrentSolutionDisplayTable();
			if (sdt != null)
			{
				sdt.sortBySelection();
			}
		});
		final ToolBar toolBarMain = new ToolBar(
			this.buttonTabNew, this.buttonTabRandom, this.buttonTabExtended, this.buttonTabClone,
			this.buttonSortForward, this.buttonSortBackward, this.buttonSortSelect);
		initializeButtons(toolBarMain);
		this.menuItemOptionsDefaultMode.setOnAction(event -> showDialogOptionsStandardGame());
		this.menuItemSort.setOnAction(event ->
			calculationRunner.getTabPane().getTabs().sort(
				(tab1, tab2) -> Integer.compare(
					calculationRunner.getTabUserData(tab1).getTabIndex(),
					calculationRunner.getTabUserData(tab2).getTabIndex())
			));
		menuItemSaveImage.setOnAction(event -> showDialogOptionsSaveImage());
		menuItemSaveCSV.setOnAction(event ->
			createSolutionFileOutput().saveSolutionsAsCSV(menuItemSaveCSV.getText()));
		menuItemInfoAbout.setOnAction(event -> showInfoAboutWindow());
		final MenuButton buttonOptions = new MenuButton();
		buttonOptions.setGraphic(new IconOptions().getNode());
		buttonOptions.getItems().addAll(
			createSubMenuOnStart(),
			this.menuItemOptionsDefaultMode,
			this.menuItemSort,
			new SeparatorMenuItem(), menuItemSaveImage, menuItemSaveCSV,
			new SeparatorMenuItem(), menuItemInfoAbout
		);
		buttonOptions.setOnShowing(e -> handleInfoAbout());
		final ToolBar toolBarOptions = new ToolBar(buttonOptions);
		toolBarOptions.setMaxHeight(Double.MAX_VALUE);
		final BorderPane paneToolBars = new BorderPane(toolBarMain);
		paneToolBars.setRight(toolBarOptions);
		this.root.setTop(paneToolBars);
		this.root.setCenter(this.defaultWindowLogo);
		calculationRunner.getTabPane().getSelectionModel().selectedItemProperty().addListener(
			(observable, tabPrevious, tabCurrent) ->
		{
			final TabUserData tabUserData = calculationRunner.getTabUserData(tabCurrent);
			if (tabUserData.isCalculationDisplay())
			{
				this.buttonTabClone.disableProperty().unbind();
				this.buttonTabClone.disableProperty().bind(
					tabUserData.getCalculationDisplay().operandArrayCompleteProperty().not());
			}
			else if (tabUserData.isSolutionDisplay())
			{
				this.buttonTabClone.disableProperty().unbind();
				this.buttonTabClone.disableProperty().set(false);
			}
			else
			{
				this.buttonTabClone.disableProperty().unbind();
				this.buttonTabClone.disableProperty().set(true);
			}
			updateSortButtonState();
		});
		this.tabPaneEmptyBinding.addListener((observable, oldValue, newValue) ->
			this.root.setCenter(newValue ? this.defaultWindowLogo : calculationRunner.getTabPane()));
		switch (getOnStartOption())
		{
			case OPEN_STD_TAB -> calculationRunner.startNewEmptyCalculation();
			case OPEN_EXT_TAB ->
				calculationRunner.startNewCalculation(new CalculationDisplayExtended());
			case NO_ACTION ->
			{
			}
			default -> logger.log(Level.WARNING, "Unknown OnStartOption");
		}
		updateSortButtonState();
	}

	@Override
	public void start(Stage primaryStage)
	{
		Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) ->
			logger.log(Level.WARNING,
				"Thread »" + thread + "« - UncaughtExceptionHandler throwing", throwable));
		this.windowState = new TrackableWindowState(primaryStage, this.root, 1000, 900);
		StageIcon.addStageIcon(this.windowState.getStage());
		primaryStage.setTitle(TITLE_WINDOW);
		primaryStage.setOnCloseRequest(event ->
		{
			if ((this.dialogInfoAbout != null) && this.dialogInfoAbout.getDialog().isShowing())
			{
				this.dialogInfoAbout.close();
			}
			calculationRunner.getTabPane().getTabs().forEach(calculationRunner::onTabClose);
		});
	}

	private static Region createDefaultWindowLogo()
	{
		final TextFactory textFactory1, textFactory2;
		textFactory1 = new TextFactory(
			Color.STEELBLUE, (int) (DEFAULT_FONT_SIZE * 64 / 13));
		textFactory2 = new TextFactory(
			Color.STEELBLUE, (int) (DEFAULT_FONT_SIZE * 35 / 13));
		final VBox vBox = new VBox(DEFAULT_FONT_SIZE * 20 / 13,
			textFactory1.createTextNode(TITLE_APPLICATION),
			textFactory2.createTextNode(SUBTITLE_APPLICATION));
		vBox.setAlignment(Pos.CENTER);
		vBox.setFillWidth(false);
		return new ScaleBox(new BorderPane(new Group(vBox))).getDisplay();
	}

	private void handleInfoAbout()
	{
		menuItemSort.setDisable(calculationRunner.getTabPane().getTabs().size() < 2);
		final SolutionDisplayTable sdt = calculationRunner.getCurrentSolutionDisplayTable();
		final Optional<Integer> selection = (sdt != null) ?
			sdt.getSelectedGraphSolutionRowIndex() : Optional.empty();
		menuItemSaveImage.setDisable(selection.isEmpty());
		menuItemSaveCSV.setDisable(sdt == null);
	}

	private static OnStartOptions getOnStartOption()
	{
		final int idx = preferences.getInt(ID_PREF_OPTION_ON_START.key(), -1);
		return ((idx >= 0) && (idx < OnStartOptions.values().length)) ?
			OnStartOptions.values()[idx] : OnStartOptions.getDefaultStartOption();
	}

	private Menu createSubMenuOnStart()
	{
		final int idxNoAction = OnStartOptions.NO_ACTION.ordinal();
		final int idxOpenStdTab = OnStartOptions.OPEN_STD_TAB.ordinal();
		final int idxOpenExtTab = OnStartOptions.OPEN_EXT_TAB.ordinal();
		final ToggleGroup toggleGroupOnStart = new ToggleGroup();
		final int onStartIndex = getOnStartOption().ordinal();
		final RadioMenuItem menuItemOnStartNothing = new RadioMenuItem("Don't open any tabs");
		menuItemOnStartNothing.setToggleGroup(toggleGroupOnStart);
		menuItemOnStartNothing.setSelected(onStartIndex == idxNoAction);
		menuItemOnStartNothing.setOnAction(e -> preferences.putInt(
			ID_PREF_OPTION_ON_START.key(), idxNoAction));
		final RadioMenuItem menuItemOnStartStandard = new RadioMenuItem("Open standard game tab");
		menuItemOnStartStandard.setToggleGroup(toggleGroupOnStart);
		menuItemOnStartStandard.setSelected(onStartIndex == idxOpenStdTab);
		menuItemOnStartStandard.setOnAction(e -> preferences.putInt(
			ID_PREF_OPTION_ON_START.key(), idxOpenStdTab));
		final RadioMenuItem menuItemOnStartExtended = new RadioMenuItem("Open extended mode tab");
		menuItemOnStartExtended.setToggleGroup(toggleGroupOnStart);
		menuItemOnStartExtended.setSelected(onStartIndex == idxOpenExtTab);
		menuItemOnStartExtended.setOnAction(e -> preferences.putInt(
			ID_PREF_OPTION_ON_START.key(), idxOpenExtTab));
		final Menu menuOnApplicationStart = new Menu("On application start");
		menuOnApplicationStart.getItems().addAll(menuItemOnStartNothing,
			menuItemOnStartStandard, menuItemOnStartExtended);
		return menuOnApplicationStart;
	}

	private SolutionFileOutput createSolutionFileOutput()
	{
		return new SolutionFileOutput(windowState.getStage(),
			calculationRunner.getCurrentSolutionDisplayTable());
	}

	private void showDialogOptionsStandardGame()
	{
		final var pane = new PaneOptionsStandardGame("Options for the standard game");
		showDialog("Options", pane, pane::saveValues);
	}

	private void showDialogOptionsSaveImage()
	{
		final SolutionDisplayTable sdt = calculationRunner.getCurrentSolutionDisplayTable();
		final var pane = new PaneOptionsSaveImage("File format options",
			sdt.getCalculationCtrlParams().getNumOperands(),
			sdt.getSelectedGraphSolution());
		if (showDialog("Image options", pane, pane::saveValues))
		{
			createSolutionFileOutput().saveGraph(menuItemSaveImage.getText(),
				pane.getSelectedFileFormat(), pane.isTransparentFill(), pane.getFontSize());
		}
	}

	private boolean showDialog(String title, InfoPane pane, Runnable onOkAction)
	{
		final ResizableDialog dialog = new ResizableDialog(
			windowState.getStage(), title,
			Modality.APPLICATION_MODAL, pane.getDisplay());
		dialog.setOkCancelButtons(onOkAction);
		StageIcon.addStageIcon(dialog.getDialog());
		dialog.getDialog().showAndWait();
		return dialog.isCommitted();
	}

	private void showInfoAboutWindow()
	{
		if (this.dialogInfoAbout == null)
		{
			this.dialogInfoAbout = new ResizableDialog(
				this.windowState.getStage(), this.menuItemInfoAbout.getText(),
				Modality.NONE, new PaneInfoAbout().getDisplay());
			this.dialogInfoAbout.setCloseButton();
			StageIcon.addStageIcon(this.dialogInfoAbout.getDialog());
			this.dialogInfoAbout.getDialog().setOnCloseRequest(e -> this.dialogInfoAbout = null);
		}
		this.dialogInfoAbout.getDialog().show();
		if (this.dialogInfoAbout.getDialog().isIconified())
		{
			this.dialogInfoAbout.getDialog().setIconified(false);
		}
	}

	private void initializeButtons(ToolBar toolBar)
	{
		toolBar.getItems().stream()
			.filter(item -> item instanceof Button)
			.forEach(button ->
			{
				button.setOnMouseEntered(event -> button.setEffect(dropShadow));
				button.setOnMouseExited(event -> button.setEffect(null));
			});
	}

	private void updateSortButtonState()
	{
		final Tab tab = calculationRunner.getCurrentSolutionTab();
		final boolean isSortable = (tab != null) ?
			calculationRunner.getTabUserData(tab).isShowingSolutions() : false;
		final boolean disableSort = !isSortable;
		this.buttonSortForward.setDisable(disableSort);
		this.buttonSortBackward.setDisable(disableSort);
		this.buttonSortSelect.setDisable(disableSort);
	}
}
