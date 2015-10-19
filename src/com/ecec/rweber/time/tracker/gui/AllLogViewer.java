package com.ecec.rweber.time.tracker.gui;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
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
		
		m_dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	}
	
	private Date checkTime(Log aLog, String dateString, boolean isStart){
		Date result = null;
		
		try{
			//check that it's parseable
			Date newDate = m_dateFormat.parse(dateString);
			
			if(isStart)
			{
				if(newDate.getTime() <= aLog.getEndDate().getTime())
				{
					//make sure the start happens before the end
					result = newDate;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Start date/time must be before ending date/time");
				}
			}
			else if(!isStart)
			{
				if(newDate.getTime() >= aLog.getStartDate().getTime())
				{
					//make sure end is after start
					result = newDate;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "End date/time must be after starting date/time");
				}
			}
		}
		catch(ParseException pe)
		{
			JOptionPane.showMessageDialog(null, "Date/Time format is incorrect");
		}
		
		return result;
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
		m_model.setEditableCol(1);
		m_model.setEditableCol(2);
		m_model.setEditableCol(4);
		
		m_model.addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent event) {
				
				if(event.getColumn() == 1)
				{
					//we are formatting the start date
					Log aLog = m_report.get(event.getFirstRow());
					
					Date newDate = checkTime(aLog,m_model.getValueAt(event.getFirstRow(), 1).toString(),true);
					
					if(newDate != null)
					{
						aLog.setStartDate(newDate);
						g_manage.saveEntry(aLog);
					
						notifyUpdate();
					}
					else
					{
						//reset the date
						m_model.setValueAt(m_dateFormat.format(aLog.getStartDate()), event.getFirstRow(), 1);
					}
				}
				else if(event.getColumn() == 2)
				{
					//we are formatting the start date
					Log aLog = m_report.get(event.getFirstRow());
					
					Date newDate = checkTime(aLog,m_model.getValueAt(event.getFirstRow(), 2).toString(),false);
					
					if(newDate != null)
					{
						aLog.setEndDate(newDate);
						g_manage.saveEntry(aLog);
					
						notifyUpdate();
					}
					else
					{
						//reset the date
						m_model.setValueAt(m_dateFormat.format(aLog.getEndDate()), event.getFirstRow(), 2);
					}
	
				}
				else if(event.getColumn() == 4)
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
