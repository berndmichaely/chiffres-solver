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

import de.bernd_michaely.chiffres.graphics.AbstractSolutionGraphBuilder;
import de.bernd_michaely.chiffres.graphics.AbstractTreeNode;
import de.bernd_michaely.chiffres.graphics.MathSymbolBuilder.Sign;
import de.bernd_michaely.chiffres.graphics.MathSymbolDirector;
import de.bernd_michaely.chiffres.graphics.NodeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete builder for solution graph graphics in SVG format.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class SvgGraphBuilder extends AbstractSolutionGraphBuilder
{
	private final float fontSize;
	private final String colorCodeBackground;
	private int graphWidth, graphHeight;
	private final List<SvgElementBuilder> svgElements = new ArrayList<>();

	/**
	 * Create SVG for solution graph with transparent background based on given
	 * font size. <code>SvgGraphBuilder(fontSize, true)</code> is same as
	 * <code>SvgGraphBuilder(fontSize, null)</code>
	 * <code>SvgGraphBuilder(fontSize, false)</code> is same as
	 * <code>SvgGraphBuilder(fontSize, "#ffffff")</code>
	 *
	 * @param fontSize    the given font size
	 * @param transparent if false, create an opaque white background, if true,
	 *                    create no background at all
	 */
	public SvgGraphBuilder(double fontSize, boolean transparent)
	{
		this(fontSize, transparent ? null : "#ffffff");
	}

	/**
	 * Create SVG for solution graph based on given font size.
	 *
	 * @param fontSize            the given font size
	 * @param colorCodeBackground SVG code for background fill, null indicates a
	 *                            transparent background
	 */
	private SvgGraphBuilder(double fontSize, String colorCodeBackground)
	{
		this.fontSize = (float) fontSize;
		this.colorCodeBackground = colorCodeBackground;
	}

	@Override
	protected void initialize()
	{
		svgElements.clear();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return false (this is not implemented yet)
	 * @see #defineDropShadowFilter(int, int)
	 */
	@Override
	protected boolean isCreatingDropShadows()
	{
		return false;
	}

	private boolean isBackgroundTransparent()
	{
		return colorCodeBackground == null;
	}

	@Override
	protected void setGraphHeight(double height)
	{
		this.graphHeight = (int) height;
	}

	@Override
	protected void setGraphWidth(double width)
	{
		this.graphWidth = (int) width;
	}

	@Override
	protected AbstractTreeNode buildNodeTypeValue(double x, double y, double width, double height,
		double fontSize, String text, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		return new SvgTreeNodeTypeValue(x, y, width, height, fontSize,
			text, nodeType, treeNodeParent);
	}

	@Override
	protected AbstractTreeNode buildNodeTypeOperator(double x, double y, double width, double height,
		double fontSize, Sign symbol, NodeType nodeType, AbstractTreeNode treeNodeParent)
	{
		return new SvgTreeNodeTypeOperator(x, y, width, height, fontSize,
			symbol, nodeType, treeNodeParent);
	}

	/**
	 * TODO (This does not really work yet…)
	 *
	 * @param stdDeviation
	 * @param offset
	 * @return an SVG element builder to define a drop shadow filter
	 */
	private SvgElementBuilder defineDropShadowFilter(int stdDeviation, int offset)
	{
		return new SvgElementBuilder("defs").addSubElements(
			new SvgElementBuilder("filter")
				.addAttribute("id", "dropShadow")
				.addAttribute("x", "0")
				.addAttribute("y", "0")
				.addAttribute("width", "200%")
				.addAttribute("height", "200%")
				.addSubElements(
					new SvgElementBuilder("feOffset")
						.addAttribute("in", "SourceAlpha")
						.addAttribute("result", "offsetOut")
						.addAttribute("dx", "" + offset)
						.addAttribute("dy", "" + offset),
					new SvgElementBuilder("feGaussianBlur")
						.addAttribute("in", "offsetOut")
						.addAttribute("result", "blurOut")
						.addAttribute("stdDeviation", "" + stdDeviation),
					new SvgElementBuilder("feBlend")
						.addAttribute("in", "SourceGraphic")
						.addAttribute("in2", "blurOut")
						.addAttribute("mode", "normal")));
	}

	@Override
	protected void addNode(AbstractTreeNode abstractTreeNode)
	{
		if (abstractTreeNode instanceof SvgTreeNodeTypeValue)
		{
			final SvgTreeNodeTypeValue node = (SvgTreeNodeTypeValue) abstractTreeNode;
			this.svgElements.add(
				new SvgElementBuilder("g").addSubElements(
					new SvgElementBuilder("rect")
						.addAttribute("x", node.getDx())
						.addAttribute("y", node.getDy())
						.addAttribute("rx", node.getWidth() / 5)
						.addAttribute("width", node.getWidth())
						.addAttribute("height", node.getHeight())
						.addAttribute("fill", node.getNodeType().getColorCode())
						.addAttributeIf(isCreatingDropShadows(), "filter", "url(#dropShadow)"),
					new SvgElementBuilder("text")
						.addAttribute("x", node.getX())
						.addAttribute("y", node.getY() + node.getFontSize() * 2 / 5)
						.addAttribute("font-size", (node.getFontSize() * 4 / 5) + "pt")
						.addAttribute("font-family", "sans-serif")
						.addAttribute("text-anchor", "middle")
						.setContent(node.getText())
				));
		}
		else if (abstractTreeNode instanceof SvgTreeNodeTypeOperator)
		{
			final SvgTreeNodeTypeOperator node = (SvgTreeNodeTypeOperator) abstractTreeNode;
			final float radius = node.getHeight() / 2;
			final SvgElementBuilder circle = new SvgElementBuilder("circle")
				.addAttribute("cx", node.getX())
				.addAttribute("cy", node.getY())
				.addAttribute("r", radius)
				.addAttribute("fill", node.getNodeType().getColorCode())
				.addAttributeIf(isCreatingDropShadows(), "filter", "url(#dropShadow)");
			final SvgElementBuilder symbol;
			if (isCreatingMathSymbolGraphics())
			{
				final float r_2 = radius / 2;
				final SvgMathSymbolBuilder builder = new SvgMathSymbolBuilder(
					node.getFontSize(), node.getSymbol(),
					node.getX() - r_2, node.getY() - r_2);
				new MathSymbolDirector(builder).construct();
				symbol = builder.getResult();
			}
			else
			{
				symbol = new SvgElementBuilder("text")
					.addAttribute("x", node.getX())
					.addAttribute("y", node.getY() + node.getFontSize() * 2 / 5)
					.addAttribute("font-size", (node.getFontSize() * 4 / 5) + "pt")
					.addAttribute("text-anchor", "middle")
					.setContent("" + node.getSymbol().getUnicodeSymbol());
			}
			final SvgElementBuilder group = new SvgElementBuilder("g");
			group.addSubElements(circle, symbol);
			this.svgElements.add(group);
		}
	}

	@Override
	protected void addEdge(AbstractTreeNode abstractTreeNode)
	{
		if (abstractTreeNode instanceof SvgTreeNode)
		{
			final SvgTreeNode node = (SvgTreeNode) abstractTreeNode;
			final AbstractTreeNode tng = node.getTreeNodeParent();
			if (tng instanceof SvgTreeNode)
			{
				final SvgTreeNode nodeParent = (SvgTreeNode) tng;
				this.svgElements.add(new SvgElementBuilder("line")
					.addAttribute("x1", node.getX())
					.addAttribute("y1", node.getY())
					.addAttribute("x2", nodeParent.getX())
					.addAttribute("y2", nodeParent.getY())
					.addAttribute("stroke", "black")
					.addAttribute("stroke-width", node.getFontSize() / 7));
			}
		}
	}

	private SvgElementBuilder createBackground()
	{
		return new SvgElementBuilder("rect")
			.addAttribute("x", 0)
			.addAttribute("y", 0)
			.addAttribute("width", graphWidth)
			.addAttribute("height", graphHeight)
			.addAttribute("fill", colorCodeBackground);
	}

	public List<String> getResult()
	{
		final List<String> result = new ArrayList<>();
		result.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		result.add("<!-- Created with JChiffresFX -->");
		result.addAll(new SvgElementBuilder("svg")
			.addAttribute("xmlns", "http://www.w3.org/2000/svg")
			.addAttributeIf(isCreatingDropShadows(), "xmlns:xlink", "http://www.w3.org/1999/xlink")
			.addAttribute("version", "1.1")
			.addAttribute("width", "100%")
			.addAttribute("height", "100%")
			.addAttribute("viewBox", "0 0 " + graphWidth + " " + graphHeight)
			.addSubElements(new SvgElementBuilder("title").setContent("JChiffresFX solution graph"))
			.addSubElementIf(isCreatingDropShadows(), defineDropShadowFilter(10, (int) fontSize))
			.addSubElementIf(!isBackgroundTransparent(), createBackground())
			.addSubElements(new SvgElementBuilder("g").addSubElements(svgElements))
			.build());
		return result;
	}
}
