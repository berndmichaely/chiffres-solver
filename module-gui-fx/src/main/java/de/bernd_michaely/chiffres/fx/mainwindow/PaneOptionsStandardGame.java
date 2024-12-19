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

import de.bernd_michaely.chiffres.fx.info.InfoPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static de.bernd_michaely.chiffres.fx.mainwindow.PreferencesKeys.*;

/**
 * Dialog content pane for editing standard game options.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class PaneOptionsStandardGame implements InfoPane
{
	private final String title;
	private final BorderPane root;
	private final VBox vBox;
	private final Slider sliderNumThreads;
	private final CheckBox checkBoxShowIntermediateResults;

	PaneOptionsStandardGame(String title)
	{
		this.title = title;
		final Label label = new Label(title);
		label.setFont(Font.font(null, FontWeight.BOLD, Font.getDefault().getSize() * 1.2));
		final double hgap = 5 * DEFAULT_INSET_SIZE;
		label.setPadding(new Insets(0, hgap, DEFAULT_INSET_SIZE, hgap));
		this.sliderNumThreads = new Slider();
		sliderNumThreads.setBlockIncrement(1);
		sliderNumThreads.setMajorTickUnit(1);
		sliderNumThreads.setMinorTickCount(0);
		sliderNumThreads.setShowTickLabels(true);
		sliderNumThreads.setShowTickMarks(true);
		sliderNumThreads.setSnapToTicks(true);
		final Label labelSlider = new Label("_Number of threads to use for computation:");
		labelSlider.setMnemonicParsing(true);
		labelSlider.setLabelFor(sliderNumThreads);
		this.checkBoxShowIntermediateResults = new CheckBox("Show _intermediate results");
		this.vBox = new VBox(DEFAULT_INSET_SIZE, labelSlider, sliderNumThreads,
			checkBoxShowIntermediateResults);
		vBox.setAlignment(Pos.CENTER_LEFT);
		this.root = new BorderPane(vBox);
		root.setTop(label);
		root.setPadding(new Insets(DEFAULT_INSET_SIZE));
		BorderPane.setAlignment(label, Pos.CENTER);
		loadValues();
	}

	public static int getNumThreadsMax()
	{
		return Runtime.getRuntime().availableProcessors();
	}

	public static int getNumThreads()
	{
		final int numThreadsMax = getNumThreadsMax();
		final int numThreadsDefault = Math.round(numThreadsMax * 3f / 8f);
		return Math.min(numThreadsMax, preferences.getInt(
			ID_PREF_MODE_DEFAULT_NUM_THREADS.key(), numThreadsDefault));
	}

	public static boolean isShowingIntermediateResult()
	{
		return preferences.getBoolean(ID_PREF_MODE_DEFAULT_INTERMEDIATE.key(), false);
	}

	private void loadValues()
	{
		final int numThreads = getNumThreads();
		sliderNumThreads.setMin(1);
		sliderNumThreads.setMax(getNumThreadsMax());
		sliderNumThreads.setValue(numThreads);
		checkBoxShowIntermediateResults.setSelected(isShowingIntermediateResult());
	}

	void saveValues()
	{
		preferences.putInt(ID_PREF_MODE_DEFAULT_NUM_THREADS.key(),
			(int) sliderNumThreads.getValue());
		preferences.putBoolean(ID_PREF_MODE_DEFAULT_INTERMEDIATE.key(),
			checkBoxShowIntermediateResults.isSelected());
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public Region getDisplay()
	{
		return root;
	}
}
