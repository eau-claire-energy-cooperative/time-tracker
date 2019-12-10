package com.ecec.rweber.time.tracker.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat m_dateFormat = null;
	
	public DateCellRenderer() {
		super();
		//will render any date given with this string
		m_dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	}
	
	@Override
	protected void setValue(Object value) {
		setText(m_dateFormat.format((Date)value));
	}
	
	public String formatDate(Object value) {
		return m_dateFormat.format((Date)value);
	}
	
}
