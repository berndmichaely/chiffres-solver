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

import de.bernd_michaely.chiffres.fx.mainwindow.TabReference;
import java.util.function.BiConsumer;
import javafx.scene.Node;

/**
 * Base class for all calculation display implementations.
 *
 * @author Bernd Michaely
 */
public abstract class AbstractCalculationDisplay implements CalculationDisplay
{
	private BiConsumer<TabReference, CalculationCtrlParams> onStart;
	private TabReference tabReference;

	@Override
	public void setOnStart(TabReference tabReference,
		BiConsumer<TabReference, CalculationCtrlParams> onStart)
	{
		this.tabReference = tabReference;
		this.onStart = onStart;
	}

	protected BiConsumer<TabReference, CalculationCtrlParams> getOnStart()
	{
		return this.onStart;
	}

	protected TabReference getTabReference()
	{
		return this.tabReference;
	}

	/**
	 * Returns the node which should initially be focused when this display is
	 * shown.
	 *
	 * @return the initially focused node
	 */
	protected Node getInitiallyFocusedNode()
	{
		return null;
	}

	@Override
	public void requestInitalFocus()
	{
		if (getInitiallyFocusedNode() != null)
		{
			getInitiallyFocusedNode().requestFocus();
		}
	}
}
