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
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * Node to display all calculation operands.
 *
 * @author Bernd Michaely
 */
public class OperandDisplay implements DisplayComponent
{
	static final double OPERAND_DISPLAY_FONT_SIZE = 2 * MathSymbol.DEFAULT_FONT_SIZE;
	private static final DropShadow dropShadow = new DropShadow();
	private static final CornerRadii CORNER_RADII = new CornerRadii(10);
	private static final BorderWidths BORDER_WIDTHS = new BorderWidths(7);
	private static final Border BORDER_TARGET = new Border(new BorderStroke(
		Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTHS));
	private static final Border BORDER_OPERAND = new Border(new BorderStroke(
		Color.TRANSPARENT, BorderStrokeStyle.NONE, CORNER_RADII, BORDER_WIDTHS));
	private static final Border BORDER_INPUT = new Border(new BorderStroke(
		Color.ORANGE, BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTHS));
	private static final Border BORDER_SUCCESS = new Border(new BorderStroke(
		Color.LIMEGREEN, BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTHS));
	private static final Border BORDER_ERROR = new Border(new BorderStroke(
		Color.RED, BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTHS));
	private final Paint PAINT_GRADIENT_LIGHT = new LinearGradient(0, 0, 1, 0, true, NO_CYCLE,
		new Stop(0, Color.LIGHTGRAY), new Stop(1, Color.WHITE));
	private final Paint PAINT_GRADIENT_DARK = new LinearGradient(0, 0, 1, 0, true, NO_CYCLE,
		new Stop(0, Color.GRAY), new Stop(1, Color.LIGHTGRAY));
	private final DisplayType displayType;
	private DisplayState displayState;
	private final StackPane stackPane;
	private final Text text;
	private final Rectangle rectangle;
	private boolean mouseActionEnabled;

	public enum DisplayType
	{
		OPERAND, TARGET
	}

	public enum DisplayState
	{
		DEFAULT, INPUT, SUCCESS, ERROR
	}

	/**
	 * Creates a display to edit an operand or calculation target.
	 *
	 * @param displayType the type of number to display
	 * @param numDigits the number of digits to display
	 */
	public OperandDisplay(DisplayType displayType, int numDigits)
	{
		if (displayType == null)
		{
			throw new IllegalArgumentException("display type is null");
		}
		this.displayType = displayType;
		this.text = new Text();
		this.rectangle = new Rectangle();
		this.rectangle.setArcWidth(15);
		this.rectangle.setArcHeight(15);
		this.stackPane = new StackPane(this.rectangle, this.text);
		this.stackPane.setBorder(getDefaultBorder());
		this.stackPane.setOnMouseEntered((event) ->
			{
				if (isMouseActionActive())
				{
					stackPane.setEffect(dropShadow);
				}
			});
		this.stackPane.setOnMouseExited((event) ->
			{
				stackPane.setEffect(null);
			});
		initFontSize(numDigits, OPERAND_DISPLAY_FONT_SIZE);
		switch (this.displayType)
		{
			case OPERAND:
				this.rectangle.setFill(PAINT_GRADIENT_LIGHT);
				this.stackPane.setOnMousePressed((event) ->
					{
						if (isMouseActionActive())
						{
							rectangle.setFill(PAINT_GRADIENT_DARK);

						}
					});
				this.stackPane.setOnMouseReleased((event) ->
					{
						if (isMouseActionActive())
						{
							rectangle.setFill(PAINT_GRADIENT_LIGHT);
						}
					});
				break;
			case TARGET:
				this.rectangle.setFill(Color.BLACK);
				this.text.setStroke(Color.WHITE);
				this.text.setFill(Color.WHITE);
				break;
			default:
				throw new AssertionError("Invalid display type");
		}
	}

	/**
	 * Sets a mouse click event handler which passes the display id.
	 *
	 * @param eventHandler a mouse click event handler which passes the display id
	 */
	public void setEventHandler(Consumer<String> eventHandler)
	{
		if (eventHandler != null)
		{
			this.stackPane.setOnMouseClicked((event) ->
				{
					if (isMouseActionActive())
					{
						final Object userData = getDisplay().getUserData();
						eventHandler.accept(
							(userData instanceof String) ? userData.toString() : "");
					}
				});
		}
	}

	private boolean isMouseActionActive()
	{
		return (this.stackPane.getOnMouseClicked() != null) &&
			this.mouseActionEnabled && !this.text.getText().isEmpty();
	}

	public boolean isMouseActionEnabled()
	{
		return this.mouseActionEnabled;
	}

	public void setMouseActionEnabled(boolean mouseActionEnabled)
	{
		this.mouseActionEnabled = mouseActionEnabled;
	}

	public DisplayType getDisplayType()
	{
		return this.displayType;
	}

	public DisplayState getDisplayState()
	{
		return this.displayState;
	}

	public void setDisplayState(DisplayState displayState)
	{
		if (this.displayState != displayState)
		{
			this.displayState = displayState;
			final Border border;
			switch (displayState)
			{
				case INPUT:
					border = BORDER_INPUT;
					break;
				case SUCCESS:
					border = BORDER_SUCCESS;
					break;
				case ERROR:
					border = BORDER_ERROR;
					break;
				default:
					border = getDefaultBorder();
			}
			this.stackPane.setBorder(border);
		}
	}

	private void initFontSize(int numDigits, double size)
	{
		final Font font = new Font(size);
		this.text.setFont(font);
		final StringBuilder stringBuffer = new StringBuilder();
		for (int i = 0; i < numDigits; i++)
		{
			stringBuffer.append('8');
		}
		final Text textMeasure = new Text(stringBuffer.toString());
		textMeasure.setFont(font);
		final Bounds bounds = textMeasure.getLayoutBounds();
		final double widthInner = bounds.getWidth();
		final double heightInner = bounds.getHeight();
		final int margin = 20;
		final double widthOuter = widthInner + margin;
		final double heightOuter = heightInner + margin;
		this.stackPane.setMinSize(widthOuter, heightOuter);
		this.stackPane.setPrefSize(widthOuter, heightOuter);
		this.stackPane.setMaxSize(widthOuter, heightOuter);
		this.rectangle.setWidth(widthInner + 10);
		this.rectangle.setHeight(heightInner + 10);
	}

	private Border getDefaultBorder()
	{
		return this.displayType.equals(DisplayType.OPERAND) ?
			BORDER_OPERAND : BORDER_TARGET;
	}

	public StringProperty textProperty()
	{
		return this.text.textProperty();
	}

	public String getText()
	{
		return textProperty().get();
	}

	public void setText(String value)
	{
		textProperty().set(value);
	}

	public void setHighlighted(boolean highlighted)
	{
		setDisplayState(highlighted ? DisplayState.INPUT : DisplayState.DEFAULT);
	}

	@Override
	public Region getDisplay()
	{
		return this.stackPane;
	}

	public static Region createSpace()
	{
		final Pane pane = new Pane();
		pane.setMinSize(3, 1);
		pane.setPrefSize(OPERAND_DISPLAY_FONT_SIZE, 1);
		pane.setMaxSize(OPERAND_DISPLAY_FONT_SIZE, Integer.MAX_VALUE);
		return pane;
	}
}
