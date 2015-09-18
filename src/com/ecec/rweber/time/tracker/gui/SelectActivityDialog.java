package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.ecec.rweber.time.tracker.Activity;
import com.ecec.rweber.time.tracker.ActivityManager;

public class SelectActivityDialog extends JPanel{
	private static final long serialVersionUID = -8610097326110926200L;
	private ActivityManager m_manage = null;
	private boolean m_shouldSave = false;
	private JComboBox<Activity> m_select = null;
	private JTextArea m_descrip = null;
	
	public SelectActivityDialog(ActivityManager manage){
		m_manage = manage;
		this.setLayout(new BorderLayout(20,15));
	}
	
	private void activitySelected(){
		Window win = SwingUtilities.getWindowAncestor(this);
		
		if (win != null) {
			m_shouldSave = true;
			win.dispose();
		}
	}
	
	public void setup(String elapsedTime){
		Font f = new Font(Font.SERIF,Font.PLAIN,15);
		this.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel l_date = new JLabel("Time: " + elapsedTime);
		l_date.setFont(f);
		
		m_select = new JComboBox<Activity>(m_manage.getActivities().toArray(new Activity[0]));
		m_select.setFont(f);
		
		JButton b_save = new JButton("Save");
		b_save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				activitySelected();
			}
			
		});
		
		this.add(m_select,BorderLayout.LINE_START);
		this.add(l_date,BorderLayout.CENTER);
		this.add(b_save,BorderLayout.LINE_END);
		
		m_descrip = new JTextArea("",4,50);
		this.add(m_descrip,BorderLayout.PAGE_END);
		
		this.setSize(500, 300);
	}
	
	public int getSelected(){
		return m_select.getSelectedIndex();
	}
	
	public String getDescription(){
		return m_descrip.getText();
	}
	
	public boolean shouldSave(){
		return m_shouldSave;
	}
}
