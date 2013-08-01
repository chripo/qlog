package com.quui.qlog.swing.gui.tab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author chripo
 *
 */

public class TabBtnComp extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton btn = null;
	private JLabel label;

	public TabBtnComp(String title) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);

		add( btn = new Btn() );

		label = new JLabel(title);
		add(label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

	public void setTile(final String text) {
		label.setText(text);
	}

	public void addActionListener(ActionListener lstnr) {
		if(btn != null)
			btn.addActionListener(lstnr);
	}

	public void removeActionListener(ActionListener lstnr) {
		if(btn != null)
			btn.removeActionListener(lstnr);
	}

	public void dispose(){
		removeAll();
		btn = null;
	}

	private class Btn extends JButton {
		public Btn() {
			int size = 17;
			setPreferredSize(new Dimension(size, size));
			setUI(new BasicButtonUI());
			setContentAreaFilled(false);
			setFocusable(false);
			setBorderPainted(false);
		}

		public void updateUI() {}

		//paint the cross
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			//shift the image for pressed buttons
			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
			if (getModel().isRollover()) {
				g2.setColor(Color.MAGENTA);
			}
			int delta = 6;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}
	}
}

