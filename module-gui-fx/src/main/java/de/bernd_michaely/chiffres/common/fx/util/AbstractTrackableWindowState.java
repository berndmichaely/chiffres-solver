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

/**
 * Abstract base class to persist the position, size and state of a stage.
 *
 * @author Bernd Michaely
 */
public abstract class AbstractTrackableWindowState
{
	/**
	 * The value used to initialize undefined double values.
	 */
	protected static final double DOUBLE_UNDEFINED = Double.NEGATIVE_INFINITY;
	private final Stage stage;
	private final Parent root;
	private final double widthDefault;
	private final double heightDefault;
	private double normalStatePosX;
	private double normalStatePosY;
	private double normalStateWidth;
	private double normalStateHeight;

	/**
	 * Creates a state to persist the position and state of the given non
	 * resizable stage. The constructor will add a newly created {@link Scene}
	 * which is necessary to properly set the stage size.
	 *
	 * @param stage the stage to observe for state changes
	 * @param root the root node to use for the constructed {@link Scene}
	 */
	protected AbstractTrackableWindowState(Stage stage, Parent root)
	{
		this(stage, root, DOUBLE_UNDEFINED, DOUBLE_UNDEFINED);
	}

	/**
	 * Creates a state to persist the position, size and state of the given
	 * resizable stage. The constructor will add a newly created {@link Scene}
	 * which is necessary to properly set the stage size.
	 *
	 * @param stage the stage to observe for state changes
	 * @param root the root node to use for the constructed {@link Scene}
	 * @param widthDefault the initial default width of the scene
	 * @param heightDefault the initial default height of the scene
	 */
	protected AbstractTrackableWindowState(Stage stage, Parent root,
		double widthDefault, double heightDefault)
	{
		if (stage == null)
		{
			throw new IllegalArgumentException("Stage is null");
		}
		this.stage = stage;
		this.root = root;
		this.widthDefault = widthDefault;
		this.heightDefault = heightDefault;
		this.stage.setResizable((widthDefault != DOUBLE_UNDEFINED) &&
			(heightDefault != DOUBLE_UNDEFINED));
	}

	/**
	 * A specialized class must call this method to initialize the stage with the
	 * persisted values.
	 */
	protected final void initializeStage()
	{
		if (getStage().isResizable())
		{
			final double width = loadSceneWidth(this.widthDefault);
			final double height = loadSceneHeight(this.heightDefault);
			getStage().setScene(new Scene(this.root, width, height));
		}
		final double posX = loadStagePosX();
		final double posY = loadStagePosY();
		if ((posX != DOUBLE_UNDEFINED) && (posY != DOUBLE_UNDEFINED))
		{
			getStage().setX(posX);
			getStage().setY(posY);
		}
		else
		{
			getStage().centerOnScreen();
		}
		getStage().setMaximized(loadStageIsMaximized());
		getStage().setIconified(loadStageIsIconified());
		getStage().maximizedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue)
			{
				normalStatePosX = getStage().getX();
				normalStatePosY = getStage().getY();
				normalStateWidth = getStage().getScene().getWidth();
				normalStateHeight = getStage().getScene().getHeight();
			}
		});
	}

	/**
	 * Returns the stage given in the constructor.
	 *
	 * @return the stage given in the constructor
	 */
	public Stage getStage()
	{
		return this.stage;
	}

	/**
	 * Returns the x position in normal (not maximized) state.
	 *
	 * @return the x position in normal (not maximized) state
	 */
	protected double getNormalStatePosX()
	{
		return this.normalStatePosX;
	}

	/**
	 * Returns the y position in normal (not maximized) state.
	 *
	 * @return the y position in normal (not maximized) state
	 */
	protected double getNormalStatePosY()
	{
		return this.normalStatePosY;
	}

	/**
	 * Returns the width in normal (not maximized) state.
	 *
	 * @return the width in normal (not maximized) state
	 */
	protected double getNormalStateWidth()
	{
		return this.normalStateWidth;
	}

	/**
	 * Returns the height in normal (not maximized) state.
	 *
	 * @return the height in normal (not maximized) state
	 */
	protected double getNormalStateHeight()
	{
		return this.normalStateHeight;
	}

	/**
	 * Implementations must return the persisted value of the x position or
	 * {@link #DOUBLE_UNDEFINED DOUBLE_UNDEFINED} as a default value.
	 *
	 * @return the persisted value of the x position
	 */
	protected abstract double loadStagePosX();

	/**
	 * Implementations must return the persisted value of the y position or
	 * {@link #DOUBLE_UNDEFINED DOUBLE_UNDEFINED} as a default value.
	 *
	 * @return the persisted value of the y position
	 */
	protected abstract double loadStagePosY();

	/**
	 * Implementations must return the persisted value of the width or the given
	 * default value.
	 *
	 * @param widthDefault the given default value
	 * @return return the persisted value of the width
	 */
	protected abstract double loadSceneWidth(double widthDefault);

	/**
	 * Implementations must return the persisted value of the height or the given
	 * default value.
	 *
	 * @param heightDefault the given default value
	 * @return return the persisted value of the height
	 */
	protected abstract double loadSceneHeight(double heightDefault);

	/**
	 * Implementations must return the persisted maximization state or a default
	 * value.
	 *
	 * @return the persisted maximization state
	 */
	protected abstract boolean loadStageIsMaximized();

	/**
	 * Implementations must return the persisted iconification state or a default
	 * value.
	 *
	 * @return the persisted iconification state
	 */
	protected abstract boolean loadStageIsIconified();
}
