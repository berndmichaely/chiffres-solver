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

/**
 * Main GUI module.
 * 
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
module de.bernd_michaely.chiffres.fx
{
	requires de.bernd_michaely.chiffres.calc;
	requires de.bernd_michaely.chiffres.fx.canvas;
	requires de.bernd_michaely.chiffres.graphics;
	requires java.desktop;
	requires java.logging;
	requires java.prefs;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.swing;
	exports de.bernd_michaely.chiffres.fx;
}
