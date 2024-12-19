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

import java.util.List;
import org.junit.jupiter.api.Test;
import static de.bernd_michaely.chiffres.graphics.svg.SvgElementBuilder.UNIT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SvgElementBuilder.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class SvgElementBuilderTest
{
	@Test
	public void testNullParameter()
	{
		System.out.println("testNullParameter");
		assertThrows(NullPointerException.class,
			() -> new SvgElementBuilder(null));
	}

	@Test
	public void testEmptyElement()
	{
		System.out.println("testEmptyElement");
		final SvgElementBuilder builder = new SvgElementBuilder("empty");
		final List<String> actual = builder.build();
		final List<String> expected = List.of("<empty />");
		assertEquals(expected, actual);
	}

	@Test
	public void testSimpleContent()
	{
		System.out.println("testSimpleContent");
		final SvgElementBuilder builder = new SvgElementBuilder("text")
			.setContent("Hello, world!");
		final List<String> actual = builder.build();
		final List<String> expected = List.of("<text>Hello, world!</text>");
		assertEquals(expected, actual);
	}

	@Test
	public void testAttributes()
	{
		System.out.println("testAttributes");
		final SvgElementBuilder builder = new SvgElementBuilder("el")
			.addAttribute("int-value", 7)
			.addAttribute("float-value", 3.5f)
			.addAttribute("string-value", "title");
		final List<String> actual = builder.build();
		final List<String> expected = List.of(
			"<el int-value=\"7" + UNIT + "\" float-value=\"3.5" + UNIT +
			"\" string-value=\"title\" />");
		assertEquals(expected, actual);
	}

	@Test
	public void testAttributesIfTrue()
	{
		System.out.println("testAttributesIfTrue");
		final SvgElementBuilder builder = new SvgElementBuilder("el")
			.addAttributeIf(true, "int-value", 7)
			.addAttributeIf(true, "float-value", 3.5f)
			.addAttributeIf(true, "string-value", "title");
		final List<String> actual = builder.build();
		final List<String> expected = List.of(
			"<el int-value=\"7" + UNIT + "\" float-value=\"3.5" + UNIT +
			"\" string-value=\"title\" />");
		assertEquals(expected, actual);
	}

	@Test
	public void testAttributesIfFalse()
	{
		System.out.println("testAttributesIfFalse");
		final SvgElementBuilder builder = new SvgElementBuilder("el")
			.addAttributeIf(false, "int-value", 7)
			.addAttributeIf(false, "float-value", 3.5f)
			.addAttributeIf(false, "string-value", "title");
		final List<String> actual = builder.build();
		final List<String> expected = List.of(
			"<el />");
		assertEquals(expected, actual);
	}

	@Test
	public void testAttributesAndContent()
	{
		System.out.println("testAttributesAndContent");
		final SvgElementBuilder builder = new SvgElementBuilder("el")
			.addAttribute("int-value", 7)
			.addAttribute("float-value", 3.5f)
			.addAttribute("string-value", "title")
			.setContent("Hello, world!");
		final List<String> actual = builder.build();
		final List<String> expected = List.of(
			"<el int-value=\"7" + UNIT + "\" float-value=\"3.5" + UNIT +
			"\" string-value=\"title\">Hello, world!</el>");
		assertEquals(expected, actual);
	}

	@Test
	public void testSubElements()
	{
		System.out.println("testSubElements");
		final SvgElementBuilder builder = new SvgElementBuilder("a")
			.addSubElements(new SvgElementBuilder("b"));
		final List<String> actual = builder.build();
		final List<String> expected = List.of(
			"<a>",
			"\t<b />",
			"</a>"
		);
		assertEquals(expected, actual);
	}

	@Test
	public void testSubElementsAndContent()
	{
		System.out.println("testSubElementsAndContent");
		final SvgElementBuilder builder = new SvgElementBuilder("a")
			.addSubElements(new SvgElementBuilder("b"))
			.setContent("invalid");
		assertThrows(IllegalArgumentException.class, () -> builder.build());
	}
}
