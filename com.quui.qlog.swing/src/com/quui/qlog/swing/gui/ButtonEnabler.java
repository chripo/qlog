package com.quui.qlog.swing.gui;

import java.util.List;

import javax.swing.JMenuItem;

import com.quui.qlog.swing.gui.ButtonFactory.MenuButton;
import com.quui.qlog.swing.gui.tab.ITab;
import com.quui.qlog.swing.gui.tab.Tab;
import com.quui.qlog.swing.gui.tab.TreeTab;



public class ButtonEnabler
{
	public ButtonEnabler()
	{
	}

	public void process(final ITab currentTab, final List<JMenuItem> items)
	{
		try {
			if(currentTab.getClass().equals(Tab.class))
				setForTab(items);
			else if(currentTab.getClass().equals(TreeTab.class))
				setForTreeTab(items);
		} catch (Exception ex) {
			setForNullTab(items);
		}
	}

	private void setForNullTab(List<JMenuItem> items)
	{
		for (final JMenuItem item : items)
			switch (MenuButton.valueOf(item.getName())) {
			case CLEAR:
			case CLEAR_ALL_TABS:
			case REMOVE_ALL_TABS:
			case FILTER:
			case SESSION_EXPORT:
			case SAVE_TAB:
				item.setEnabled(false);
				break;
			}
	}

	private void setForTreeTab(List<JMenuItem> items)
	{
		for (final JMenuItem item : items)
			switch (MenuButton.valueOf(item.getName())) {
			case CLEAR:
			case SAVE_TAB:
				item.setEnabled(false);
				break;
			}
	}

	private void setForTab(List<JMenuItem> items)
	{
		for (final JMenuItem menuItem : items)
			menuItem.setEnabled(true);
	}
}
