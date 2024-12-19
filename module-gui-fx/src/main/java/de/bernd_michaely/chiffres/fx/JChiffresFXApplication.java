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
package de.bernd_michaely.chiffres.fx;

import de.bernd_michaely.chiffres.fx.mainwindow.ApplicationMainWindow;
import de.bernd_michaely.chiffres.fx.mainwindow.JChiffresFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

/**
 * Main class for JChiffresFX application.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class JChiffresFXApplication extends Application
{
	public static final String TITLE_APPLICATION = "JChiffresFX";
	private ApplicationMainWindow mainWindow;
	private static boolean testMode;

	static void runTest()
	{
		testMode = true;
		launch();
	}

	@Override
	public void init() throws Exception
	{
		super.init();
		mainWindow = new JChiffresFX();
		mainWindow.init();
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		mainWindow.start(stage);
		if (testMode)
		{
			System.out.format("Stage title = »%s«%n", stage.getTitle());
			Platform.runLater(Platform::exit);
		}
		else
		{
			stage.show();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
