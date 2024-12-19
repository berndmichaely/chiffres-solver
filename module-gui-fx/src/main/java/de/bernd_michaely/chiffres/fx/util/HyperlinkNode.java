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
package de.bernd_michaely.chiffres.fx.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/**
 * A class to create a hyperlink node. If launching an external browser is
 * supported on this platform, a hyperlink node will be created, otherwise a
 * simple label with the URL as text.
 *
 * @author Bernd Michaely
 */
public class HyperlinkNode
{
	private static final Logger logger = Logger.getLogger(HyperlinkNode.class.getName());

	public static boolean isBrowseSupported()
	{
		return Desktop.isDesktopSupported() ?
			Desktop.getDesktop().isSupported(Action.BROWSE) : false;
	}

	/**
	 * Creates a HyperlinkNode for the given URL, using the url as title.
	 *
	 * @param url the given URL, also used as title
	 * @return a HyperlinkNode for the given URL
	 */
	public static Node createNode(String url)
	{
		return createNode(url, createShortTitle(url));
	}

	public static String createShortTitle(String url)
	{
		if (url == null)
		{
			return "";
		}
		final String p = "://";
		final int index = url.indexOf(p);
		return (index >= 0) ? url.substring(index + p.length()) : url;
	}

	/**
	 * Creates a HyperlinkNode for the given URL and title.
	 *
	 * @param url   the given URL
	 * @param title the given title
	 * @return a HyperlinkNode for the given URL and title
	 */
	public static Node createNode(String url, String title)
	{
		if (url == null)
		{
			throw new IllegalArgumentException("URL is null");
		}
		final String strTitle = (title != null) ? title : url;
		final Node node;
		if (isBrowseSupported())
		{
			final Hyperlink hyperlink = new Hyperlink(strTitle);
			hyperlink.setTooltip(new Tooltip(url));
			hyperlink.setOnAction(event ->
			{
				try
				{
					Desktop.getDesktop().browse(URI.create(url));
				}
				catch (IOException ex)
				{
					logger.log(Level.WARNING,
						"Can't launch external browser for URL : " + url, ex);
				}
			});
			node = hyperlink;
		}
		else
		{
			final Label label = new Label(strTitle);
			label.setTooltip(new Tooltip(url));
			node = label;
		}
		return node;
	}
}
