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
import de.bernd_michaely.chiffres.fx.canvas.CanvasSolutionGraphBuilder;
import de.bernd_michaely.chiffres.fx.table.SolutionDisplayTable;
import de.bernd_michaely.chiffres.graphics.SolutionGraphDirector;
import de.bernd_michaely.chiffres.graphics.svg.SvgGraphBuilder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import static de.bernd_michaely.chiffres.fx.mainwindow.GraphicsFileFormats.getExtensionFilters;
import static de.bernd_michaely.chiffres.fx.mainwindow.PreferencesKeys.*;
import static de.bernd_michaely.chiffres.fx.util.MathSymbol.DEFAULT_FONT_SIZE;
import static java.util.stream.Collectors.joining;

/**
 * Class to handle saving of solution info to misc file formats.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class SolutionFileOutput
{
	private final Window windowDialogParent;
	private final SolutionDisplayTable sdt;

	SolutionFileOutput(Window windowDialogParent, SolutionDisplayTable sdt)
	{
		this.windowDialogParent = windowDialogParent;
		this.sdt = sdt;
	}

	public static void main(String[] args)
	{
		final StringBuilder result = new StringBuilder(String.format("ENUM IMAGE WRITERS :%n"));
		GraphicsFileFormats.forEach(f  ->
		{
			result.append(String.format("FORMAT »%s« :%n", f));
			final Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(f.name());
			int counter = 0;
			while (iter.hasNext())
			{
				final ImageWriter writer = iter.next();
				result.append(String.format("%d.) %s%n", ++counter, writer.getClass().getCanonicalName()));
			}
		});
		System.out.println(result);
	}

	private List<String> getSvgContent(boolean transparent)
	{
		if (sdt != null)
		{
			final Solution solution = sdt.getSelectedGraphSolution();
			if (solution != null)
			{
				final int numOperands = sdt.getCalculationCtrlParams().getNumOperands();
				final SvgGraphBuilder builder = new SvgGraphBuilder(DEFAULT_FONT_SIZE, transparent);
				final SolutionGraphDirector director = new SolutionGraphDirector(
					builder, numOperands, DEFAULT_FONT_SIZE);
				director.construct(solution);
				return builder.getResult();
			}
		}
		return null;
	}

	private List<String> getCsvContent()
	{
		final List<String> result = new ArrayList<>();
		if (sdt != null)
		{
			sdt.getSolutionStream().forEach(solution  ->
			{
				final StringBuilder s = new StringBuilder();
				final int n = sdt.getCalculationCtrlParams().getNumOperands() - 1;
				for (int i = 0; i < n; i++)
				{
					if (i > 0)
					{
						s.append(',');
					}
					if (i < solution.getDepth())
					{
						s.append(solution.getOperation(i));
					}
				}
				result.add(s.toString());
			});
		}
		return result;
	}

	private void savePlainTextFile(String initialFileName,
		String preferencesKey, String fileChooserTitle, List<String> content,
		String defaultExtension, ExtensionFilter... extensionFilters)
	{
		final Path outputPath = getOutputPath(initialFileName, preferencesKey,
			fileChooserTitle, defaultExtension, extensionFilters);
		if (outputPath != null)
		{
			try
			{
				Files.write(outputPath, content);
			}
			catch (AccessDeniedException | SecurityException ex)
			{
				new Alert(AlertType.ERROR,
					"Access denied to write file »" + outputPath + "«",
					ButtonType.OK).showAndWait();
			}
			catch (IOException ex)
			{
				new Alert(AlertType.ERROR,
					"Error trying to write file »" + outputPath + "«",
					ButtonType.OK).showAndWait();
			}
		}
	}

	private void saveImageFile(String initialFileName, String preferencesKey,
		String fileChooserTitle, boolean transparentFill, int fontSize,
		GraphicsFileFormats format, ExtensionFilter... extensionFilters)
	{
		final Path outputPath = getOutputPath(initialFileName, preferencesKey,
			fileChooserTitle, format.getPostfix(), extensionFilters);
		if (outputPath != null)
		{
			final boolean transparency = transparentFill && format.isSupportingTransparency();
			final int numOperands = sdt.getCalculationCtrlParams().getNumOperands();
			final var builder = new CanvasSolutionGraphBuilder(fontSize);
			final var director = new SolutionGraphDirector(builder, numOperands, fontSize);
			director.construct(sdt.getSelectedGraphSolution());
			final BufferedImage buffer = transparency ? null : new BufferedImage(
				builder.getCanvasWidth(), builder.getCanvasHeight(), BufferedImage.TYPE_INT_RGB);
			final BufferedImage fromFXImage = SwingFXUtils.fromFXImage(
				builder.createImage(transparency), buffer);
			try
			{
				final String formatName = format.name();
				if (!ImageIO.write(fromFXImage, formatName, outputPath.toFile()))
				{
					new Alert(AlertType.ERROR,
						"No appropriate writer plug-in found for format »" + formatName + "«",
						ButtonType.OK).showAndWait();
				}
			}
			catch (IOException ex)
			{
				new Alert(AlertType.ERROR,
					"Error trying to write file »" + outputPath + "«",
					ButtonType.OK).showAndWait();
			}
		}
	}

	private Path getOutputPath(String initialFileName,
		String preferencesKey, String fileChooserTitle,
		String defaultFilePostfix, ExtensionFilter... extensionFilters)
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(fileChooserTitle);
		fileChooser.getExtensionFilters().addAll(extensionFilters);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
		final Optional<ExtensionFilter> defaultFilter = Arrays.stream(extensionFilters)
			.filter(f  -> f.getDescription().toLowerCase(Locale.ROOT).startsWith(defaultFilePostfix))
			.findFirst();
		if (defaultFilter.isPresent())
		{
			fileChooser.setSelectedExtensionFilter(defaultFilter.get());
		}
		final File fileSavePath = getFileSavePath(preferencesKey);
		if (fileSavePath.isDirectory())
		{
			fileChooser.setInitialDirectory(fileSavePath);
		}
		if (initialFileName != null)
		{
			fileChooser.setInitialFileName(initialFileName);
		}
		final File fileSave = fileChooser.showSaveDialog(this.windowDialogParent);
		if (fileSave != null)
		{
			preferences.put(preferencesKey, fileSave.getParentFile().getPath());
			final boolean hasExtension = fileSave.toString().contains(".");
			final String pathName = ((defaultFilePostfix != null) && !hasExtension) ?
				fileSave + "." + defaultFilePostfix : fileSave.toString();
			return Paths.get(pathName);
		}
		else
		{
			return null;
		}
	}

	private File getFileSavePath(String preferencesKey)
	{
		final String pathDefault = System.getProperty("user.home");
		File pathSave = new File(preferences.get(preferencesKey, pathDefault));
		while ((pathSave != null) && !pathSave.isDirectory())
		{
			pathSave = pathSave.getParentFile();
		}
		if ((pathSave == null) || !pathSave.isDirectory())
		{
			pathSave = new File(pathDefault);
		}
		return pathSave;
	}

	private String getFileNamePrefix()
	{
		if (sdt != null)
		{
			final var params = sdt.getCalculationCtrlParams();
			return params.getTarget() + "_" +
				Arrays.stream(params.getOperands())
					.mapToObj(n  -> String.format("%03d", n))
					.collect(joining("-"));
		}
		else
		{
			return "";
		}
	}

	private String getInitialFileBaseName()
	{
		final Optional<Integer> selection = (sdt != null) ?
			sdt.getSelectedGraphSolutionRowIndex() : Optional.empty();
		if (!selection.isEmpty())
		{
			final int width = ("" + sdt.getNumSolutions()).length();
			final int index = selection.get() + 1;
			return getFileNamePrefix() +
				String.format("_solution-%0" + width + "d", index);
		}
		else
		{
			return null;
		}
	}

	void saveGraph(String fileChooserTitle, GraphicsFileFormats imageFileFormat,
		boolean transparentFill, int fontSize)
	{
		final String initialFileBaseName = getInitialFileBaseName();
		if (initialFileBaseName != null)
		{
			final String initialFileName = initialFileBaseName + "." + imageFileFormat.getPostfix();
			final String preferencesKey = ID_PREF_PATH_SAVE_IMG.key(imageFileFormat.name());
			switch (imageFileFormat)
			{
				case SVG:
					savePlainTextFile(initialFileName, preferencesKey, fileChooserTitle,
						getSvgContent(transparentFill), imageFileFormat.getPostfix(), getExtensionFilters());
					break;
				default:
					saveImageFile(initialFileName, preferencesKey, fileChooserTitle,
						transparentFill, fontSize, imageFileFormat, getExtensionFilters());
			}
		}
	}

	void saveSolutionsAsCSV(String fileChooserTitle)
	{
		if (sdt != null)
		{
			final String format = "csv";
			final String initialFileName = getFileNamePrefix() + "." + format;
			savePlainTextFile(initialFileName, ID_PREF_PATH_SAVE_CSV.key(), fileChooserTitle,
				getCsvContent(), format, new ExtensionFilter("CSV files", "*." + format));
		}
	}
}
