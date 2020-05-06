package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SetMinimumsDialog extends DialogWindow {
	private static final long serialVersionUID = 1L;
	private JComboBox<Integer> m_minTime = null;
	private JComboBox<Integer> m_roundTime = null;
	
	public SetMinimumsDialog() {
		super();
		
		m_minTime = new JComboBox<Integer>(DateChooser.MINUTES);
		m_roundTime = new JComboBox<Integer>(new Integer[]{0,5,10,15,30,60});
	}
	
	@Override
	public void setup() {
		Font f = new Font(Font.SERIF,Font.PLAIN,15);
		this.setBorder(new EmptyBorder(10,10,10,10));
		
		//use a box layout for this dialog
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel l_directions = new JLabel();
		l_directions.setText("<html>Minimum time refers to the smallest amount of time that can be set. A setting of 0 means no minimum. <br> Round to nearest will always round the activity time to the nearest time increment given, 0 being don't round.</html>");
		l_directions.setFont(f);
		l_directions.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(l_directions);
		//create empty area
		this.add(Box.createRigidArea(new Dimension(0,10)));
		
		//create a panel for the minimum time
		JPanel minPanel = new JPanel();
		JLabel l_minTime = new JLabel("Minimum Time (minutes): ");
		l_minTime.setFont(f);
	
		minPanel.add(l_minTime);
		minPanel.add(m_minTime);
		
		//create a panel for the round time
		JPanel roundPanel = new JPanel();
		JLabel l_roundTime = new JLabel("Round to nearest (minutes): ");
		l_roundTime.setFont(f);
		
		roundPanel.add(l_roundTime);
		roundPanel.add(m_roundTime);
		
		this.add(minPanel);
		this.add(roundPanel);
		
		//add the save and cancel buttons
		JPanel buttonsPanel = new JPanel();
				
		JButton b_cancel = new JButton("Cancel");
		b_cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelClicked();
			}
			
		});
		
		JButton b_save = new JButton("Save");
		b_save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveClicked();
			}
			
		});

		buttonsPanel.add(b_save);
		buttonsPanel.add(b_cancel);
		
		//create empty area
		this.add(Box.createRigidArea(new Dimension(0,5)));

		this.add(buttonsPanel);
		
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH,HEIGHT));
	}

	@Override
	public Map<String,Object> getResults() {
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("minTime", m_minTime.getSelectedItem().toString());
		result.put("roundTime", m_roundTime.getSelectedItem().toString());
		
		return result;
	}

}
