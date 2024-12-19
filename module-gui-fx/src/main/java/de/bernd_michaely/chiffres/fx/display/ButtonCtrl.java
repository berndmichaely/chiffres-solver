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

import de.bernd_michaely.chiffres.fx.display.CalculationState.State;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;

import static de.bernd_michaely.chiffres.fx.display.CalculationState.STATES_OPERAND;
import static de.bernd_michaely.chiffres.fx.display.CalculationState.STATES_TARGET;
import static de.bernd_michaely.chiffres.fx.display.CalculationState.State.*;

/**
 * Package local class to create the buttons of a {@link NumPadDisplay}.
 *
 * @author Bernd Michaely
 */
public class ButtonCtrl implements Iterable<Button>
{
	/**
	 * Button id prefix specific for numerical buttons.
	 */
	public static final String NUM_BUTTON_ID_PREFIX = "NUM_";
	private static final Pattern PATTERN_BUTTON_NUM_KEY_NAME =
		Pattern.compile("BUTTON_(\\d{3})");
	private static final DropShadow dropShadow = new DropShadow();
	private static final Font fontButtons = Font.font(20);
	private final EnumMap<ButtonKey, Button> mapButtons;

	enum ButtonKey
	{
		BUTTON_000, BUTTON_001, BUTTON_002, BUTTON_003, BUTTON_004,
		BUTTON_005, BUTTON_006, BUTTON_007, BUTTON_008, BUTTON_009,
		BUTTON_010, BUTTON_025, BUTTON_050, BUTTON_075, BUTTON_100,
		BUTTON_START, BUTTON_CLEAR, BUTTON_CLEAR_ALL
	}

	/**
	 * Package local constructor.
	 */
	ButtonCtrl()
	{
		this.mapButtons = new EnumMap<>(ButtonKey.class);
		this.mapButtons.put(ButtonKey.BUTTON_000, new Button("0"));
		this.mapButtons.put(ButtonKey.BUTTON_001, new Button("1"));
		this.mapButtons.put(ButtonKey.BUTTON_002, new Button("2"));
		this.mapButtons.put(ButtonKey.BUTTON_003, new Button("3"));
		this.mapButtons.put(ButtonKey.BUTTON_004, new Button("4"));
		this.mapButtons.put(ButtonKey.BUTTON_005, new Button("5"));
		this.mapButtons.put(ButtonKey.BUTTON_006, new Button("6"));
		this.mapButtons.put(ButtonKey.BUTTON_007, new Button("7"));
		this.mapButtons.put(ButtonKey.BUTTON_008, new Button("8"));
		this.mapButtons.put(ButtonKey.BUTTON_009, new Button("9"));
		this.mapButtons.put(ButtonKey.BUTTON_010, new Button("10"));
		this.mapButtons.put(ButtonKey.BUTTON_025, new Button("25"));
		this.mapButtons.put(ButtonKey.BUTTON_050, new Button("50"));
		this.mapButtons.put(ButtonKey.BUTTON_075, new Button("75"));
		this.mapButtons.put(ButtonKey.BUTTON_100, new Button("100"));
		this.mapButtons.put(ButtonKey.BUTTON_START, new Button("Start!"));
		this.mapButtons.put(ButtonKey.BUTTON_CLEAR, new Button("C"));
		this.mapButtons.put(ButtonKey.BUTTON_CLEAR_ALL, new Button("AC"));
		this.mapButtons.forEach((ButtonKey key, Button button) ->
			{
				final String strKey = key.toString();
				final Matcher matcher = PATTERN_BUTTON_NUM_KEY_NAME.matcher(strKey);
				if (matcher.matches())
				{
					button.setUserData(NUM_BUTTON_ID_PREFIX + matcher.group(1));
				}
				else
				{
					button.setUserData(strKey);
				}
				button.setFont(fontButtons);
				button.setMaxWidth(Integer.MAX_VALUE);
				button.setMaxHeight(Integer.MAX_VALUE);
				button.setOnMouseEntered((event) -> button.setEffect(dropShadow));
				button.setOnMouseExited((event) -> button.setEffect(null));
			});
	}

	Button getButton(ButtonKey buttonKey)
	{
		return this.mapButtons.get(buttonKey);
	}

	void updateButtonStates(State state)
	{
		final boolean isOperand = STATES_OPERAND.contains(state);
		final boolean isTarget = STATES_TARGET.contains(state);
		final boolean isOperandOrTarget = isOperand || isTarget;
		this.mapButtons.get(ButtonKey.BUTTON_CLEAR).setDisable(state.equals(INITIAL));
		this.mapButtons.get(ButtonKey.BUTTON_CLEAR_ALL).setDisable(state.equals(INITIAL));
		this.mapButtons.get(ButtonKey.BUTTON_START).setDisable(!state.equals(READY));
		this.mapButtons.get(ButtonKey.BUTTON_000).setDisable(!state.equals(TARGET_NOT_10X));
		getButton(ButtonKey.BUTTON_001).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_002).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_003).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_004).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_005).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_006).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_007).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_008).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_009).setDisable(!isOperandOrTarget);
		getButton(ButtonKey.BUTTON_010).setDisable(!isOperand);
		getButton(ButtonKey.BUTTON_025).setDisable(!isOperand);
		getButton(ButtonKey.BUTTON_050).setDisable(!isOperand);
		getButton(ButtonKey.BUTTON_075).setDisable(!isOperand);
		getButton(ButtonKey.BUTTON_100).setDisable(!isOperand);
	}

	@Override
	public Iterator<Button> iterator()
	{
		return this.mapButtons.values().iterator();
	}
}
