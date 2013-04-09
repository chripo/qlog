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

	public void process(ITab currentTab, List<JMenuItem> items)
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
		for (JMenuItem item : items)
			switch (MenuButton.valueOf(item.getName())) {
			case CLEAR:
			case FILTER:
			case CHANGE_FONTSIZE:
			case SAVE_LOG:
				item.setEnabled(false);
				break;
			}
	}

	private void setForTreeTab(List<JMenuItem> items)
	{
		for (JMenuItem item : items)
			switch (MenuButton.valueOf(item.getName())) {
			case CLEAR:
			case SAVE_LOG:
				item.setEnabled(false);
				break;
			}
	}

	private void setForTab(List<JMenuItem> items)
	{
		for (JMenuItem menuItem : items)
		{
			menuItem.setEnabled(true);
		}
	}
}
