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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;

/**
 * Enum to describe graphics file formats.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
enum GraphicsFileFormats
{
	PNG(true), JPG(false), BMP(false), TIFF(true), SVG(true);

	private final boolean supportingTransparency;

	private GraphicsFileFormats(boolean supportingTransparency)
	{
		this.supportingTransparency = supportingTransparency;
	}

	boolean isSupportingTransparency()
	{
		return supportingTransparency;
	}

	boolean isVectorFormat()
	{
		return this == SVG;
	}

	/**
	 * Returns a file name extension without dot. E.g.: »<code>png</code>«
	 *
	 * @return a file name extension without dot
	 */
	String getPostfix()
	{
		return name().toLowerCase(Locale.ROOT);
	}

	/**
	 * Returns true, if {@link ImageIO} has an image writer plugin available.
	 *
	 * @return true, if an image writer is available
	 */
	boolean isImageWriterAvailable()
	{
		return !isVectorFormat() && List.of(ImageIO.getWriterFormatNames()).contains(name());
	}

	static void forEach(Consumer<GraphicsFileFormats> action)
	{
		List.of(values()).forEach(action);
	}

	ExtensionFilter getExtensionFilter()
	{
		return new ExtensionFilter(name() + " files", "*." + getPostfix());
	}

	static ExtensionFilter[] getExtensionFilters()
	{
		return Arrays.stream(values())
			.map(GraphicsFileFormats::getExtensionFilter)
			.toArray(ExtensionFilter[]::new);
	}

	static GraphicsFileFormats getDefaultGraphicsFileFormat()
	{
		return values()[0];
	}

	/**
	 * Finds an enum constant by name.
	 *
	 * @param formatName the constant name to search
	 * @return the matching constant or null
	 */
	static GraphicsFileFormats getByName(String formatName)
	{
		return Arrays.stream(values())
			.filter((f) -> f.name().equals(formatName))
			.findFirst().orElse(null);
	}
}
