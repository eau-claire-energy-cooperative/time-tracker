package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public abstract class DialogWindow extends JPanel {
	protected int HEIGHT = 300;
	protected int WIDTH = 500;
	protected boolean m_shouldSave = false;
	
	public DialogWindow(){
		this.setLayout(new BorderLayout(20,15));
	}
	
	public abstract void setup();
	public abstract int[] getSelected();
	
	protected void activitySelected(){
		Window win = SwingUtilities.getWindowAncestor(this);
		
		if (win != null) {
			m_shouldSave = true;
			win.dispose();
		}
	}
	
	public boolean shouldSave(){
		return m_shouldSave;
	}
	
}
