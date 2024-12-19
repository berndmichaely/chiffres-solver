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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Class to provide an icon for top level windows.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class StageIcon
{
	private static final Logger logger = Logger.getLogger(StageIcon.class.getName());
	private static final String PATH_APP_ICON = "JChiffresFX.png";
	private static Image stageIcon;

	static
	{
		try (InputStream inputStream = StageIcon.class.getResourceAsStream(PATH_APP_ICON))
		{
			stageIcon = new Image(inputStream);
		}
		catch (IOException | NullPointerException | IllegalArgumentException ex)
		{
			stageIcon = null;
			logger.log(Level.INFO, "Icon not found : {0}", PATH_APP_ICON);
		}
	}

	/**
	 * Add the application icon to the given stage
	 *
	 * @param stage the stage to supply with an icon
	 */
	static void addStageIcon(Stage stage)
	{
		if (stageIcon != null)
		{
			stage.getIcons().add(stageIcon);
		}
	}
}
