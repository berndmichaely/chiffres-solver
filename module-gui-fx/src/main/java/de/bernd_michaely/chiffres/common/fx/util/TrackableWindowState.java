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
package de.bernd_michaely.chiffres.common.fx.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static de.bernd_michaely.chiffres.fx.mainwindow.PreferencesKeys.*;

/**
 * Utility class for persisting a window position, size and state.
 *
 * @author Bernd Michaely
 */
public class TrackableWindowState extends AbstractTrackableWindowState
{
	/**
	 * Creates an instance to persist the position, size and state of the given
	 * stage in the given preferences. The constructor will add a newly created
	 * {@link Scene} which is necessary to properly set the stage size. The state
	 * will be stored on a {@link WindowEvent#WINDOW_CLOSE_REQUEST}.
	 *
	 * @param stage         the stage to observe for state changes
	 * @param root          the root node to use for the constructed {@link Scene}
	 * @param widthDefault  the initial default width of the scene
	 * @param heightDefault the initial default height of the scene
	 */
	public TrackableWindowState(Stage stage, Parent root, double widthDefault, double heightDefault)
	{
		super(stage, root, widthDefault, heightDefault);
		initializeStage();
		stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event ->
		{
			preferences.putBoolean(ID_PREF_WINDOW_ICONIFIED.key(), stage.isIconified());
			final boolean maximized = stage.isMaximized();
			preferences.putBoolean(ID_PREF_WINDOW_MAXIMIZED.key(), maximized);
			if (maximized)
			{
				preferences.putDouble(ID_PREF_WINDOW_POS_X.key(), getNormalStatePosX());
				preferences.putDouble(ID_PREF_WINDOW_POS_Y.key(), getNormalStatePosY());
				preferences.putDouble(ID_PREF_WINDOW_WITH.key(), getNormalStateWidth());
				preferences.putDouble(ID_PREF_WINDOW_HEIGHT.key(), getNormalStateHeight());
			}
			else
			{
				preferences.putDouble(ID_PREF_WINDOW_POS_X.key(), stage.getX());
				preferences.putDouble(ID_PREF_WINDOW_POS_Y.key(), stage.getY());
				preferences.putDouble(ID_PREF_WINDOW_WITH.key(), stage.getScene().getWidth());
				preferences.putDouble(ID_PREF_WINDOW_HEIGHT.key(), stage.getScene().getHeight());
			}
		});
	}

	@Override
	protected double loadStagePosX()
	{
		return preferences.getDouble(ID_PREF_WINDOW_POS_X.key(), DOUBLE_UNDEFINED);
	}

	@Override
	protected double loadStagePosY()
	{
		return preferences.getDouble(ID_PREF_WINDOW_POS_Y.key(), DOUBLE_UNDEFINED);
	}

	@Override
	protected double loadSceneWidth(double widthDefault)
	{
		return preferences.getDouble(ID_PREF_WINDOW_WITH.key(), widthDefault);
	}

	@Override
	protected double loadSceneHeight(double heightDefault)
	{
		return preferences.getDouble(ID_PREF_WINDOW_HEIGHT.key(), heightDefault);
	}

	@Override
	protected boolean loadStageIsMaximized()
	{
		return preferences.getBoolean(ID_PREF_WINDOW_MAXIMIZED.key(), false);
	}

	@Override
	protected boolean loadStageIsIconified()
	{
		return preferences.getBoolean(ID_PREF_WINDOW_ICONIFIED.key(), false);
	}
}
