package com.ecec.rweber.time.tracker.gui;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.Log;

public class AllLogViewer extends LogViewerTemplate {
	private static final long serialVersionUID = 1L;
	
	public AllLogViewer(ActivityManager manager){
		super("Log Viewer",manager);
	}

	@Override
	protected DefaultTableModel createTableModel(Date startDate, Date endDate) {
		
		//generate the report
		List<Log> report = g_manage.generateReport(startDate.getTime(), endDate.getTime());
		
		DefaultTableModel tModel = new DefaultTableModel(new String[]{"Activity Name","Start Date","End Date","Total Minutes","Description"},report.size());
		
		//add the data to the table
		Log aLog = null;
		for(int count = 0; count < report.size(); count ++)
		{
			aLog = report.get(count);
			tModel.setValueAt(aLog.getActivity(), count, 0);
			tModel.setValueAt(aLog.getStartDate().toString(), count, 1);
			tModel.setValueAt(aLog.getEndDate().toString(), count, 2);
			tModel.setValueAt(aLog.getTotal() + "",count,3);
			tModel.setValueAt(aLog.getShortDescription(),count,4);
		}
		
		return tModel;
	}
}
