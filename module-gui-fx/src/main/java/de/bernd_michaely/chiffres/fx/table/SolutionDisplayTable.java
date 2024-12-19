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
package de.bernd_michaely.chiffres.fx.table;

import de.bernd_michaely.chiffres.calc.Operation;
import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.calc.SolutionCandidate;
import de.bernd_michaely.chiffres.fx.display.CalculationCtrlParams;
import de.bernd_michaely.chiffres.fx.display.ProgressDisplay;
import de.bernd_michaely.chiffres.fx.display.ProgressDisplay.ProgressDisplayMode;
import de.bernd_michaely.chiffres.fx.display.SolutionDisplay;
import de.bernd_michaely.chiffres.fx.graph.SolutionGraph;
import de.bernd_michaely.chiffres.fx.graph.SolutionGraphShape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import static javafx.scene.control.TabPane.TabClosingPolicy.ALL_TABS;
import static javafx.scene.control.TableColumn.SortType.*;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN;

/**
 * Class for displaying solutions of a calculation in a table view.
 *
 * @author Bernd Michaely
 */
public class SolutionDisplayTable implements SolutionDisplay
{
	private static final int WIDTH_COLUMN_INDEX = 60;
	private static final int MAX_WIDTH_COLUMN_INDEX = 2 * WIDTH_COLUMN_INDEX;
	private static final int WIDTH_COLUMN_SELECTION = WIDTH_COLUMN_INDEX;
	private static final int MIN_WIDTH_COLUMN_OPERATION = 140;
	private static final int PREF_WIDTH_COLUMN_OPERATION = 175;
	private static final int MAX_WIDTH_COLUMN_OPERATION = 2 * PREF_WIDTH_COLUMN_OPERATION;
	private static final int PREF_WIDTH_COLUMNS_FIXED = WIDTH_COLUMN_INDEX + WIDTH_COLUMN_SELECTION;
	private final int numOperands;
	private final BorderPane pane;
	private final TableView<SolutionRow> tableViewResult;
	private final TableColumn<SolutionRow, Number> tableColumnIndex;
	private final TableColumn<SolutionRow, Boolean> tableColumnSelectRow;
	private final List<TableColumn<SolutionRow, Operation>> listOperationTableColumns;
	private final TabPane tabPaneGraph;
	private final SolutionGraph graphSelectedRow;
	private boolean isUserRowSelection;
	private final CalculationCtrlParams calculationCtrlParams;
	private final ProgressDisplay progressDisplay;
	// for handling intermediate results:
	private final ScheduledExecutorService scheduledExecutorService;
	private final BlockingQueue<SolutionCandidate> queueIntermediateResults;
	private final SortedMap<Solution.EquivalenceClass, Integer> mapSolutions;

	public SolutionDisplayTable(CalculationCtrlParams calculationCtrlParams)
	{
		this.calculationCtrlParams = calculationCtrlParams;
		this.numOperands = calculationCtrlParams.getNumOperands();
		this.graphSelectedRow = createSolutionGraph();
		this.tableViewResult = new TableView<>();
		this.tableViewResult.setEditable(true);
		this.tableViewResult.setMinHeight(100);
		final int widthMaxTable = PREF_WIDTH_COLUMNS_FIXED +
			(this.numOperands - 1) * MAX_WIDTH_COLUMN_OPERATION;
		this.tableViewResult.setMaxWidth(widthMaxTable);
		this.tableViewResult.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		// create TableView
		this.tableColumnIndex = new TableColumn<>("Nr.");
		this.tableColumnIndex.setEditable(false);
		this.tableColumnIndex.setCellValueFactory(cellDataFeatures ->
			cellDataFeatures.getValue().indexProperty());
		this.tableColumnIndex.setCellFactory(col -> new IndexTableCell());
		this.tableColumnIndex.setMinWidth(WIDTH_COLUMN_INDEX);
		this.tableColumnIndex.setPrefWidth(WIDTH_COLUMN_INDEX);
		this.tableColumnIndex.setMaxWidth(MAX_WIDTH_COLUMN_INDEX);
//		this.tableColumnIndex.setResizable(false);
		this.tableViewResult.getColumns().add(this.tableColumnIndex);
		this.listOperationTableColumns = new ArrayList<>(this.numOperands - 1);
		for (int i = 1; i < this.numOperands; i++)
		{
			final int colIndex = i - 1;
			final TableColumn<SolutionRow, Operation> tableColumn =
				new TableColumn<>("Operation " + i);
			tableColumn.setEditable(false);
			tableColumn.setCellValueFactory(cellDataFeatures ->
				cellDataFeatures.getValue().operationProperty(colIndex));
			tableColumn.setCellFactory(col -> new OperationTableCell());
			tableColumn.setMinWidth(MIN_WIDTH_COLUMN_OPERATION);
			tableColumn.setPrefWidth(PREF_WIDTH_COLUMN_OPERATION);
			tableColumn.setMaxWidth(MAX_WIDTH_COLUMN_OPERATION);
			tableColumn.setResizable(true);
			this.tableViewResult.getColumns().add(tableColumn);
			this.listOperationTableColumns.add(tableColumn);
		}
		this.tableColumnSelectRow = new TableColumn<>("Sel.");
		this.tableColumnSelectRow.setEditable(true);
		this.tableColumnSelectRow.setCellFactory(callback -> new SelectionTableCell(
			rowIndex -> tableViewResult.getItems().get(rowIndex).getSelectionProperty()));
		this.tableColumnSelectRow.setCellValueFactory(
			cellDataFeatures -> cellDataFeatures.getValue().getSelectionProperty());
		this.tableColumnSelectRow.setMinWidth(WIDTH_COLUMN_SELECTION);
		this.tableColumnSelectRow.setPrefWidth(WIDTH_COLUMN_SELECTION);
		this.tableColumnSelectRow.setMaxWidth(WIDTH_COLUMN_SELECTION);
		this.tableColumnSelectRow.setResizable(false);
		this.tableViewResult.getColumns().add(this.tableColumnSelectRow);
		this.tableColumnSelectRow.setVisible(!calculationCtrlParams.isShowingIntermediateResult());
		this.tableViewResult.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.tableViewResult.setPlaceholder(new Label("Calculating ..."));
		// create graph view
		this.tabPaneGraph = new TabPane();
		this.tabPaneGraph.setTabClosingPolicy(ALL_TABS);
		this.tabPaneGraph.getSelectionModel().selectedItemProperty().addListener(
			(observable, oldValue, newValue) ->
		{
			if (!isUserRowSelection)
			{
				if ((oldValue != null) && (newValue != null))
				{
					final GraphTabInfo graphTabInfo = getGraphTabInfo(newValue);
					if (graphTabInfo != null)
					{
						if (!tableViewResult.getSortOrder().contains(tableColumnSelectRow))
						{
							final int tableViewIndex = findBySolutionRowIndex(graphTabInfo.getSolutionRowIndex());
							tableViewResult.scrollTo(tableViewIndex >= 0 ? tableViewIndex : 0);
						}
					}
				}
			}
		});
		// create outer pane
		this.pane = new BorderPane(this.tableViewResult);
		if (calculationCtrlParams.isShowingIntermediateResult())
		{
			this.queueIntermediateResults = new LinkedBlockingQueue<>();
			this.mapSolutions = new TreeMap<>();
			this.progressDisplay = new ProgressDisplay(ProgressDisplayMode.STATUSBAR);
			this.pane.setBottom(this.progressDisplay.getDisplay());
			this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			this.scheduledExecutorService.scheduleAtFixedRate(
				this::handleIntermediateResults, 1000, 500, TimeUnit.MILLISECONDS);
		}
		else
		{
			this.queueIntermediateResults = null;
			this.mapSolutions = null;
			this.scheduledExecutorService = null;
			this.progressDisplay = new ProgressDisplay(ProgressDisplayMode.PLACEHOLDER);
			this.tableViewResult.setPlaceholder(this.progressDisplay.getDisplay());
		}
	}

	@Override
	public DoubleProperty progressProperty()
	{
		return this.progressDisplay.progressProperty();
	}

	private void shutdownScheduledService(boolean now)
	{
		if (this.scheduledExecutorService != null)
		{
			if (now)
			{
				this.scheduledExecutorService.shutdownNow();
			}
			else
			{
				this.scheduledExecutorService.shutdown();
			}
			boolean terminated = false;
			while (!terminated)
			{
				try
				{
					terminated = this.scheduledExecutorService.awaitTermination(1, TimeUnit.MINUTES);
				}
				catch (InterruptedException ex)
				{
					terminated = false; // recheck condition
				}
			}
		}
	}

	@Override
	public void setOnCancel(Runnable runnable)
	{
		this.progressDisplay.setOnCancel(runnable);
	}

	@Override
	public void cancel()
	{
		this.progressDisplay.cancel();
		if (this.calculationCtrlParams.isShowingIntermediateResult())
		{
			this.tableViewResult.setPlaceholder(new Label(ProgressDisplay.TITLE_CANCELLED));
			shutdownScheduledService(false);
			initTableRowSelectionHandler();
		}
	}

	/**
	 * Returns the current index to the TableView for a given SolutionRow index,
	 * independently of the TableView sort order, or -1, if the index was not
	 * found.
	 *
	 * @param solutionRowIndex the searched SolutionRow index
	 * @return the TableView index for a given SolutionRow index or -1
	 */
	private int findBySolutionRowIndex(int solutionRowIndex)
	{
		final ObservableList<SolutionRow> items = this.tableViewResult.getItems();
		final int size = items.size();
		int i = 0;
		boolean found = false;
		while (!found && (i < size))
		{
			if (items.get(i).getIndex() == solutionRowIndex)
			{
				found = true;
			}
			else
			{
				i++;
			}
		}
		return found ? i : -1;
	}

	private void handleGraphTabCloseRequest()
	{
		final int numTabs = this.tabPaneGraph.getTabs().size();
		setTabPaneGraphEnabled(numTabs > 1);
	}

	private SolutionGraph createSolutionGraph()
	{
		final SolutionGraph solutionGraph = new SolutionGraphShape(this.numOperands);
		solutionGraph.getDisplay().setBackground(BACKGROUND_GRADIENT);
		return solutionGraph;
	}

	private void setTabPaneGraphEnabled(boolean enable)
	{
		final boolean isEnabled = (this.pane.getTop() != null);
		if (enable != isEnabled)
		{
			this.pane.setTop(enable ? this.tabPaneGraph : null);
		}
	}

	public void sortForward()
	{
		final ObservableList<TableColumn<SolutionRow, ?>> sortOrder =
			this.tableViewResult.getSortOrder();
		sortOrder.clear();
		this.tableColumnIndex.setSortType(ASCENDING);
		sortOrder.add(this.tableColumnIndex);
	}

	public void sortBackward()
	{
		final ObservableList<TableColumn<SolutionRow, ?>> sortOrder =
			this.tableViewResult.getSortOrder();
		sortOrder.clear();
		this.listOperationTableColumns.stream().forEach(tableColumn ->
			tableColumn.setSortType(ASCENDING));
		final List<TableColumn<SolutionRow, Operation>> listColumns =
			new ArrayList<>(this.listOperationTableColumns);
		Collections.reverse(listColumns);
		sortOrder.addAll(listColumns);
	}

	public void sortBySelection()
	{
		final ObservableList<TableColumn<SolutionRow, ?>> sortOrder =
			this.tableViewResult.getSortOrder();
		sortOrder.clear();
		this.tableColumnSelectRow.setSortType(DESCENDING);
		this.tableColumnIndex.setSortType(ASCENDING);
		final List<TableColumn<SolutionRow, ?>> listColumns =
			new ArrayList<>(2);
		listColumns.add(this.tableColumnSelectRow);
		listColumns.add(this.tableColumnIndex);
		sortOrder.addAll(listColumns);
		this.tableViewResult.scrollTo(0);
	}

	/**
	 * Returns the graph tab showing the solution for the selected TableView row
	 * or null, if it doesn't exist.
	 *
	 * @return the graph tab showing the solution for the selected TableView row
	 */
	private Tab getGraphTab()
	{
		for (Tab tab : this.tabPaneGraph.getTabs())
		{
			final GraphTabInfo tabInfo = getGraphTabInfo(tab);
			if ((tabInfo != null) && !tabInfo.isFixed())
			{
				return tab;
			}
		}
		return null;
	}

	/**
	 * Return the tab for the given SolutionRow index or null, if it doesn't
	 * exist.
	 *
	 * @param index the searched index
	 * @return the graph tab for the given SolutionRow index
	 */
	private Tab getGraphTabByIndex(int index)
	{
		for (Tab tab : this.tabPaneGraph.getTabs())
		{
			final GraphTabInfo tabInfo = getGraphTabInfo(tab);
			if ((tabInfo != null) && tabInfo.isFixed() && (index == tabInfo.getSolutionRowIndex()))
			{
				return tab;
			}
		}
		return null;
	}

	private static GraphTabInfo getGraphTabInfo(Tab tab)
	{
		if (tab == null)
		{
			return null;
		}
		final Object userData = tab.getUserData();
		return (userData instanceof GraphTabInfo) ? (GraphTabInfo) userData : null;
	}

	private void handleSolutionRowSelected(SolutionRow solutionRow)
	{
		Tab tab = getGraphTab();
		if (tab == null)
		{
			tab = new Tab();
			tab.setUserData(new GraphTabInfo());
			tab.setContent(this.graphSelectedRow.getDisplay());
			tab.setOnCloseRequest(event -> handleGraphTabCloseRequest());
			tab.setOnClosed(event -> tableViewResult.getSelectionModel().clearSelection());
			this.tabPaneGraph.getTabs().add(0, tab);
		}
		final GraphTabInfo graphTabInfo = getGraphTabInfo(tab);
		if (graphTabInfo != null)
		{
			graphTabInfo.setSolutionRowIndex(solutionRow.getIndex());
		}
		tab.setText("Solution Nr. " + (solutionRow.getIndex() + 1));
		this.graphSelectedRow.setSolution(solutionRow.getSolution());
		setTabPaneGraphEnabled(true);
		this.isUserRowSelection = true;
		try
		{
			this.tabPaneGraph.getSelectionModel().select(tab);
		}
		finally
		{
			this.isUserRowSelection = false;
		}
	}

	private void handleCheckBoxSelectionChanged(boolean selected, int solutionRowIndex)
	{
		if (selected)
		{
			final SolutionGraph solutionGraph = createSolutionGraph();
			final SolutionRow solutionRow = this.tableViewResult.getItems().get(
				findBySolutionRowIndex(solutionRowIndex));
			solutionGraph.setSolution(solutionRow.getSolution());
			final Tab tab = new Tab("Sel. Nr. " + (solutionRowIndex + 1), solutionGraph.getDisplay());
			tab.setUserData(new GraphTabInfo(solutionRowIndex));
			tab.setOnCloseRequest(event -> handleGraphTabCloseRequest());
			tab.setOnClosed(event -> solutionRow.getSelectionProperty().set(false));
			setTabPaneGraphEnabled(true);
			this.tabPaneGraph.getTabs().add(tab);
		}
		else
		{
			final Tab tab = getGraphTabByIndex(solutionRowIndex);
			if (tab != null)
			{
				this.tabPaneGraph.getTabs().remove(tab);
			}
		}
		this.tableViewResult.sort();
	}

	@Override
	public void setSolutions(SortedSet<Solution> solutions)
	{
		shutdownScheduledService(true);
		// clean up intermediate solutions:
		if (this.mapSolutions != null)
		{
			this.mapSolutions.clear();
		}
		this.pane.setBottom(null);
		this.tableViewResult.getItems().clear();
		// add final solutions:
		final List<SolutionRow> listSolutions = new ArrayList<>(solutions.size());
		solutions.forEach(solution -> listSolutions.add(
			new SolutionRow(solution, listSolutions.size(),
				this::handleCheckBoxSelectionChanged)));
		this.tableViewResult.getItems().addAll(listSolutions);
		this.tableViewResult.setPlaceholder(new Label("No (non redundant) solutions"));
		sortForward();
		initTableRowSelectionHandler();
	}

	private void initTableRowSelectionHandler()
	{
		this.tableColumnSelectRow.setVisible(true);
		this.tableViewResult.getSelectionModel().getSelectedItems().addListener(
			(ListChangeListener.Change<? extends SolutionRow> change) ->
		{
			if (change.next())
			{
				final List<? extends SolutionRow> addedSubList = change.getAddedSubList();
				if (addedSubList.size() == 1)
				{
					handleSolutionRowSelected(addedSubList.get(0));
				}
			}
		});
		if (!this.tableViewResult.getItems().isEmpty())
		{
			this.tableViewResult.getSelectionModel().selectFirst();
		}
	}

	private void handleIntermediateResults()
	{
		Platform.runLater(() ->
		{
			final List<SolutionCandidate> list = new ArrayList<>();
			queueIntermediateResults.drainTo(list);
			list.forEach(solutionCandidate ->
			{
				final Solution solution = solutionCandidate.getSolution();
				final Solution.EquivalenceClass equivalenceClass = solutionCandidate.getEquivalenceClass();
				final Integer index = mapSolutions.get(equivalenceClass);
				if (index != null)
				{
					if (solution.compareTo(tableViewResult.getItems().get(index).getSolution()) < 0)
					{
						tableViewResult.getItems().set(index, new SolutionRow(solution, index));
					}
				}
				else
				{
					final int n = mapSolutions.size();
					mapSolutions.put(equivalenceClass, n);
					tableViewResult.getItems().add(new SolutionRow(solution, n));
				}
			});
		});
	}

	@Override
	public void handleIntermediateResult(SolutionCandidate solutionCandidate)
	{
		this.queueIntermediateResults.add(solutionCandidate);
	}

	@Override
	public Region getDisplay()
	{
		return this.pane;
	}

	@Override
	public CalculationCtrlParams getCalculationCtrlParams()
	{
		return this.calculationCtrlParams;
	}

	/**
	 * Returns the table index of the solution shown in the selected graph tab.
	 *
	 * @return the table index of the solution shown in the selected graph tab
	 */
	public Optional<Integer> getSelectedGraphSolutionRowIndex()
	{
		Optional<Integer> result = Optional.empty();
		if (this.tabPaneGraph.isVisible())
		{
			final GraphTabInfo graphTabInfo = getGraphTabInfo(
				this.tabPaneGraph.getSelectionModel().getSelectedItem());
			if (graphTabInfo != null)
			{
				result = Optional.of(graphTabInfo.getSolutionRowIndex());
			}
		}
		return result;
	}

	/**
	 * Returns the solution shown in the selected graph tab. Returns null, if no
	 * solution is selected or the graph tab pane is invisible.
	 *
	 * @return the solution shown in the selected graph tab
	 */
	public Solution getSelectedGraphSolution()
	{
		final Optional<Integer> selectedIndex = getSelectedGraphSolutionRowIndex();
		return selectedIndex.isPresent() ?
			this.tableViewResult.getItems().get(
				findBySolutionRowIndex(selectedIndex.get())).getSolution() :
			null;
	}

	/**
	 * Returns the solution selected in the table view. Returns null, if table is
	 * empty or nothing is selected.
	 *
	 * @return the solution selected in the table view
	 */
	public Solution getSelectedTableRowSolution()
	{
		final var selectedItems = this.tableViewResult.getSelectionModel().getSelectedItems();
		return selectedItems.isEmpty() ? null : selectedItems.get(0).getSolution();
	}

	/**
	 * Returns the number of solutions.
	 *
	 * @return the number of solutions
	 */
	public int getNumSolutions()
	{
		return this.tableViewResult.getItems().size();
	}

	/**
	 * Returns the solutions shown in the table view as stream.
	 *
	 * @return the solutions shown in the table view as stream
	 */
	public Stream<Solution> getSolutionStream()
	{
		return this.tableViewResult.getItems().stream().map(row -> row.getSolution());
	}
}
