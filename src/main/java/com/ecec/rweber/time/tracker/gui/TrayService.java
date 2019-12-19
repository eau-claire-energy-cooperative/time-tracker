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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.CountdownTimer;
import com.ecec.rweber.time.tracker.Log;
import com.ecec.rweber.time.tracker.TimerState;
import com.ecec.rweber.time.tracker.ElapsedTimer;
import com.ecec.rweber.time.tracker.util.DBFile;
import com.ecec.rweber.time.tracker.util.Notifier;
import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class TrayService implements Observer {
	private final String PROGRAM_NAME = "Time Tracker";
	private Logger m_log = null;
	private ActivityManager m_actManage = null;
	private ElapsedTimer m_timer = null;
	private CountdownTimer m_countdown = null;
	
	//for the gui
	private TrayIcon m_trayIcon = null;
	private MenuItem m_isRunning = null;
	
	public TrayService(){
		setupLogger();
		
		//setup timers
		m_timer = new ElapsedTimer();
		m_countdown = new CountdownTimer();
		m_countdown.addObserver(this);
		
		//load the database
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
	
	private List<Log> activityPrompt(){
		List<Log> result = new ArrayList<Log>();
		
		//open a dialog box to select the activity you were doing
		SelectActivityDialog selectBox = new SelectActivityDialog(m_actManage,m_timer.toString());
		selectBox.setup();
		
		JDialog dialog = new JDialog(null,"Choose Activity",ModalityType.APPLICATION_MODAL);
		dialog.setIconImage(new ImageIcon("resources/timer-small.png").getImage());
		dialog.setSize(selectBox.WIDTH, selectBox.HEIGHT);
		
		Container contentPane = dialog.getContentPane();
		contentPane.setSize(selectBox.WIDTH,selectBox.HEIGHT);
		contentPane.add(selectBox);
		
		//open in the middle of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(screenSize.getWidth()/2 - (selectBox.WIDTH/2)), (int)(screenSize.getHeight()/2 - (selectBox.HEIGHT/2)));
		
		dialog.pack();
		dialog.setVisible(true);
		
		if(selectBox.shouldSave())
		{
			int[] selected = selectBox.getSelected();
			
			if(selected.length > 0)
			{
				ElapsedTimer[] times = ElapsedTimer.splitTime(m_timer, selected.length);
				
				//save each of the times
				m_log.info("Saving " + selected.length + " entries");
				for(int count = 0; count < selected.length; count ++)
				{
					result.add(new Log(m_actManage.getActivity(selected[count]),times[count],selectBox.getDescription()));
				}
			}
			else
			{
				m_trayIcon.displayMessage(PROGRAM_NAME, "No Activity Selected", MessageType.WARNING);
			}
		}
		
		return result;
	}
	
	private void countdownCompletePrompt(double time){
		
		int height = 300;
		int width = 500;
		int timeFormat = TimeFormatter.guessBestFormat((long)time, TimeFormatter.MILLISECONDS);
		
		//open a dialog to set the amount of time that has passed
		CountdownComplete completeBox = new CountdownComplete();
		completeBox.setup(TimeFormatter.format((long)time, TimeFormatter.MILLISECONDS, timeFormat) + " " + TimeFormatter.toString(timeFormat));
		
		JDialog dialog = new JDialog(null,"Countdown Complete!",ModalityType.APPLICATION_MODAL);
		dialog.setIconImage(new ImageIcon("resources/timer-small.png").getImage());
		dialog.setSize(width, height);
		
		Container contentPane = dialog.getContentPane();
		contentPane.setSize(width,height);
		contentPane.add(completeBox);
		
		//open in the middle of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(screenSize.getWidth()/2 - (width/2)), (int)(screenSize.getHeight()/2 - (height/2)));
		
		dialog.pack();
		dialog.setVisible(true);
	}
	
	private void startCountdown(int amount,int format){
		m_countdown.setAmount(amount, format);
		m_countdown.start();
		
		m_trayIcon.displayMessage(PROGRAM_NAME, "Countdown Started", MessageType.INFO);
	}
	
	private void toggleTimer(){
		//turn off the countdown timer
		if(m_countdown.getState() == TimerState.RUNNING)
		{
			m_countdown.stop();
		}
		else if(m_timer.getState() != TimerState.RUNNING)
		{
			//start the timer
			m_timer.start();
			m_trayIcon.displayMessage(PROGRAM_NAME, "Timer started", MessageType.INFO);
			
			m_trayIcon.setImage(new ImageIcon("resources/timer-running-small.png").getImage());
			m_trayIcon.setToolTip(PROGRAM_NAME + " - Running");
		}
		else
		{
			//stop the timer
			m_timer.stop();
			
			//ask the user for the activity
			List<Log> activity = this.activityPrompt();
			
			if(!activity.isEmpty())
			{
				m_actManage.saveEntries(activity);
				
				String message = String.format("%s Saved", activity.get(0).getActivity());
				
				if(activity.size() > 1)
				{
					//we have multiple
					message = String.format("Saved to %d Activities", activity.size());
				}
				
				m_trayIcon.displayMessage(PROGRAM_NAME, message, MessageType.INFO);
			}
			
			m_timer.reset();
			
			m_trayIcon.setImage(new ImageIcon("resources/timer-small.png").getImage());
			m_trayIcon.setToolTip(PROGRAM_NAME);
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
        m_isRunning = new MenuItem("Start Timer");
        
        Menu reportMenu = new Menu("Reports");
        MenuItem normalReport = new MenuItem("All Logs Report");
        MenuItem groupReport = new MenuItem("Grouped Logs Report");
        reportMenu.add(normalReport);
        reportMenu.add(groupReport);
        
        Menu countdownMenu = new Menu("Countdown");
        MenuItem fifteenMinCountdown = new MenuItem("15 Minutes");
        MenuItem thirtyMinCountdown = new MenuItem("30 Minutes");
        MenuItem sixtyMinCountdown = new MenuItem("60 Minutes");
        MenuItem customCountdown = new MenuItem("Custom");
        countdownMenu.add(fifteenMinCountdown);
        countdownMenu.add(thirtyMinCountdown);
        countdownMenu.add(sixtyMinCountdown);
        countdownMenu.add(customCountdown);
        
        Menu settingsMenu = new Menu("Settings");
        MenuItem activitiesItem = new MenuItem("Activities");
        settingsMenu.add(activitiesItem);
        
        MenuItem exitItem = new MenuItem("Exit");
        
        //add the menu items to the popup menu
        popup.add(m_isRunning);
        popup.addSeparator();
        popup.add(reportMenu);
        popup.add(countdownMenu);
        popup.add(settingsMenu);
        popup.add(exitItem);
        
        //add menu to tray
        m_trayIcon.setPopupMenu(popup);
        m_trayIcon.addMouseListener(new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent e) {
				
				//check if this is a right click event
				if(SwingUtilities.isRightMouseButton(e))
				{
					if(m_timer.getState() == TimerState.RUNNING)
					{
						m_isRunning.setLabel("Running: " + m_timer.toString());
					}
					else if(m_countdown.getState() == TimerState.RUNNING)
					{
						m_isRunning.setLabel("Remaining: " + m_countdown.toString());
					}
					else
					{
						m_isRunning.setLabel("Start Timer");
					}
				}
				
				//double click to start/stop quickly
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
				{
					toggleTimer();
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
        
        fifteenMinCountdown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startCountdown(15, TimeFormatter.MINUTES);
			}
        	
        });
        
        thirtyMinCountdown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startCountdown(30, TimeFormatter.MINUTES);
			}
        	
        });
        
        sixtyMinCountdown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startCountdown(60, TimeFormatter.MINUTES);
			}
        	
        });
        
        customCountdown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int height = 300;
				int width = 500;
				
				CustomCountdownDialog customBox = new CustomCountdownDialog();
				customBox.setup();
				
				JDialog dialog = new JDialog(null,"Set Countdown Timer",ModalityType.APPLICATION_MODAL);
				dialog.setIconImage(new ImageIcon("resources/timer-small.png").getImage());
				dialog.setSize(width, height);
				
				Container contentPane = dialog.getContentPane();
				contentPane.setSize(width,height);
				contentPane.add(customBox);
				
				//open in the middle of the screen
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				dialog.setLocation((int)(screenSize.getWidth()/2 - (width/2)), (int)(screenSize.getHeight()/2 - (height/2)));
				
				dialog.pack();
				dialog.setVisible(true);
				
				if(customBox.shouldSave())
				{
					startCountdown(customBox.getTime(),customBox.getFormat());
				}
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
                tray.remove(m_trayIcon);
                System.exit(0);
            }
        });
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		countdownCompletePrompt(Double.parseDouble(arg1.toString()));
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
