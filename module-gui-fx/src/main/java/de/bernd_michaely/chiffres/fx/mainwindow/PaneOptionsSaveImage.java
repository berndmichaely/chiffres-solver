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

import de.bernd_michaely.chiffres.calc.Solution;
import de.bernd_michaely.chiffres.fx.info.InfoPane;
import java.util.EnumMap;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static de.bernd_michaely.chiffres.fx.info.InfoPane.DEFAULT_INSET_SIZE;
import static de.bernd_michaely.chiffres.fx.mainwindow.GraphicsFileFormats.getDefaultGraphicsFileFormat;
import static de.bernd_michaely.chiffres.fx.mainwindow.PreferencesKeys.*;

/**
 * Dialog content pane for editing solution graph image save options.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class PaneOptionsSaveImage implements InfoPane
{
	private final String title;
	private final BorderPane root;
	private final VBox paneGroups;
	private final HBox paneButtons;
	private final ToggleGroup toggleGroup;
	private final EnumMap<GraphicsFileFormats, RadioButton> mapButtons;
	private final CheckBox checkBoxTransparency;
	private final Spinner<Integer> spinner;
	private final Button buttonDefaultFontSize;
	private final Label labelImageSize;

	PaneOptionsSaveImage(String title, int numOperands, Solution solution)
	{
		this.title = title;
		final Label labelTitle = new Label(title);
		labelTitle.setFont(Font.font(null, FontWeight.BOLD, Font.getDefault().getSize() * 1.2));
		final double hgap = 5 * DEFAULT_INSET_SIZE;
		labelTitle.setPadding(new Insets(0, hgap, 1.5 * DEFAULT_INSET_SIZE, hgap));
		this.paneButtons = new HBox(DEFAULT_INSET_SIZE, new Label("Image file format:"));
		this.toggleGroup = new ToggleGroup();
		this.mapButtons = new EnumMap<>(GraphicsFileFormats.class);
		GraphicsFileFormats.forEach(f ->
		{
			final RadioButton radioButton = new RadioButton("_" + f);
			radioButton.setDisable(!(f.isVectorFormat() || f.isImageWriterAvailable()));
			radioButton.setUserData(f);
			mapButtons.put(f, radioButton);
			radioButton.setToggleGroup(toggleGroup);
			paneButtons.getChildren().add(radioButton);
		});
		this.checkBoxTransparency = new CheckBox("_Transparent background");
		checkBoxTransparency.setSelected(true);
		this.spinner = new Spinner<>(5, 99, getDefaultFontSize());
		spinner.setEditable(true);
		spinner.setPrefWidth(7 * DEFAULT_FONT_SIZE);
		final Label labelSpinner = new Label("_Font size");
		labelSpinner.setMnemonicParsing(true);
		labelSpinner.setLabelFor(spinner);
		buttonDefaultFontSize = new Button("_Default");
		buttonDefaultFontSize.setTooltip(new Tooltip("Set default font size"));
		buttonDefaultFontSize.setOnAction(event ->
			spinner.getValueFactory().setValue(getDefaultFontSize()));
		final HBox paneControls = new HBox(DEFAULT_INSET_SIZE,
			spinner, labelSpinner, buttonDefaultFontSize);
		paneButtons.setAlignment(Pos.CENTER_LEFT);
		paneControls.setAlignment(Pos.CENTER_LEFT);
		this.labelImageSize = new Label();
		labelImageSize.textProperty().bind(new ImageSizeBinding(
			spinner.valueProperty(), numOperands, solution));
		this.paneGroups = new VBox(DEFAULT_INSET_SIZE,
			paneButtons, checkBoxTransparency, paneControls, labelImageSize);
		paneGroups.setAlignment(Pos.CENTER_LEFT);
		this.root = new BorderPane(paneGroups);
		root.setTop(labelTitle);
		root.setPadding(new Insets(DEFAULT_INSET_SIZE));
		BorderPane.setAlignment(labelTitle, Pos.CENTER);
		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
		{
			final GraphicsFileFormats f = getSelectedFileFormat(newValue);
			checkBoxTransparency.setDisable((f == null) || !f.isSupportingTransparency());
			final boolean isSizeDisabled = (f == null) || f.isVectorFormat();
			spinner.setDisable(isSizeDisabled);
			labelSpinner.setDisable(isSizeDisabled);
			buttonDefaultFontSize.setDisable(isSizeDisabled);
			labelImageSize.setDisable(isSizeDisabled);
		});
		loadValues();
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

	private static int getDefaultFontSize()
	{
		return (int) Math.round(DEFAULT_FONT_SIZE);
	}

	int getFontSize()
	{
		return spinner.getValue();
	}

	GraphicsFileFormats getSelectedFileFormat()
	{
		return getSelectedFileFormat(toggleGroup.getSelectedToggle());
	}

	private GraphicsFileFormats getSelectedFileFormat(Toggle toggle)
	{
		if ((toggle != null) && (toggle.getUserData() instanceof GraphicsFileFormats))
		{
			return (GraphicsFileFormats) toggle.getUserData();
		}
		else
		{
			return getDefaultGraphicsFileFormat();
		}
	}

	boolean isTransparentFill()
	{
		return checkBoxTransparency.isSelected();
	}

	void loadValues()
	{
		final String formatName = preferences.get(
			ID_PREF_IMAGE_FILE_FORMAT.key(), getDefaultGraphicsFileFormat().name());
		mapButtons.get(Objects.requireNonNullElse(
			GraphicsFileFormats.getByName(formatName), getDefaultGraphicsFileFormat()))
			.setSelected(true);
		checkBoxTransparency.setSelected(
			preferences.getBoolean(ID_PREF_IMAGE_TRANSPARENT.key(), true));
		spinner.getValueFactory().setValue(
			preferences.getInt(ID_PREF_IMAGE_FONT_SIZE.key(), getDefaultFontSize()));
	}

	void saveValues()
	{
		preferences.put(ID_PREF_IMAGE_FILE_FORMAT.key(), getSelectedFileFormat().name());
		preferences.putBoolean(ID_PREF_IMAGE_TRANSPARENT.key(), checkBoxTransparency.isSelected());
		preferences.putInt(ID_PREF_IMAGE_FONT_SIZE.key(), getFontSize());
	}
}
