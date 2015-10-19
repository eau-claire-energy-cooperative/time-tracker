package com.ecec.rweber.time.tracker.gui;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.Log;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class AllLogViewer extends LogViewerTemplate {
	private static final long serialVersionUID = 1L;
	private List<Log> m_report = null;
	private LogTableModel m_model = null;
	private SimpleDateFormat m_dateFormat = null;
	
	public AllLogViewer(ActivityManager manager){
		super("Log Viewer",manager);
		
		m_dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:KK a");
	}
	
	@Override
	protected DefaultTableModel createTableModel(Date startDate, Date endDate) {
		
		//generate the report
		m_report = g_manage.generateReport(startDate.getTime(), endDate.getTime());
		
		m_model = new LogTableModel(new String[]{"Activity Name","Start Date","End Date","Total Time","Description"},m_report.size());
		
		//add the data to the table
		Log aLog = null;
		for(int count = 0; count < m_report.size(); count ++)
		{
			aLog = m_report.get(count);

			m_model.setValueAt(aLog, count, 0);
			m_model.setValueAt(m_dateFormat.format(aLog.getStartDate()), count, 1);
			m_model.setValueAt(m_dateFormat.format(aLog.getEndDate()), count, 2);
			m_model.setValueAt(aLog.getTotal(this.getTimeFormat()) + " " + TimeFormatter.toString(this.getTimeFormat()),count,3);
			m_model.setValueAt(aLog.getDescription(),count,4);
		}
		
		//set the description as editable
		m_model.setEditableCol(4);
		
		m_model.addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent event) {
				
				if(event.getColumn() == 4)
				{
					//get the log for this row
					Log aLog = m_report.get(event.getFirstRow());
					aLog.setDescription(m_model.getValueAt(event.getFirstRow(), event.getColumn()).toString());
					
					g_manage.saveEntry(aLog);
				}
			}
			
		});
		
		return m_model;
	}

	@Override
	protected void deleteRowImpl(int row) {
		//get the log object and delete it
		Log aLog = (Log)m_table.getValueAt(row, 0);
		g_manage.deleteEntry(aLog);
	}
	
	@Override 
	public boolean canDelete(){
		return true;
	}
}
