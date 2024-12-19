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

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Interface to implement by an OpenJavaFX Application main window.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public interface ApplicationMainWindow
{
	/**
	 * This method is called by {@link Application#init()}.
	 *
	 * @throws Exception will be passed to {@link Application#init()}
	 */
	void init() throws Exception;

	/**
	 * This method is called by {@link  Application#start(Stage)}. NOTE that the
	 * implementation of this method must NOT call {@link Stage#show()} itself.
	 *
	 * @param stage the {@link Stage} passed by {@link Application#start(Stage)}
	 * @throws Exception will be passed to {@link Application#start(Stage)}
	 */
	void start(Stage stage) throws Exception;
}
