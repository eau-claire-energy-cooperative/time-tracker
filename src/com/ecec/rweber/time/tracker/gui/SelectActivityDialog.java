package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ecec.rweber.time.tracker.ActivityManager;

public class SelectActivityDialog extends JPanel{
	private static final long serialVersionUID = -8610097326110926200L;
	private ActivityManager m_manage = null;
	private JComboBox m_select = null;
	
	public SelectActivityDialog(ActivityManager manage){
		m_manage = manage;
	}
	
	private void activitySelected(){
		Window win = SwingUtilities.getWindowAncestor(this);
		
		if (win != null) {
			win.dispose();
		}
	}
	
	public void setup(){
		Font f = new Font(Font.SERIF,Font.PLAIN,15);
		
		m_select = new JComboBox(m_manage.getActivities().toArray());
		m_select.setFont(f);
		
		JButton b_save = new JButton("Choose");
		b_save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				activitySelected();
			}
			
		});
		
		this.add(m_select,BorderLayout.CENTER);
		this.add(b_save,BorderLayout.CENTER);
		
		this.setSize(500, 300);
	}
	
	public int getSelected(){
		return m_select.getSelectedIndex();
	}
}
