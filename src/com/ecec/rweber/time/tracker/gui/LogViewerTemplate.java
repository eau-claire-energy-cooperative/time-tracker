package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.util.CSVWriter;
import com.ecec.rweber.time.tracker.util.EmailWriter;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public abstract class LogViewerTemplate extends GuiWindow {
	private static final long serialVersionUID = -4509764308515953846L;
	private Date m_startDate = null;
	private Date m_endDate = null;
	private int m_timeFormat = TimeFormatter.MINUTES;
	
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
				
				m_table.setModel(this.createTableModel(m_startDate, m_endDate));
			}
			else
			{
				JOptionPane.showMessageDialog(this, "The start date must be before the end date");
			}
		}
	}
	
	private void deleteSelectedRow(){
		int row = m_table.getSelectedRow();
		
		if(this.canDelete() && JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this?") == JOptionPane.YES_OPTION)
		{
			//delet the row from the table
			this.deleteRowImpl(row);
			
			//regenerate the table
			this.generateReport();
		}
	}
	
	private Date chooseDate(String title,Date startDate){
		DateChooser sChooser = new DateChooser(this,title);
		sChooser.setLocation(this.getX(), this.getY());
	
		return sChooser.select(startDate);
	}
	
	private JPopupMenu createTablePopup(){
		JPopupMenu result = new JPopupMenu();
		
		if(this.canDelete())
		{
			//create the delete action
			JMenuItem deleteAction = new JMenuItem("Delete Row");
			deleteAction.addActionListener(new ActionListener(){
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					deleteSelectedRow();
				}
				
			});
			result.add(deleteAction);
		}
		
		return result;
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar result = new JMenuBar();
		
		//the file menu
		JMenu file_Menu = new JMenu("File");
		
		JMenuItem file_Export = new JMenuItem("Export CSV");
		file_Export.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				saveReport();
			}
			
		});
		file_Menu.add(file_Export);
		
		JMenuItem file_Email = new JMenuItem("Send Email");
		file_Email.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//create the body
				EmailWriter writer = new EmailWriter();
				
				
				try {
					//create the subject line
					String subject = URLEncoder.encode("Tracked Hours", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
					
					//get the body
					String body = URLEncoder.encode(writer.writeData((DefaultTableModel)m_table.getModel()), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
					
					//try and open the default mail client
					Desktop.getDesktop().mail(new URI(String.format("mailto:?subject=%s&body=%s", subject,body)));
					
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		file_Menu.add(file_Email);
		
		JMenuItem file_Exit = new JMenuItem("Exit");
		file_Exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
			
		});
		file_Menu.add(file_Exit);
		
		result.add(file_Menu);
		
		//add the time menu
		JMenu time_Menu = new JMenu("Time");
		ButtonGroup time_Group = new ButtonGroup();
	
		JRadioButtonMenuItem time_Minutes = new JRadioButtonMenuItem("Minutes");
		time_Minutes.setSelected(true); //default
		time_Minutes.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				m_timeFormat = TimeFormatter.MINUTES;
				generateReport();
			}
			
		});
		time_Group.add(time_Minutes);
		time_Menu.add(time_Minutes);
		
		JRadioButtonMenuItem time_Hours = new JRadioButtonMenuItem("Hours");
		time_Hours.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				m_timeFormat = TimeFormatter.HOURS;
				generateReport();
			}
			
		});
		time_Group.add(time_Hours);
		time_Menu.add(time_Hours);
		
		result.add(time_Menu);
		
		
		return result;
	}
	
	private void saveReport(){
		JFileChooser saveAs = new JFileChooser();
		saveAs.setFileFilter(new FileNameExtensionFilter("CSV Files","csv"));
		saveAs.setAcceptAllFileFilterUsed(false);
		
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
	
	protected abstract void deleteRowImpl(int row);
	
	protected void notifyUpdate(){
		generateReport();
	}
	
	protected int getTimeFormat(){
		return m_timeFormat;
	}
	
	protected boolean canDelete(){
		return false;
	}
	
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
		this.setJMenuBar(this.createMenuBar());
		
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
		m_table.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseReleased(MouseEvent event) {
				//make sure it's a right click
				if(SwingUtilities.isRightMouseButton(event))
				{
					int row = m_table.rowAtPoint(event.getPoint());
					
					//make sure this is a valid row
					if(row >=0 && row < m_table.getRowCount())
					{
						m_table.setRowSelectionInterval(row, row);
						
						JPopupMenu popup = createTablePopup();
						popup.show(event.getComponent(), event.getX(), event.getY());
					}
					else
					{
						m_table.clearSelection();
					}
				}
			}
			
		});
		
		JScrollPane scroller = new JScrollPane(m_table);
		scroller.setAlignmentX(Component.CENTER_ALIGNMENT);
		scroller.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		layoutPane.add(scroller);
		
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,10)));
		
		//generate the report
		generateReport();
	}

}
