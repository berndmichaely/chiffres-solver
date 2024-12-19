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
package de.bernd_michaely.chiffres.common.fx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;

import static java.util.Objects.requireNonNull;

/**
 * Binding to observe an ObservableList for emptiness. The binding will have a
 * value of true, iff the observed list is empty.
 *
 * @author Bernd Michaely
 */
public class ObservableListEmptyBinding extends BooleanBinding
{
	private final ObservableList<?> list;

	/**
	 * Creates an instance to observe the given list.
	 *
	 * @param list the list to observe
	 * @throws NullPointerException if list is null
	 */
	public ObservableListEmptyBinding(ObservableList<?> list)
	{
		this.list = requireNonNull(list, "ObservableList is null");
		bind(list);
	}

	@Override
	protected boolean computeValue()
	{
		return this.list.isEmpty();
	}
}
