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
package de.bernd_michaely.chiffres.fx.info;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * InfoPane to display system info.
 *
 * @author Bernd Michaely
 */
public class InfoPaneSystemInfo implements InfoPane
{
	private final ReadOnlyBooleanWrapper showDefaultFontSizeProperty;

	public InfoPaneSystemInfo()
	{
		this.showDefaultFontSizeProperty = new ReadOnlyBooleanWrapper(false);
	}

	@Override
	public String getTitle()
	{
		return "System Info";
	}

	private ReadOnlyBooleanProperty showDefaultFontSizeProperty()
	{
		return this.showDefaultFontSizeProperty.getReadOnlyProperty();
	}

	@Override
	public Region getDisplay()
	{
		final Label headerJreVersion = new Label("JRE Version:");
		final Label textJreVersion = new Label(JreVersionUtil.getJreVersionInfo());
		final Label headerJavaFXVersion = new Label("JavaFX Version:");
		final Label textJavaFXVersion = new Label(System.getProperty("javafx.version"));
		final Label headerOSName = new Label("Operating system name:");
		final Label textOSName = new Label(System.getProperty("os.name"));
		final Label headerOSArch = new Label("Operating system architecture:");
		final Label textOSArch = new Label(System.getProperty("os.arch"));
		final Label headerAvailableProcessors = new Label("Number of available processors:");
		final Label textAvailableProcessors = new Label("" + Runtime.getRuntime().availableProcessors());
		final Label headerMaxHeapSize = new Label("Maximum heap memory size:");
		final Label textMaxHeapSize = new Label(
			String.format("%.1f MB", (double) Runtime.getRuntime().maxMemory() / (1 << 20)));
		final Label headerDefaultFontSize = new Label("Default font size:");
		final Label textDefaultFontSize = new Label(DEFAULT_FONT_SIZE + " points");
		final Font fontHeader = Font.font(null, FontWeight.BOLD, DEFAULT_FONT_SIZE);
		headerJreVersion.setFont(fontHeader);
		headerJavaFXVersion.setFont(fontHeader);
		headerOSName.setFont(fontHeader);
		headerOSArch.setFont(fontHeader);
		headerAvailableProcessors.setFont(fontHeader);
		headerMaxHeapSize.setFont(fontHeader);
		headerDefaultFontSize.setFont(fontHeader);
		final VBox vBox = new VBox(
			headerJreVersion, textJreVersion,
			headerJavaFXVersion, textJavaFXVersion,
			headerOSName, textOSName,
			headerOSArch, textOSArch,
			headerAvailableProcessors, textAvailableProcessors,
			headerMaxHeapSize, textMaxHeapSize,
			headerDefaultFontSize, textDefaultFontSize);
		vBox.setPadding(new Insets(DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE, 0, DEFAULT_INSET_SIZE));
		vBox.setSpacing(DEFAULT_FONT_SIZE / 3);
		headerDefaultFontSize.visibleProperty().bind(showDefaultFontSizeProperty());
		textDefaultFontSize.visibleProperty().bind(showDefaultFontSizeProperty());
		vBox.setOnMouseClicked(mouseEvent ->
		{
			if (mouseEvent.getClickCount() == 2)
			{
				if (!mouseEvent.isShiftDown() && mouseEvent.isControlDown() && mouseEvent.isAltDown())
				{
					this.showDefaultFontSizeProperty.set(!this.showDefaultFontSizeProperty.get());
				}
			}
		});
		return vBox;
	}
}
