package com.ecec.rweber.time.tracker.gui;


import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.LogGroup;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class GroupLogViewer extends LogViewerTemplate{
	private static final long serialVersionUID = -4190305067721655539L;

	public GroupLogViewer(ActivityManager manage) {
		super("Group Log Viewer", manage, 1);
	}
	
	@Override
	protected DefaultTableModel createTableModel(Date startDate, Date endDate) {
		
		//generate the report
		List<LogGroup> report = g_manage.generateGroupReport(startDate.getTime(), endDate.getTime());
		
		DefaultTableModel tModel = new LogTableModel(new String[]{"Activity Name","Total Time","Time Unit"},report.size());
		
		//add the data to the table
		LogGroup aLog = null;
		for(int count = 0; count < report.size(); count ++)
		{
			aLog = report.get(count);
			tModel.setValueAt(aLog.getActivity(), count, 0);
			tModel.setValueAt(aLog.getTotal(this.getTimeFormat()),count,1);
			tModel.setValueAt(TimeFormatter.toString(this.getTimeFormat()), count, 2);
		}
		
		return tModel;
	}

	@Override
	protected void deleteRowImpl(int row) {
		//do nothing
	}

	@Override
	protected TableSorter createTableSorter(TableModel model) {
		return new TableSorter(model,TableSorter.generateSortOrder(0), new int[] {2});
	}

}
