package com.ecec.rweber.time.tracker.gui;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.Log;

public class AllLogViewer extends LogViewerTemplate {
	private static final long serialVersionUID = 1L;
	private List<Log> m_report = null;
	private LogTableModel m_model = null;
	
	public AllLogViewer(ActivityManager manager){
		super("Log Viewer",manager);
	}

	@Override
	protected DefaultTableModel createTableModel(Date startDate, Date endDate) {
		
		//generate the report
		m_report = g_manage.generateReport(startDate.getTime(), endDate.getTime());
		
		m_model = new LogTableModel(new String[]{"Activity Name","Start Date","End Date","Total Minutes","Description"},m_report.size());
		
		//add the data to the table
		Log aLog = null;
		for(int count = 0; count < m_report.size(); count ++)
		{
			aLog = m_report.get(count);
			m_model.setValueAt(aLog.getActivity(), count, 0);
			m_model.setValueAt(aLog.getStartDate().toString(), count, 1);
			m_model.setValueAt(aLog.getEndDate().toString(), count, 2);
			m_model.setValueAt(aLog.getTotal() + "",count,3);
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
					
					g_manage.doActivity(aLog);
				}
			}
			
		});
		
		return m_model;
	}
}
