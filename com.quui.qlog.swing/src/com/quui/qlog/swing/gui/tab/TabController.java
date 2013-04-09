package com.quui.qlog.swing.gui.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.quui.utils.event.Distributor;


public class TabController extends Distributor implements ActionListener, ChangeListener {
	private JTabbedPane _tabPane;
	private List<ITab> _tabList;

	public TabController() {
		_tabList = new ArrayList<ITab>();
		_tabPane = new JTabbedPane();
		_tabPane.setOpaque(true);
		_tabPane.addChangeListener(this);
	}

	public JTabbedPane getJTabbedPane() {
		return _tabPane;
	}

	public synchronized void addTab(ITab tab) {
		_tabList.add(tab);
		int idx = _tabList.size() - 1;

		_tabPane.addTab(tab.getName(), tab.getTabComponent());
		TabBtnComp btn = new TabBtnComp(tab.getName());
		btn.addActionListener(this);
		_tabPane.setTabComponentAt(idx, btn);

		if(idx <= 9)
			_tabPane.setMnemonicAt(idx,  48 + ((idx+1)%10));

		if(idx == 0)
			_tabPane.setSelectedIndex(idx);
	}

	public synchronized void removeAllTabs() {
		for (ITab t : _tabList)
			t.close();
		_tabList.clear();
		_tabPane.removeAll();
	}

	private synchronized void removeTab(int idx) {
		if(idx < 0 || idx >=  _tabList.size())
			return;

		ITab tab = _tabList.get(idx);
		_tabList.remove(idx);
		TabBtnComp btn = (TabBtnComp)_tabPane.getTabComponentAt(idx);
		btn.removeActionListener(this);

		_tabPane.removeTabAt(idx);
		tab.close();

		idx = _tabPane.getTabCount() > 10 ? 10 : _tabPane.getTabCount();
		for (int i = 0; i < idx; i++)
			_tabPane.setMnemonicAt(i, 48 + ((i+1)%10));
	}

	public synchronized ITab getCurrentTab() {
		return getTabForComponent(_tabPane.getSelectedComponent());
	}

	private synchronized int getTabIndex(ITab tab) {
		int len = _tabList.size();
		for (int i = 0; i < len ; i++)
			if (_tabList.get(i).equals(tab))
				return i;

		return -1;
	}

	public synchronized  ITab getTabForName(String name) {
		for (ITab t : _tabList) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}

	private synchronized  ITab getTabForComponent(Component c) {
		for (ITab t : _tabList) {
			if (t.getTabComponent().equals(c)) {
				return t;
			}
		}
		return null;
	}

	public synchronized void stateChanged(ChangeEvent e) {
		int index = _tabPane.getSelectedIndex();
		if (index != -1)
			_tabPane.setBackgroundAt(index, Color.LIGHT_GRAY);

		distribute(new TabControllerEvent(this, TabControllerEvent.Tab.TAB_CHANGED));
	}

	public synchronized List<ITab> getTabList() {
		return _tabList;
	}

	public synchronized void notifyAboutIncomingMsg(String source) {
		ITab t = getTabForName(source);
		int index = getTabIndex(t);
		if (!getCurrentTab().equals(t))
			_tabPane.setBackgroundAt(index, Color.GRAY);
	}

	public void actionPerformed(ActionEvent e) {
		removeTab(_tabPane.indexOfTabComponent(((JButton)e.getSource()).getParent()));
	}
}
