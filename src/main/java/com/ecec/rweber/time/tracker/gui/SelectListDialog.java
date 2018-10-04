package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import com.ecec.rweber.time.tracker.Activity;

public class SelectListDialog extends DialogWindow {
	private static final long serialVersionUID = 5753445871421295291L;
	private List<Activity> m_activities = null;
	private JList<Activity> m_list = null;
	private int m_initialSelect = 0;
	
	public SelectListDialog(List<Activity> activities, int selected){
		super();
		m_activities = activities;
		m_initialSelect = selected;
		
		//diff width/height for this dialog
		WIDTH = 200;
		HEIGHT = 200;
	}
	
	public void setup(){
		Font f = new Font(Font.SERIF,Font.PLAIN,15);
		this.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel l_text = new JLabel("<html>Select activites to split this time across<br />(CTL-click for multiple)</html>");
		l_text.setPreferredSize(new Dimension(200,70));
		l_text.setFont(f);
		
		m_list = new JList<Activity>(m_activities.toArray(new Activity[0]));
		m_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		m_list.setSelectedIndex(m_initialSelect);
		m_list.setFont(f);
		
		JScrollPane scroller = new JScrollPane(m_list);
		
		JButton b_save = new JButton("OK");
		b_save.setFont(f);
		b_save.setPreferredSize(new Dimension(200,50));
		b_save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				activitySelected();
			}
			
		});
		this.add(l_text,BorderLayout.PAGE_START);
		this.add(scroller,BorderLayout.CENTER);
		this.add(b_save,BorderLayout.PAGE_END);
		
		this.setSize(WIDTH,HEIGHT);
	}

	@Override
	public int[] getSelected() {
		return m_list.getSelectedIndices();
	}
}
