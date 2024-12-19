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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Class to encapsulate a Solution with a table row index and selection.
 *
 * @author Bernd Michaely
 */
class SolutionRow implements Comparable<SolutionRow>
{
	private static final ReadOnlyObjectProperty<Operation> NULL_OPERATION_PROPERTY =
		new ReadOnlyObjectWrapper<Operation>(null).getReadOnlyProperty();
	private final Solution solution;
	private final ReadOnlyIntegerWrapper indexProperty;
	private final List<ReadOnlyObjectWrapper<Operation>> operationProperties;
	private final BooleanProperty selectionProperty;

	/**
	 * Interface to handle row selection changes.
	 *
	 * @see #notify(boolean, int)
	 */
	@FunctionalInterface
	interface SelectionListener
	{
		/**
		 * Method to notify a listener about a changed selection.
		 *
		 * @param selected the new selection status
		 * @param index the index of the row for which the selection status changed
		 */
		void notify(boolean selected, int index);
	}

	/**
	 * Encapsulates a given solution with a given row index.
	 *
	 * @param solution the solution to encapsulate
	 * @param index the solution index
	 */
	SolutionRow(Solution solution, int index)
	{
		this(solution, index, null);
	}

	/**
	 * Encapsulates a given solution with a given row index.
	 *
	 * @param solution the solution to encapsulate
	 * @param index the solution index
	 * @param selectionListener listener to be notified about selection changes
	 * (may be null)
	 */
	SolutionRow(Solution solution, int index, SelectionListener selectionListener)
	{
		this.solution = Objects.requireNonNull(solution);
		this.indexProperty = new ReadOnlyIntegerWrapper(index);
		this.operationProperties = new ArrayList<>(solution.getDepth());
		for (int i = 0; i < solution.getDepth(); i++)
		{
			this.operationProperties.add(new ReadOnlyObjectWrapper<>(solution.getOperation(i)));
		}
		this.selectionProperty = new SimpleBooleanProperty();
		if (selectionListener != null)
		{
			this.selectionProperty.addListener((observable, oldValue, newValue) ->
				selectionListener.notify(newValue, index));
		}
	}

	/**
	 * Returns the given solution.
	 *
	 * @return the given solution.
	 */
	Solution getSolution()
	{
		return this.solution;
	}

	ReadOnlyIntegerProperty indexProperty()
	{
		return this.indexProperty.getReadOnlyProperty();
	}

	ReadOnlyObjectProperty<Operation> operationProperty(int index)
	{
		try
		{
			return this.operationProperties.get(index).getReadOnlyProperty();
		}
		catch (IndexOutOfBoundsException ex)
		{
			return NULL_OPERATION_PROPERTY;
		}
	}

	/**
	 * Returns the given row index.
	 *
	 * @return the given row index
	 */
	int getIndex()
	{
		return indexProperty.get();
	}

	/**
	 * Returns the selection status of this row.
	 *
	 * @return the selection status
	 */
	BooleanProperty getSelectionProperty()
	{
		return this.selectionProperty;
	}

	@Override
	public int compareTo(SolutionRow other)
	{
		return (other != null) ? Integer.compare(getIndex(), other.getIndex()) : 1;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof SolutionRow) ?
			compareTo((SolutionRow) obj) == 0 : false;
	}

	@Override
	public int hashCode()
	{
		return getIndex();
	}

	@Override
	public String toString()
	{
		return "Solution [" + getIndex() + "] :" + this.solution;
	}
}
