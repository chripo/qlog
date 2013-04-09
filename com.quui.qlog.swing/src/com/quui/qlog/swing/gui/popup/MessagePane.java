package com.quui.qlog.swing.gui.popup;

import java.awt.Window;

import javax.swing.JOptionPane;


public class MessagePane
{
	public static void createTabErrorDialog(Window parent)
	{
		JOptionPane.showMessageDialog(parent,
				"To perform this action at least one tab needs to be opened.",
				"Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void createTreeTabErrorDialog(Window parent)
	{
		JOptionPane.showMessageDialog(parent,
				"This action is not availible for tree tabs.",
				"Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static void createSocketErrorDialog(Window parent, int port)
	{
		JOptionPane.showMessageDialog(parent,
				"There is another QLog instance running or the port "+port+" is already in use.",
				"Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void createInfoDialog(Window parent)
	{
		JOptionPane.showMessageDialog(parent,
			"QLog 1.2\r\n\r\nCopyright Max Kugland\r\nhttp://www.splink.org\r\n\r\nUpdate by\r\nChristoph Polcin\r\nhttp://www.polcin.de",
			"QLog", JOptionPane.INFORMATION_MESSAGE);
	}
}
