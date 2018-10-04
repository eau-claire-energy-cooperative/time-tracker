package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class CustomCountdownDialog extends JPanel {
	private static final long serialVersionUID = -8610404151190228884L;
	private JTextField m_time = null;
	private JComboBox<String> m_formatSelect = null;
	private boolean m_shouldSave = false;
	
	public CustomCountdownDialog(){
		this.setLayout(new BorderLayout(20,15));
	}
	
	private void selectOK(){
		Window win = SwingUtilities.getWindowAncestor(this);
		
		if (win != null) {
			m_shouldSave = true;
			win.dispose();
		}
	}
	
	public void setup(){
		Font f = new Font(Font.SERIF,Font.PLAIN,15);
		this.setBorder(new EmptyBorder(10,10,10,10));
		
		m_time = new JTextField(5);
		m_time.setFont(f);
		m_time.setText("10");
		
		m_formatSelect = new JComboBox<String>(new String[]{"Seconds","Minutes","Hours"});
		m_formatSelect.setSelectedIndex(1);
		m_formatSelect.setFont(f);
		
		JButton b_select = new JButton("OK");
		b_select.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				selectOK();
			}
			
		});
		
		this.add(m_time,BorderLayout.LINE_START);
		this.add(m_formatSelect, BorderLayout.CENTER);
		this.add(b_select, BorderLayout.LINE_END);
		
		this.setSize(500, 300);
	}
	
	public int getTime(){
		return Integer.parseInt(m_time.getText());
	}
	
	public int getFormat(){
		return m_formatSelect.getSelectedIndex() + 1;
	}
	
	public boolean shouldSave(){
		return m_shouldSave;
	}
}
