package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class DateCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final long serialVersionUID = 1L;
	public final String EDIT_ACTION = "EDIT";
	
	private JFrame m_parent = null;
	private Date m_selectedDate = null;
	private JButton m_activateButton = null;
	
	public DateCellEditor(JFrame parent) {
		super();
		m_parent = parent;
		
		///setup the button that will activate the dialog
		m_activateButton = new JButton();
		m_activateButton.setActionCommand(this.EDIT_ACTION);
		m_activateButton.addActionListener(this);
	}

	@Override 
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals(EDIT_ACTION))
		{
			//open the date/time chooser
			DateChooser sChooser = new DateChooser(m_parent,"Edit Time",true);
			sChooser.setLocation(m_parent.getX(), m_parent.getY());
			Date newDate = sChooser.select(m_selectedDate);
			
			if(newDate != null)
			{
				m_selectedDate = newDate;
			}
			
			fireEditingStopped();
		}
	}
	
	@Override
	public Object getCellEditorValue() {
		//get the date value (could be the same)
		return m_selectedDate;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		//set the starting date/time for the chooser
		m_selectedDate = (Date)value;
		
		return this.m_activateButton;
	}

	
}
