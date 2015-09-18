package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.util.CSVWriter;

public abstract class LogViewerTemplate extends GuiWindow {
	private Date m_startDate = null;
	private Date m_endDate = null;
	
	//for the gui
	private JLabel l_startDate = null;
	private JLabel l_endDate = null;
	protected JTable m_table = null;
	
	public LogViewerTemplate(String windowName, ActivityManager manage) {
		super(windowName, manage);
	}

	private void generateReport(){
		if(m_startDate != null && m_endDate != null)
		{
			//make sure the start date is older than the end date
			if(m_startDate.getTime() < m_endDate.getTime())
			{
				SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYY");
				
				//set the labels
				l_startDate.setText("Start: " + formatter.format(m_startDate));
				l_endDate.setText("End: " + formatter.format(m_endDate));
				
				m_table.setModel(this.createTableModel(m_startDate, m_endDate));;
			}
			else
			{
				JOptionPane.showMessageDialog(this, "The start date must be before the end date");
			}
		}
	}
	
	private void saveReport(){
		JFileChooser saveAs = new JFileChooser();
		saveAs.setFileFilter(new FileNameExtensionFilter("CSV Files","csv"));
		
		int returnVal = saveAs.showSaveDialog(this);
		
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			String file = saveAs.getSelectedFile().toString();
			
			if(!file.endsWith(".csv"))
			{
				file = file + ".csv";
			}
			
			if(!saveReport(file))
			{
				this.sendNotification("Error writing " + file, MessageType.ERROR);
			}
			else
			{
				this.sendNotification(file + " saved", MessageType.INFO);
			}
		}
		
	}
	
	private Date chooseDate(String title,Date startDate){
		DateChooser sChooser = new DateChooser(this,title);
		sChooser.setLocation(this.getX(), this.getY());
	
		return sChooser.select(startDate);
	}
	
	private boolean saveReport(String filename){
		boolean result = true;
	
		//create the CSVWriter
		CSVWriter writer;
		try {
			writer = new CSVWriter(filename);
			
			//generate the table data
			writer.writeData((DefaultTableModel)m_table.getModel());
			
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	protected abstract DefaultTableModel createTableModel(Date startDate, Date endDate);
	
	@Override
	protected void setupInformation() {
		this.HEIGHT = 400;
		this.WIDTH = 800;
		
		//set the dates to go back one month by default
		m_startDate = new Date();
		m_endDate = new Date();
		
		Calendar d1 = new GregorianCalendar();
		d1.setTime(m_startDate);
		d1.add(GregorianCalendar.DAY_OF_MONTH, -30);
		m_startDate = d1.getTime();

	}

	@Override
	protected void viewModal(Container layoutPane) {
		
		layoutPane.setLayout(new BoxLayout(layoutPane,BoxLayout.Y_AXIS));
		
		JComponent wrapper1= new JPanel();
		wrapper1.setSize(new Dimension(WIDTH,100));
		
		l_startDate = new JLabel("");
		l_startDate.setAlignmentX(Component.RIGHT_ALIGNMENT);
		wrapper1.add(l_startDate);
		
		//add the start date button
		JButton b_startDate = new JButton("Set");
		b_startDate.setAlignmentX(Component.RIGHT_ALIGNMENT);
		b_startDate.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_startDate = chooseDate("Choose Start Date",m_startDate);
				
				generateReport();
			}
			
		});
		wrapper1.add(b_startDate);
		
		l_endDate = new JLabel("");
		l_endDate.setAlignmentX(Component.RIGHT_ALIGNMENT);
		wrapper1.add(l_endDate);
		
		//add the end date button
		JButton b_endDate = new JButton("Set");
		b_endDate.setAlignmentX(Component.RIGHT_ALIGNMENT);
		b_endDate.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				m_endDate = chooseDate("Choose End Date",m_endDate);
				
				generateReport();
			}
			
		});
		wrapper1.add(b_endDate);
		wrapper1.setAlignmentX(Container.CENTER_ALIGNMENT);
		
		layoutPane.add(wrapper1);
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,10)));
		
		m_table = new JTable();
		m_table.getTableHeader().setReorderingAllowed(true);
		m_table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scroller = new JScrollPane(m_table);
		scroller.setAlignmentX(Component.CENTER_ALIGNMENT);
		scroller.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		layoutPane.add(scroller);
		
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,10)));
		
		//add an export button
		JButton b_export = new JButton("Export CSV");
		b_export.setAlignmentX(Component.CENTER_ALIGNMENT);
		b_export.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				saveReport();
			}
			
		});
		layoutPane.add(b_export);
		
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,10)));
		
		//generate the report
		generateReport();
	}

}
