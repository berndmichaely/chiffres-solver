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
package de.bernd_michaely.chiffres.fx.display;

import de.bernd_michaely.chiffres.fx.util.MathSymbol;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import static de.bernd_michaely.chiffres.fx.display.ProgressDisplay.ProgressDisplayMode.*;
import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * A display showing a progress indicator and a cancel button.
 *
 * @author Bernd Michaely
 */
public class ProgressDisplay implements DisplayComponent
{
	public static final String TITLE_CANCELLED = "Cancelled";
	private static final String TITLE_BUTTON_START = "_Start";
	private static final String TITLE_BUTTON_CANCEL = "_Cancel";
	private static final String MSG_INVALID_PROGRESS_DISPLAY_MODE =
		"Invalid ProgressDisplayMode";
	private static final int SIZE_INDICATOR = 80;
	private static final double GAP_SIZE = MathSymbol.DEFAULT_FONT_SIZE / 2;
	private final ProgressDisplayMode progressDisplayMode;
	private final ProgressIndicator progressIndicator;
	private final Button button;
	private final BorderPane borderPane;
	private Runnable onStart;
	private ReadOnlyBooleanWrapper runningProperty;
	private Runnable onCancel;

	public enum ProgressDisplayMode
	{
		EDITOR, PLACEHOLDER, STATUSBAR;

		public static ProgressDisplayMode getDefaultProgressDisplayMode()
		{
			return PLACEHOLDER;
		}
	}

	public ProgressDisplay(ProgressDisplayMode progressDisplayMode)
	{
		this.progressDisplayMode = (progressDisplayMode != null) ?
			progressDisplayMode : ProgressDisplayMode.getDefaultProgressDisplayMode();
		this.runningProperty = new ReadOnlyBooleanWrapper();
		this.borderPane = new BorderPane();
		switch (getProgressDisplayMode())
		{
			case EDITOR:
				this.runningProperty.set(false);
				this.button = new Button(TITLE_BUTTON_START);
				this.borderPane.setCenter(this.button);
				this.button.setOnAction(e -> start());
				this.progressIndicator = new ProgressIndicator();
				initProgressIndicator();
				this.borderPane.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
				BorderPane.setMargin(this.button, new Insets(SIZE_INDICATOR / 5));
				break;
			case PLACEHOLDER:
				this.runningProperty.set(true);
				this.button = new Button(TITLE_BUTTON_CANCEL);
				this.button.setOnAction(e -> cancel());
				this.progressIndicator = new ProgressIndicator();
				initProgressIndicator();
				this.borderPane.setCenter(this.button);
				this.borderPane.setLeft(this.progressIndicator);
				BorderPane.setMargin(this.progressIndicator, new Insets(SIZE_INDICATOR / 5));
				this.borderPane.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
				break;
			case STATUSBAR:
				this.runningProperty.set(true);
				this.button = new Button(TITLE_BUTTON_CANCEL);
				this.button.setOnAction(e -> cancel());
				this.progressIndicator = new ProgressBar();
				this.borderPane.setCenter(this.progressIndicator);
				this.borderPane.setRight(this.button);
				this.progressIndicator.setMaxWidth(Double.MAX_VALUE);
				BorderPane.setMargin(this.button, new Insets(GAP_SIZE));
				BorderPane.setMargin(this.progressIndicator, new Insets(GAP_SIZE));
				break;
			default:
				throw new AssertionError(MSG_INVALID_PROGRESS_DISPLAY_MODE);
		}
		this.button.setDefaultButton(true);
	}

	private void initProgressIndicator()
	{
		this.progressIndicator.setPrefSize(SIZE_INDICATOR, SIZE_INDICATOR);
		this.progressIndicator.setMaxSize(SIZE_INDICATOR, SIZE_INDICATOR);
	}

	@Override
	public Region getDisplay()
	{
		return this.borderPane;
	}

	public ProgressDisplayMode getProgressDisplayMode()
	{
		return this.progressDisplayMode;
	}

	public DoubleProperty progressProperty()
	{
		return this.progressIndicator.progressProperty();
	}

	public Runnable getOnStart()
	{
		return this.onStart;
	}

	public void setOnStart(Runnable onStart)
	{
		this.onStart = onStart;
	}

	public Runnable getOnCancel()
	{
		return this.onCancel;
	}

	public void setOnCancel(Runnable runnable)
	{
		this.onCancel = runnable;
	}

	public boolean isStartDisabled()
	{
		return !isRunning() && this.button.isDisabled();
	}

	public void setStartDisabled(boolean startDisabled)
	{
		if (!isRunning())
		{
			this.button.setDisable(startDisabled);
		}
	}

	public ReadOnlyBooleanProperty runningProperty()
	{
		return this.runningProperty.getReadOnlyProperty();
	}

	public boolean isRunning()
	{
		return runningProperty().get();
	}

	public boolean isCancelled()
	{
		return this.button.isDisabled();
	}

	public void start()
	{
		if (!isRunning())
		{
			this.runningProperty.set(true);
			if (getProgressDisplayMode().equals(EDITOR))
			{
				this.button.setText(TITLE_BUTTON_CANCEL);
				this.borderPane.setLeft(this.progressIndicator);
				this.button.setOnAction(e -> cancel());
			}
			if (this.onStart != null)
			{
				this.onStart.run();
			}
		}
	}

	public void cancel()
	{
		if (isRunning() && !isCancelled())
		{
			switch (getProgressDisplayMode())
			{
				case EDITOR:
					this.borderPane.setLeft(null);
					this.button.setText(TITLE_BUTTON_START);
					this.button.setOnAction(e -> start());
					this.runningProperty.set(false);
					break;
				case PLACEHOLDER:
					this.button.setDisable(true);
					break;
				case STATUSBAR:
					this.button.setDisable(true);
					this.borderPane.setCenter(new Label(TITLE_CANCELLED));
					break;
				default:
					throw new AssertionError(MSG_INVALID_PROGRESS_DISPLAY_MODE);
			}
			if (this.onCancel != null)
			{
				this.onCancel.run();
			}
		}
	}
}
