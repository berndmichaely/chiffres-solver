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
package de.bernd_michaely.chiffres.graphics.svg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class to build SVG element strings.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class SvgElementBuilder
{
	static final String UNIT = "px";
	private final String elementName;
	private final List<String> attributes = new ArrayList<>();
	private String content;
	private final List<SvgElementBuilder> subElements = new ArrayList<>();
	private final List<String> result = new ArrayList<>();
	private int indentLevel;

	/**
	 * Constructs an SVG element of given name.
	 *
	 * @param elementName the element name
	 * @throws NullPointerException if elementName is null
	 */
	SvgElementBuilder(String elementName)
	{
		this.elementName = Objects.requireNonNull(elementName,
			"SVG element name must not be null");
	}

	SvgElementBuilder addAttribute(String name, int value)
	{
		return addAttributeIf(true, name, value);
	}

	SvgElementBuilder addAttribute(String name, float value)
	{
		return addAttributeIf(true, name, value);
	}

	SvgElementBuilder addAttribute(String name, String value)
	{
		return addAttributeIf(true, name, value);
	}

	SvgElementBuilder addAttributeIf(boolean condition, String name, int value)
	{
		return addAttributeIf(condition, name, "" + value + UNIT);
	}

	SvgElementBuilder addAttributeIf(boolean condition, String name, float value)
	{
		return addAttributeIf(condition, name, "" + value + UNIT);
	}

	SvgElementBuilder addAttributeIf(boolean condition, String name, String value)
	{
		if (condition)
		{
			attributes.add(name + "=\"" + value + "\"");
		}
		return this;
	}

	SvgElementBuilder setContent(String content)
	{
		this.content = content;
		return this;
	}

	SvgElementBuilder addSubElements(SvgElementBuilder... subElements)
	{
		this.subElements.addAll(Arrays.asList(subElements));
		return this;
	}

	SvgElementBuilder addSubElements(List<SvgElementBuilder> subElements)
	{
		this.subElements.addAll(subElements);
		return this;
	}

	SvgElementBuilder addSubElementIf(boolean condition, SvgElementBuilder subElement)
	{
		if (condition)
		{
			this.subElements.add(subElement);
		}
		return this;
	}

	private void addResultLine(String line)
	{
		result.add(Stream.concat(
			IntStream.range(0, indentLevel).mapToObj(i -> "\t"), Stream.of(line))
			.collect(Collectors.joining()));
	}

	/**
	 * Creates a string list representing the SVG element.
	 *
	 * @return string list representing the SVG element
	 * @throws IllegalArgumentException if the SVG element contains both content
	 *                                  and sub-elements
	 */
	List<String> build()
	{
		final boolean hasContent = content != null;
		final boolean hasSubElements = !subElements.isEmpty();
		final boolean isEmpty = !(hasContent || hasSubElements);
		if (hasContent && hasSubElements)
		{
			throw new IllegalArgumentException("SVG element »" + elementName +
				"« has both content and sub-elements");
		}
		final StringBuilder s = new StringBuilder();
		s.append('<').append(elementName);
		attributes.stream().map(a -> ' ' + a).forEach(s::append);
		if (isEmpty)
		{
			s.append(" />");
			addResultLine(s.toString());
		}
		else
		{
			if (hasContent)
			{
				s.append('>').append(content).append("</").append(elementName).append('>');
				addResultLine(s.toString());
			}
			else
			{
				s.append('>');
				addResultLine(s.toString());
				indentLevel++;
				subElements.stream().flatMap(e -> e.build().stream()).forEach(this::addResultLine);
				indentLevel--;
				addResultLine("</" + elementName + '>');
			}
		}
		return result;
	}
}
