package com.quui.qlog.core;

import java.util.regex.Pattern;

public class TableBuilder
{
	private String _filter = "";
	private String _content = "";
	private float _fontSize;
	private String _css;
	final private float _initialFontSize;

	public TableBuilder(final int fontSize)
	{
		_initialFontSize = _fontSize = toEm(fontSize);
		_css = "<style type='text/css'>p { margin: 0; padding: 3px 0px; width:100%; font-family: Arial, 'Open Sans', sans-serif, monospace; font-size:" + _fontSize + "em; }</style>";
		_content =  _css;
	}

	static private float toEm(int px) {
		return (float)((int)( (((float)px) / 16.0f) * 10000)) / 10000.0f;
	}

	public void setFilter(String filter)
	{
		_filter = filter;
	}

	public void changeFontSize(int fontSize)
	{
		final float s = toEm(fontSize);
		_content = _content.replaceAll(
				"font-size:" + _fontSize + "em;",
				"font-size:" + s + "em;");
		_fontSize = s;
	}

	static String wrap(final String message, final String color)
	{
		return "<p style='background-color:" + color + "'>" + message + "</p>";
	}

	public String buildHTML(final String color, final String message)
	{
		String newMsg = wrap(message, validateColor(color))
				+ System.getProperty("line.separator");
		_content += newMsg;

		if (!filter(newMsg))
			return null;
		return newMsg;
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

	private boolean filter(String str)
	{
		if (_filter.equals(""))
			return true;
		Pattern pattern = Pattern.compile(_filter);
		if (pattern.matcher(str).find())
			return true;
		else
			return false;
	}

	public void clear()
	{
		_content =  _css;
		int newFontSize = (int)(_fontSize * 16);
		_fontSize = _initialFontSize;
		changeFontSize(newFontSize);
	}

	public String getContent()
	{
		return _content;
	}

	public String getFilter()
	{
		return _filter;
	}

	public String getCss()
	{
		return _css.replaceAll(
				"font-size:" + _initialFontSize + "em;",
				"font-size:" + _fontSize + "em;");
	}
}
