package com.quui.qlog.swing.gui.tab;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class AutoScrollHelper {

	interface OnScrollListener {
		void onScrollListener(boolean value);
	}

	static public void setupListener(final JScrollPane p, final OnScrollListener l) {
		final JScrollBar sb = p.getVerticalScrollBar();

		final MouseWheelListener mwl = new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				l.onScrollListener(computeScoll(sb));
			}
		};

		final MouseListener ml = new MouseListener() {

			public void mouseReleased(MouseEvent arg0) {
				l.onScrollListener(computeScoll(sb));
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseClicked(MouseEvent arg0) {
			}
		};

		for (final Component c : sb.getComponents())
			try {
				((JButton)c).addMouseListener(ml);
			} catch (Exception e) {}
		sb.addMouseListener(ml);
		sb.addMouseWheelListener(mwl);
		p.addMouseWheelListener(mwl);
	}

	static boolean computeScoll(final JScrollBar sb) {
		return (sb.getValue() + sb.getVisibleAmount()) == sb.getMaximum();
	}
}
