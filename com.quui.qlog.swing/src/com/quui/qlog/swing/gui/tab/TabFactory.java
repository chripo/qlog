package com.quui.qlog.swing.gui.tab;

import com.quui.qlog.core.PropertiesReader;
import com.quui.qlog.swing.gui.Window;
import com.quui.utils.util.IDestroyable;


public class TabFactory {
	private static Window _window;
	private static PropertiesReader _reader;

	public static void setWindow(Window window) {
		_window = window;
	}

	public static void setPropertiesReader(PropertiesReader reader) {
		_reader = reader;
	}

	public static ITab createTab(IDestroyable client, String name) {
		ITab t = _window.getTabForName(name);

		if (t == null) {
			return new Tab(_window, _reader, client, name);
		}
		return t;
	}

	public static ITreeTab createTreeTab(String name) {
		ITreeTab tab = (ITreeTab) _window.getTabForName(name);

		if (tab == null) {
			return new TreeTab(_window, _reader, name);
		}

		return tab;
	}
}
