package com.ecec.rweber.time.tracker.gui;

import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.Log;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class AllLogViewer extends LogViewerTemplate {
	private static final long serialVersionUID = 1L;
	private List<Log> m_report = null;
	private LogTableModel m_model = null;
	
	public AllLogViewer(ActivityManager manager){
		super("Log Viewer",manager, 3);
	}
	
	private Date checkTime(Log aLog, Date newDate, boolean isStart){
		Date result = null;

		//check the time window
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
		
		
		return result;
	}
	
	@Override
	protected DefaultTableModel createTableModel(Date startDate, Date endDate) {
		
		//generate the report
		m_report = g_manage.generateReport(startDate.getTime(), endDate.getTime());
		
		m_model = new LogTableModel(new String[]{"Activity Name","Start Date","End Date","Total Time","Time Units","Description"}, 
									new Class[] {String.class, Date.class, Date.class, Double.class, String.class, String.class}, m_report.size());
		
		//add the data to the table
		Log aLog = null;
		for(int count = 0; count < m_report.size(); count ++)
		{
			aLog = m_report.get(count);
			
			m_model.setValueAt(aLog.toString(), count, 0);
			m_model.setValueAt(aLog.getStartDate(), count, 1);
			m_model.setValueAt(aLog.getEndDate(), count, 2);
			m_model.setValueAt(aLog.getTotal(this.getTimeFormat()),count,3);
			m_model.setValueAt(TimeFormatter.toString(this.getTimeFormat()), count, 4);
			m_model.setValueAt(aLog.getDescription(),count,5);
		}
		
		//set the description as editable
		m_model.setEditableCol(1);
		m_model.setEditableCol(2);
		m_model.setEditableCol(5);
		
		m_model.addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent event) {
				
				if(event.getColumn() == 1)
				{
					//we are formatting the start date
					Log aLog = m_report.get(event.getFirstRow());
					
					Date newDate = checkTime(aLog,(Date)m_model.getValueAt(event.getFirstRow(), 1),true);

					if(newDate != null)
					{
						aLog.setStartDate(newDate);
						g_manage.saveEntry(aLog);
					
						//also update the time field
						m_model.setValueAt(aLog.getTotal(getTimeFormat()), event.getFirstRow(), 3);
						
						notifyUpdate();
					}
					else
					{
						//reset the date
						m_model.setValueAt(aLog.getStartDate(), event.getFirstRow(), 1);
					}
				}
				else if(event.getColumn() == 2)
				{
					//we are formatting the start date
					Log aLog = m_report.get(event.getFirstRow());
					
					Date newDate = checkTime(aLog,(Date)m_model.getValueAt(event.getFirstRow(), 2),false);
					
					if(newDate != null)
					{
						aLog.setEndDate(newDate);
						g_manage.saveEntry(aLog);
						
						//also update the time field
						m_model.setValueAt(aLog.getTotal(getTimeFormat()), event.getFirstRow(), 3);
						
						notifyUpdate();
					}
					else
					{
						//reset the date
						m_model.setValueAt(aLog.getEndDate(), event.getFirstRow(), 2);
					}
	
				}
				else if(event.getColumn() == 5)
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
	protected TableSorter createTableSorter(TableModel model) {
		//sort by start date on this screen
		return new TableSorter(model,TableSorter.generateSortOrder(1,SortOrder.ASCENDING), new int[] {4,5});
	}
	
	@Override 
	public boolean canDelete(){
		return true;
	}
}
