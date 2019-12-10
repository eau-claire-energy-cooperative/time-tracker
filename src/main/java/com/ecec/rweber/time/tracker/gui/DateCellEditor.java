package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class DateCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	private DateCellRenderer m_render = null;
	private JTextField m_text = null;
	
	public DateCellEditor() {
		super(new JTextField());
		m_render = new DateCellRenderer();
		m_text = new JTextField();
	}

	@Override
	public Object getCellEditorValue() {
		//get the new text value entered
		return m_text.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		//render the value with the correct date format
		m_text.setText(m_render.formatDate(value));
		
		return m_text;
	}

	
}
