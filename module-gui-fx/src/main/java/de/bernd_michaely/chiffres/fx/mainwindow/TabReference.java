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

import java.lang.ref.WeakReference;
import javafx.scene.control.Tab;

/**
 * An immutable class to encapsulate a weak reference to a tab to pass through.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class TabReference
{
	private final WeakReference<Tab> tabRef;

	TabReference(Tab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("TabReference : Tab is null");
		}
		this.tabRef = new WeakReference<>(tab);
	}

	/**
	 * Returns the encapsulated tab reference.
	 *
	 * @return the encapsulated tab reference
	 */
	Tab getTab()
	{
		return this.tabRef.get();
	}
}
