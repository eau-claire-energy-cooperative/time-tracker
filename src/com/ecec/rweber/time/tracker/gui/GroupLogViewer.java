package com.ecec.rweber.time.tracker.gui;

import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.LogGroup;

public class GroupLogViewer extends LogViewerTemplate{
	private static final long serialVersionUID = -4190305067721655539L;

	public GroupLogViewer(ActivityManager manage) {
		super("Group Log Viewer", manage);
	}

	@Override
	protected DefaultTableModel createTableModel(Date startDate, Date endDate) {
		
		//generate the report
		List<LogGroup> report = g_manage.generateGroupReport(startDate.getTime(), endDate.getTime());
		
		DefaultTableModel tModel = new DefaultTableModel(new String[]{"Activity Name","Total Minutes"},report.size());
		
		//add the data to the table
		LogGroup aLog = null;
		for(int count = 0; count < report.size(); count ++)
		{
			aLog = report.get(count);
			tModel.setValueAt(aLog.getActivity(), count, 0);
			tModel.setValueAt(aLog.getTotal() + "",count,1);
		}
		
		return tModel;
	}

}
