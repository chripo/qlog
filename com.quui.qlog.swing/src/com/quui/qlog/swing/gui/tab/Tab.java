package com.quui.qlog.swing.gui.tab;

import java.awt.Dimension;
import java.io.StringReader;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import com.quui.qlog.core.Filter;
import com.quui.qlog.core.PropertiesReader;
import com.quui.qlog.core.TableBuilder;
import com.quui.qlog.swing.gui.Window;
import com.quui.qlog.swing.gui.popup.FontSizePopUp;
import com.quui.qlog.swing.gui.tab.AutoScrollHelper.OnScrollListener;
import com.quui.utils.util.IDestroyable;


public class Tab implements ITab {
	private PropertiesReader _reader;
	private TableBuilder _tableBuilder;
	private JScrollPane _scrollPane;
	private IDestroyable _client;
	private String _name;
	private Window _window;
	private boolean _isAutoScroll = true;

	public Tab(final Window window, final PropertiesReader reader, final IDestroyable client, final String name) {
		_reader = reader;
		_window = window;
		_name = name;
		_client = client;
		_tableBuilder = new TableBuilder(FontSizePopUp.getFontSize());
		_scrollPane = createTabContent();
		getEditorPane().setText(_tableBuilder.getContent());
		_window.addTab(this);

		AutoScrollHelper.setupListener(_scrollPane, new OnScrollListener() {

			public void onScrollListener(boolean value) {
				_isAutoScroll = value;
			}
		});
	}

	private JScrollPane createTabContent() {
		final JEditorPane textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(_reader.getWidth(), _reader.getHeight()));
		textPane.setContentType("text/html");
		textPane.setEditorKit(new HTMLEditorKit());

		final JScrollPane scrollPane = new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setWheelScrollingEnabled(true);

		return scrollPane;
	}

	private JEditorPane getEditorPane() {
		try {
			return (JEditorPane) _scrollPane.getViewport().getView();
		} catch (Exception e) {}
		return null;
	}

	public void incomingCommand(final String command) {
		if (_window.getClearOnConnect()) {
			if ("clear".equals(command) || "clearonconnect".equals(command))
				clear();
		}
	}

	public void incomingMessage(final String color, final String message) {
		final String newMsg = _tableBuilder.buildHTML(color, message.replace(" ", "&nbsp;"));
		if (newMsg == null)
			return;

		try {
			final JEditorPane p = getEditorPane();

			((HTMLEditorKit) p.getEditorKit()).read(
						new StringReader(newMsg),
						p.getDocument(),
						p.getDocument().getLength());

			if (_isAutoScroll)
				p.setCaretPosition(p.getDocument().getLength());

		} catch (Exception e) {
			e.printStackTrace();
		}

		_window.notifyAboutIncomingMsg(_name);
	}

	public void close() {
		try {
			_client.destroy();
			_tableBuilder.clear();
		} catch (Exception ex) {}
		_reader = null;
		_client = null;
		_tableBuilder = null;
		_scrollPane = null;
	}

	public void clear() {
		_tableBuilder.clear();
		getEditorPane().setText(_tableBuilder.getCss());
	}

	public JScrollPane getTabComponent() {
		return _scrollPane;
	}

	public String getName() {
		return _name;
	}

	public TableBuilder getTableBuilder() {
		return _tableBuilder;
	}

	public String getFilter() {
		return _tableBuilder.getFilter();
	}

	public void applyFilter(String filter) {
		_tableBuilder.setFilter(filter);
		getEditorPane().setText(Filter.find(_tableBuilder));
	}

	public void changeFontSize(int size) {
		_tableBuilder.changeFontSize(size);
	}
}
