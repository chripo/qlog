package com.quui.qlog.core;

import java.util.regex.Pattern;

public class TableBuilder
{
	private Pattern _filterPttrn;
	private StringBuilder _content = new StringBuilder("");
	private float _fontSizeEM;

	public TableBuilder(final int fontSizePX)
	{
		_fontSizeEM = toEm(fontSizePX);
		setFilter("");
	}

	static private float toEm(int px)
	{
		return (float)((int)( (((float)px) / 16.0f) * 10000)) / 10000.0f;
	}

	public void changeFontSize(int fontSize)
	{
		_fontSizeEM = toEm(fontSize);
	}

	static String wrap(final String message, final String color)
	{
		return "<p style='background-color:" + color + "'>" + message + "</p>";
	}

	public String buildHTML(final String color, final String message)
	{
		final String msg = wrap(message, validateColor(color)) + "\n";
		_content.append(msg);

		return isVisible(msg) ? msg : null;
	}

	static String validateColor(String color)
	{
		if (color == null || "".equals(color))
			return "#ffffff";
		color = color.replace("0x", "#");
		if (color.length() < 7)
			color += "0000000".substring(0, 7 - color.length());

		return color;
	}

	public boolean isVisible(final String str)
	{
		try {
			if ("".equals(_filterPttrn.pattern()))
				return true;
			return _filterPttrn.matcher(str).find();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return true;
	}

	public void clear()
	{
		_content.setLength(0);
	}

	public String getContent()
	{
		return _content.toString();
	}

	public void setFilter(final String filter)
	{
		_filterPttrn = Pattern.compile(filter);
	}

	public String getFilter()
	{
		try {
			return _filterPttrn.pattern();
		} catch (Exception ex) {}
		return "";
	}

	public String getCss()
	{
		return getCss(_fontSizeEM);
	}

	static public String getCss(final float fontSizeEM)
	{
		return "<style type='text/css'>"
				+ "p { margin: 0; padding: 3px 0px; width:100%;\n"
				+ "    font-family: Arial, 'Open Sans', sans-serif, monospace;\n"
				+ "    font-size:" + fontSizeEM + "em;\n"
				+ "}"
				+"</style>\n";
	}
}
