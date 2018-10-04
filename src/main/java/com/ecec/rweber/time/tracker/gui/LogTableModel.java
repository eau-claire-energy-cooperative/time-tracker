package com.ecec.rweber.time.tracker.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class LogTableModel extends DefaultTableModel{
	private static final long serialVersionUID = -5870488478365971908L;
	private List<Integer> m_editableCells = null;
	
	public LogTableModel(String[] strings, int size) {
		super(strings,size);
		
		m_editableCells = new ArrayList<Integer>();
	}

	public void setEditableCol(int column){
		m_editableCells.add(column);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return m_editableCells.contains(column);
	}

	
}
