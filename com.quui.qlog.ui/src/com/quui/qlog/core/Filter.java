package com.quui.qlog.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Filter
{
	public static String find(final TableBuilder tb)
	{
		if ("".equals(tb.getFilter()))
			return tb.getCss() + tb.getContent();

		String line;
		final StringBuilder matches = new StringBuilder("");
		final BufferedReader r = new BufferedReader(new StringReader(tb.getContent()));
		try {
			while ((line = r.readLine()) != null)
				if (tb.isVisible(line))
					matches.append(line);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return tb.getCss() + matches;
	}
}
