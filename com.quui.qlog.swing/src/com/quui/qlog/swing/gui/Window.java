package com.quui.qlog.swing.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import com.quui.qlog.core.PropertiesReader;
import com.quui.qlog.swing.gui.ButtonFactory.MenuButton;
import com.quui.qlog.swing.gui.popup.FilterPopUp;
import com.quui.qlog.swing.gui.popup.FontSizePopUp;
import com.quui.qlog.swing.gui.popup.IPopUp;
import com.quui.qlog.swing.gui.popup.MessagePane;
import com.quui.qlog.swing.gui.tab.ITab;
import com.quui.qlog.swing.gui.tab.Tab;
import com.quui.qlog.swing.gui.tab.TabController;
import com.quui.qlog.swing.gui.tab.TabControllerEvent;
import com.quui.qlog.swing.util.Session;
import com.quui.utils.event.IEvent;
import com.quui.utils.event.IListener;

public class Window extends JFrame implements ActionListener, IListener {
	private static final long serialVersionUID = 1L;

	private ImageIcon _checkIcon = new ImageIcon("img/check.png");
	private List<IPopUp> _openPopUps = new ArrayList<IPopUp>();
	private boolean _clearOnConnect;
	private boolean _alwaysOnTop;
	private TabController _tabCtrl;
	private ButtonEnabler _enabler;

	public Window(PropertiesReader reader) {
		setLocation(new Point(reader.getX(), reader.getY()));

		_clearOnConnect = reader.getClearOnConnect();
		_alwaysOnTop = reader.getAlwaysOnTop();

		setTitle("QLog");

		_enabler = new ButtonEnabler();

		_tabCtrl = new TabController();
		_tabCtrl.register(TabControllerEvent.Tab.TAB_CHANGED, this);

		Look.createWindowLook(this);
		createGui();
		add(_tabCtrl.getJTabbedPane());
	}

	public void onEvent(IEvent event) {
		setSelectedTab();
	}

	private void setSelectedTab() {
		final ITab tab = _tabCtrl.getCurrentTab();

		if (tab != null) {
			for (IPopUp popup : _openPopUps) {
				popup.setCurrentTab(tab);
				if (popup.getClass().equals(FilterPopUp.class)) {
					popup.setText(tab.getFilter());
				}
			}
		}
		_enabler.process(tab, ButtonFactory.getItems());
	}

	public void notifyAboutIncomingMsg(String source) {
		_tabCtrl.notifyAboutIncomingMsg(source);
	}

	public void addTab(ITab tab) {
		_tabCtrl.addTab(tab);
	}

	public List<IPopUp> getOpenPopUpList() {
		return _openPopUps;
	}

	public void addPopUp(IPopUp popup) {
		_openPopUps.add(popup);
	}

	public void removePopUp(IPopUp popup) {
		_openPopUps.remove(popup);
	}

	public ITab getTabForName(String name) {
		return _tabCtrl.getTabForName(name);
	}

	private void createGui() {
		createMenu();
		setSelectedTab();
		setLayout(new BorderLayout());
	}

	private void createMenu() {
		final JMenuBar menubar = new JMenuBar();
		final JMenu menu = new JMenu("Menu");
		menu.setMnemonic(KeyEvent.VK_M);
		menubar.add(menu);

		final JMenuItem alwaysOnTop = menu.add(ButtonFactory.create(MenuButton.ALWAYS_ON_TOP, this));
		if (_alwaysOnTop) {
			setAlwaysOnTop(_alwaysOnTop);
			alwaysOnTop.setIcon(_checkIcon);
		}

		menu.add(ButtonFactory.create(MenuButton.CHANGE_FONTSIZE, this));


		final JMenuItem clearOnConnect = menu.add(ButtonFactory.create(MenuButton.CLEAR_ON_CONNECT, this));
		if (_clearOnConnect)
			clearOnConnect.setIcon(_checkIcon);

		final JMenu session = new JMenu("Session");
		session.setMnemonic(KeyEvent.VK_N);
		menubar.add(session);
		session.add(ButtonFactory.create(MenuButton.SESSION_IMPORT, this));
		session.add(ButtonFactory.create(MenuButton.SESSION_EXPORT, this));

		final JMenu tab = new JMenu("Tab");
		tab.setMnemonic(KeyEvent.VK_B);
		menubar.add(tab);

		tab.add(ButtonFactory.create(MenuButton.CLEAR, this));
		tab.add(ButtonFactory.create(MenuButton.FILTER, this));
		tab.add(ButtonFactory.create(MenuButton.SAVE_TAB, this));
		tab.add(new JSeparator());
		tab.add(ButtonFactory.create(MenuButton.CLEAR_ALL_TABS, this));
		tab.add(ButtonFactory.create(MenuButton.REMOVE_ALL_TABS, this));

		final JMenu help = new JMenu("Help");
		menubar.add(help);
		help.add(ButtonFactory.create(MenuButton.ABOUT, this));

		setJMenuBar(menubar);
	}

	/**
	 * Called if a button is invoked
	 *
	 * @param evt
	 *            carrys information to distinguish which button has been invoked
	 */
	public void actionPerformed(final ActionEvent evt) {
		switch (MenuButton.valueOf(evt.getActionCommand())) {
		case ABOUT:
			MessagePane.createInfoDialog(this);
			break;

		case CLEAR:
			try {
				_tabCtrl.getCurrentTab().clear();
			} catch (Exception ex) {}
			break;

		case FILTER:
			try {
				new FilterPopUp(this, _tabCtrl.getCurrentTab());
			} catch (Exception ex) {
				MessagePane.createTabErrorDialog(this);
			}
			break;

		case SAVE_TAB:
			try {
				new LogTabSave(this, (Tab) _tabCtrl.getCurrentTab());
			} catch (Exception ex) {
				MessagePane.createTreeTabErrorDialog(this);
			}
			break;

		case CHANGE_FONTSIZE:
			try {
				new FontSizePopUp(this, _tabCtrl.getTabList());
			} catch (Exception ex) {
				MessagePane.createTabErrorDialog(this);
			}
			break;

		case REMOVE_ALL_TABS:
			_tabCtrl.removeAllTabs();
			break;

		case CLEAR_ALL_TABS:
			try {
				for (final ITab t : _tabCtrl.getTabList())
					t.clear();
			} catch (Exception e) {}
			break;

		case SESSION_IMPORT:
			try {
				Session.doimport(this, _tabCtrl);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Fail to import QLog session",
						"Import Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			break;

		case SESSION_EXPORT:
			try {
				Session.export(this, _tabCtrl.getTabList());
			} catch (Exception ex) {
				MessagePane.createTabErrorDialog(this);
			}
			break;

		case ALWAYS_ON_TOP:
			handleAlwaysOnTop((JMenuItem) evt.getSource());
			break;

		case CLEAR_ON_CONNECT:
			handleClearOnConnect((JMenuItem) evt.getSource());
			break;

		default: break;
		}
	}

	private void handleAlwaysOnTop(final JMenuItem item) {
		_alwaysOnTop = !_alwaysOnTop;
		setAlwaysOnTop(_alwaysOnTop);

		if (_alwaysOnTop)
			item.setIcon(_checkIcon);
		else
			item.setIcon(null);
	}

	private void handleClearOnConnect(final JMenuItem item) {
		_clearOnConnect = !_clearOnConnect;

		if (_clearOnConnect)
			item.setIcon(_checkIcon);
		else
			item.setIcon(null);
	}

	public boolean getClearOnConnect() {
		return _clearOnConnect;
	}

	public boolean getAlwaysOnTop() {
		return _alwaysOnTop;
	}
}
