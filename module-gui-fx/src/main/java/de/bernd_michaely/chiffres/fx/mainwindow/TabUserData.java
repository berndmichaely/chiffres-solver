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

import de.bernd_michaely.chiffres.fx.display.CalculationCtrl;
import de.bernd_michaely.chiffres.fx.display.CalculationCtrlParams;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplay;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplayExtended;
import de.bernd_michaely.chiffres.fx.display.CalculationDisplayStandard;
import de.bernd_michaely.chiffres.fx.display.SolutionDisplay;
import de.bernd_michaely.chiffres.fx.table.SolutionDisplayTable;
import javafx.scene.control.TabPane;

/**
 * Class to encapsulate information associated with main window tabs.
 *
 * @author Bernd Michaely
 */
class TabUserData
{
	private static final int TAB_INDEX_EMPTY = -1;
	private static final int TAB_INDEX_APPROX = -2;
	/**
	 * Instance to indicate an empty TabPane.
	 */
	static final TabUserData EMPTY = new TabUserData(TAB_INDEX_EMPTY, null);
	private final int tabIndex;
	private final boolean extendedMode;
	private CalculationCtrl calculationCtrl;
	private CalculationCtrlParams calculationCtrlParams;
	private TabPane tabPaneApprox;
	private boolean showingSolutions;

	TabUserData(SolutionDisplayTable solutionDisplayApprox, boolean showingSolutions)
	{
		this(TAB_INDEX_APPROX, solutionDisplayApprox);
		this.showingSolutions = showingSolutions;
	}

	TabUserData(int tabIndex, CalculationCtrl calculationCtrl)
	{
		this.tabIndex = tabIndex;
		this.calculationCtrl = calculationCtrl;
		this.extendedMode = isCalculationDisplayExtended();
	}

	int getTabIndex()
	{
		return this.tabIndex;
	}

	CalculationCtrlParams getCalculationCtrlParams()
	{
		return this.calculationCtrlParams;
	}

	void setCalculationCtrlParams(CalculationCtrlParams calculationCtrlParams)
	{
		this.calculationCtrlParams = calculationCtrlParams;
	}

	void setSolutionDisplay(SolutionDisplay solutionDisplay)
	{
		this.calculationCtrl = solutionDisplay;
		this.showingSolutions = true;
	}

	boolean isExtendedMode()
	{
		return this.extendedMode;
	}

	boolean isCalculationCtrl()
	{
		return this.calculationCtrl instanceof CalculationCtrl;
	}

	public CalculationCtrl getCalculationCtrl()
	{
		return this.calculationCtrl;
	}

	boolean isCalculationDisplay()
	{
		return this.calculationCtrl instanceof CalculationDisplay;
	}

	CalculationDisplay getCalculationDisplay()
	{
		return (this.calculationCtrl instanceof CalculationDisplay) ?
			((CalculationDisplay) this.calculationCtrl) : null;
	}

	boolean isCalculationDisplayStandard()
	{
		return this.calculationCtrl instanceof CalculationDisplayStandard;
	}

	CalculationDisplayStandard getCalculationDisplayStandard()
	{
		return (this.calculationCtrl instanceof CalculationDisplayStandard) ?
			((CalculationDisplayStandard) this.calculationCtrl) : null;
	}

	boolean isCalculationDisplayExtended()
	{
		return this.calculationCtrl instanceof CalculationDisplayExtended;
	}

	CalculationDisplayExtended getCalculationDisplayExtended()
	{
		return (this.calculationCtrl instanceof CalculationDisplayExtended) ?
			((CalculationDisplayExtended) this.calculationCtrl) : null;
	}

	boolean isSolutionDisplay()
	{
		return this.calculationCtrl instanceof SolutionDisplay;
	}

	SolutionDisplay getSolutionDisplay()
	{
		return (this.calculationCtrl instanceof SolutionDisplay) ?
			((SolutionDisplay) this.calculationCtrl) : null;
	}

	boolean isSolutionDisplayTable()
	{
		return this.calculationCtrl instanceof SolutionDisplayTable;
	}

	SolutionDisplayTable getSolutionDisplayTable()
	{
		return (this.calculationCtrl instanceof SolutionDisplayTable) ?
			((SolutionDisplayTable) this.calculationCtrl) : null;
	}

	TabPane getTabPaneApprox()
	{
		return this.tabPaneApprox;
	}

	void setTabPaneApprox(TabPane tabPaneApprox)
	{
		this.tabPaneApprox = tabPaneApprox;
	}

	boolean isShowingSolutions()
	{
		return this.showingSolutions;
	}
}
