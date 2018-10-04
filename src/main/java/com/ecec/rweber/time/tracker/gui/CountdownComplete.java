package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CountdownComplete extends JPanel{
	private static final long serialVersionUID = -895377596874562033L;

	public CountdownComplete(){
		this.setLayout(new BorderLayout(20,15));
	}
	
	public void setup(String elapsedTime){
		Font f = new Font(Font.SERIF,Font.PLAIN,30);
		this.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel l_date = new JLabel(elapsedTime + " has elapsed");
		l_date.setFont(f);
		
		this.add(l_date,BorderLayout.CENTER);
		
		this.setSize(500, 300);
	}
}
