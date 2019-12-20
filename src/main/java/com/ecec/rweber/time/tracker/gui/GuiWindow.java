package com.ecec.rweber.time.tracker.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.ecec.rweber.time.tracker.ActivityManager;
import com.ecec.rweber.time.tracker.util.Notifier;

@SuppressWarnings("serial")
public abstract class GuiWindow extends JFrame{
	private List<Notifier> m_observers = null;
	
	protected int HEIGHT = 300;
	protected int WIDTH = 500;
	protected Logger g_log = null;
	protected ActivityManager g_manage = null;
	
	public GuiWindow(String windowName,ActivityManager manage){
		g_log = Logger.getLogger(this.getClass());
		m_observers = new ArrayList<Notifier>();
		
		//set some default things
		this.setTitle(windowName);
		this.setIconImage(TrayService.PROGRAM_ICON.getImage());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		g_manage = manage;
	}
	
	protected abstract void setupInformation();
	
	protected abstract void viewModal(Container contentPane);
	
	protected void sendNotification(String message, MessageType level){
		Iterator<Notifier> iter = m_observers.iterator();
		
		while(iter.hasNext())
		{
			iter.next().onMessage(message,level);
		}
	}
	
	public void run(){
		//setup any information
		this.setupInformation();
		
		this.setSize(WIDTH,HEIGHT);
		
		//add things to the view modal
		Container contentPane = this.getContentPane();
		contentPane.setSize(WIDTH,HEIGHT);
		this.viewModal(contentPane);
		
		//open in the middle of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.getWidth()/2 - (WIDTH/2)), (int)(screenSize.getHeight()/2 - (HEIGHT/2)));
		this.setVisible(true);
	}
	
	public void addNotifier(Notifier n){
		this.m_observers.add(n);
	}
}
