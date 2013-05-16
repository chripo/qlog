package com.quui.qlog.swing.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class ButtonFactory
{
	private static final List<JMenuItem> _items = new ArrayList<JMenuItem>();

	enum MenuButton {
		ABOUT,
		CLEAR,
		CLEAR_ON_CONNECT,
		FILTER,
		ALWAYS_ON_TOP,
		SAVE_LOG,
		CHANGE_FONTSIZE,
		REMOVE_ALL_TABS,
		SESSION_EXPORT,
		SESSION_IMPORT
	}

	public static JMenuItem create(final MenuButton id, final ActionListener listener)
	{
		JMenuItem item = null;
		switch (id)
		{
		case ABOUT:
			item = buildItem(id, "About", KeyEvent.VK_U, listener);
			break;
		case CLEAR:
			item = buildItem(id, "Clear", KeyEvent.VK_E, listener);
			break;
		case CLEAR_ON_CONNECT:
			item = buildItem(id, "Clear On Connect", KeyEvent.VK_O, listener);
			break;
		case FILTER:
			item = buildItem(id, "Filter", KeyEvent.VK_F, listener);
			break;
		case ALWAYS_ON_TOP:
			item = buildItem(id, "Always on top", KeyEvent.VK_T, listener);
			break;
		case SAVE_LOG:
			item = buildItem(id, "Save", KeyEvent.VK_S, listener);
			break;
		case CHANGE_FONTSIZE:
			item = buildItem(id, "Change Fontsize", KeyEvent.VK_P, listener);
			break;
		case REMOVE_ALL_TABS:
			item = buildItem(id, "Remove All Tabs", KeyEvent.VK_R, listener);
			break;
		case SESSION_IMPORT:
			item = buildItem(id, "Import", KeyEvent.VK_I, listener);
			break;
		case SESSION_EXPORT:
			item = buildItem(id, "Export", KeyEvent.VK_X, listener);
			break;
		default:
			System.out.println("No Button available for id " + id);
		}

		if(item != null) _items.add(item);

		return item;
	}

	private static JMenuItem buildItem(final MenuButton id, final String title, final int keyEvent,
			final ActionListener listener)
	{
		JMenuItem item = new JMenuItem(title, keyEvent);
		item.setAccelerator(KeyStroke.getKeyStroke("control "
				+ String.valueOf(((char)(keyEvent))).toUpperCase()));
		item.setActionCommand(id.toString());
		item.addActionListener(listener);
		item.setName(""+id);

		return item;
	}

	public static List<JMenuItem> getItems()
	{
		return _items;
	}
}
