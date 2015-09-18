package com.ecec.rweber.time.tracker.gui;
import java.awt.AWTException;

import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.Log;
import com.ecec.rweber.time.tracker.Timer;
import com.ecec.rweber.time.tracker.util.Notifier;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class TrayService implements HotkeyListener {
	private final String PROGRAM_NAME = "Time Tracker";
	private Logger m_log = null;
	private ActivityManager m_actManage = null;
	private Timer m_timer = null;
	
	//for the gui
	private TrayIcon m_trayIcon = null;
	private MenuItem m_isRunning = null;
	
	public TrayService(){
		setupLogger();
		
		//add the global hotkey
		JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN, (int)'Q');
		JIntellitype.getInstance().addHotKeyListener(this);
		
		m_timer = new Timer();
		m_actManage = new ActivityManager();
	}
	
	private void setupLogger(){
		String directory = System.getProperty("user.dir");
		
		m_log = Logger.getLogger(this.getClass());
		
		Logger rootLog = Logger.getRootLogger();
		rootLog.setLevel(Level.INFO);
		
		try{
			//setup the console
			rootLog.addAppender(new ConsoleAppender(new SimpleLayout(),ConsoleAppender.SYSTEM_OUT));
			rootLog.addAppender(new DailyRollingFileAppender(new PatternLayout("%p %d{DATE} - %m %n"),directory + "/logs/tracker.log","yyyy-ww"));
		}
		catch(Exception e)
		{
			m_log.error("Cannot write to log file");
			e.printStackTrace();
		}
	}
	
	private Image createImage(String path, String description) {
	       File f = new File(path);
	       
	        if (path == null) {
	            m_log.error("Resource not found: " + path);
	            return null;
	        } else {
	            return (new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath()), description)).getImage();
	        }
	    }
	
	private Log activityPrompt(){
		Log result = null;
		
		int height = 300;
		int width = 500;
		
		//open a dialog box to select the activity you were doing
		SelectActivityDialog selectBox = new SelectActivityDialog(m_actManage);
		selectBox.setup(m_timer.toString());
		
		JDialog dialog = new JDialog(null,"Choose Activity",ModalityType.APPLICATION_MODAL);
		dialog.setIconImage(new ImageIcon("resources/timer-small.png").getImage());
		dialog.setSize(width, height);
		
		Container contentPane = dialog.getContentPane();
		contentPane.setSize(width,height);
		contentPane.add(selectBox);
		
		//open in the middle of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(screenSize.getWidth()/2 - (width/2)), (int)(screenSize.getHeight()/2 - (height/2)));
		
		dialog.pack();
		dialog.setVisible(true);
		
		if(selectBox.shouldSave())
		{
			result = new Log(m_actManage.getActivity(selectBox.getSelected()),m_timer,selectBox.getDescription());
		}
		
		return result;
	}
	
	private void toggleTimer(){
		if(m_timer.getState() != Timer.RUNNING)
		{
			//start the timer
			m_timer.start();
			m_trayIcon.displayMessage(PROGRAM_NAME, "Timer started", MessageType.INFO);
		}
		else
		{
			//stop the timer
			m_timer.stop();
			
			//ask the user for the activity
			Log activity = this.activityPrompt();
			
			if(activity != null)
			{
				m_actManage.saveEntry(activity);
				
				m_trayIcon.displayMessage(PROGRAM_NAME, activity.getActivity() +  " Saved", MessageType.INFO);
			}
			
			m_timer.reset();
		}
	}
	
	public void run(){
		//Check the SystemTray support
        if (!SystemTray.isSupported()) {
            m_log.error("SystemTray is not supported");
            return;
        }
        
        final PopupMenu popup = new PopupMenu();
        m_trayIcon = new TrayIcon(createImage("resources/timer-small.png", PROGRAM_NAME));
        m_trayIcon.setToolTip(PROGRAM_NAME);
        
        final SystemTray tray = SystemTray.getSystemTray();
        
        //create the menu items
        m_isRunning = new MenuItem("Not Running");
        
        Menu reportMenu = new Menu("Reports");
        MenuItem normalReport = new MenuItem("All Logs Report");
        MenuItem groupReport = new MenuItem("Grouped Logs Report");
        reportMenu.add(normalReport);
        reportMenu.add(groupReport);
        
        MenuItem activitiesItem = new MenuItem("Activities");
        MenuItem exitItem = new MenuItem("Exit");
        
        //add the menu items to the popup menu
        popup.add(m_isRunning);
        popup.addSeparator();
        popup.add(reportMenu);
        popup.add(activitiesItem);
        popup.add(exitItem);
        
        //add menu to tray
        m_trayIcon.setPopupMenu(popup);
        m_trayIcon.addMouseListener(new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent e) {
				
				//check if this is a right click event
				if(SwingUtilities.isRightMouseButton(e))
				{
					if(m_timer.getState() == Timer.RUNNING)
					{
						m_isRunning.setLabel("Running: " + m_timer.toString());
					}
					else
					{
						m_isRunning.setLabel("Not Running");
					}
				}
			}
        	
        });
        try {
            tray.add(m_trayIcon);
        } catch (AWTException e) {
            m_log.error("TrayIcon could not be added.");
            return;
        }
        
        //setup the listeners for events 
        m_isRunning.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//manual way to start/stop timer
				toggleTimer();
			}
        	
        });
        
        normalReport.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AllLogViewer viewer = new AllLogViewer(m_actManage);
				viewer.addNotifier(new Notifier(){

					@Override
					public void onMessage(String message, MessageType level) {
						m_trayIcon.displayMessage(PROGRAM_NAME, message, level);
					}
					
				});
				viewer.run();
			}
        	
        });
        
        groupReport.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GroupLogViewer viewer = new GroupLogViewer(m_actManage);
				viewer.addNotifier(new Notifier(){

					@Override
					public void onMessage(String message, MessageType level) {
						m_trayIcon.displayMessage(PROGRAM_NAME, message, level);
					}
					
				});
				viewer.run();
			}
        	
        });
        
        activitiesItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ActivityTable table = new ActivityTable(m_actManage);
				table.run();
			}
        	
        });
        
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	JIntellitype.getInstance().unregisterHotKey(1);
                tray.remove(m_trayIcon);
                System.exit(0);
            }
        });
	}

	@Override
	public void onHotKey(int ident) {
		if(ident == 1){
			toggleTimer();
		}
	}
	
	public static void main(String[] args) {
		
		//set the look and feel
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			System.exit(1);
		}
		
		TrayService g = new TrayService();
		g.run();
	}

}
