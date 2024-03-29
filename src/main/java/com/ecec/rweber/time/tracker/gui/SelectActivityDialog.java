package com.ecec.rweber.time.tracker.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import com.ecec.rweber.time.tracker.Activity;
import com.ecec.rweber.time.tracker.ActivityManager;

public class SelectActivityDialog extends DialogWindow {
	private static final long serialVersionUID = -8610097326110926200L;
	private ActivityManager m_manage = null;
	private String m_timeString = null;
	private List<Integer> m_selected = null;
	private JCheckBox m_splitTime = null;
	private JComboBox<Activity> m_select = null;
	private JTextArea m_descrip = null;
	
	public SelectActivityDialog(ActivityManager manage, String timeString){
		super();
		m_manage = manage;
		m_timeString = timeString;
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> showMultipleSelect(){
		
		SelectListDialog selectList = new SelectListDialog(m_manage.getActivities(),m_select.getSelectedIndex());
		selectList.setup();
		
		JDialog dialog = new JDialog(null,"Select Activities",ModalityType.APPLICATION_MODAL);
		dialog.setIconImage(new ImageIcon("resources/timer-small.png").getImage());
		dialog.setSize(selectList.WIDTH, selectList.HEIGHT);
		
		Container contentPane = dialog.getContentPane();
		contentPane.setSize(selectList.WIDTH, selectList.HEIGHT);
		contentPane.add(selectList);
		
		//open slightly to the right and higher than this window
		Point p = this.getLocationOnScreen();
		dialog.setLocation((int)(p.getX() + 250), (int)(p.getY() - 250));
		
		dialog.pack();
		dialog.setVisible(true);
		
		return (List<Integer>)selectList.getResults().get("selected");
	}
	
	@Override
	protected void saveClicked(){
		Window win = SwingUtilities.getWindowAncestor(this);
		
		if(win != null) {
			
			if(m_splitTime.isSelected())
			{
				m_selected = this.showMultipleSelect();
			}
			else
			{
				m_selected = new ArrayList<Integer>();
				m_selected.add(m_select.getSelectedIndex());
			}
			
			m_shouldSave = true;
			win.dispose();
		}
	}
	
	@Override
	public void setup(){
		Font f = new Font(Font.SERIF,Font.PLAIN,15);
		this.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel l_date = new JLabel("Time: " + m_timeString);
		l_date.setFont(f);
		
		m_splitTime = new JCheckBox("Split Time");
		m_splitTime.setFont(f);
		
		//create a midpanel to marry these two items for the center
		JPanel midPanel = new JPanel(new BorderLayout(10,15));
		
		midPanel.add(l_date,BorderLayout.LINE_START);
		midPanel.add(m_splitTime,BorderLayout.LINE_END);
		
		//setup activity select box
		m_select = new JComboBox<Activity>(m_manage.getActivities().toArray(new Activity[0]));
		m_select.setFont(f);
		
		JButton b_cancel = new JButton("Cancel");
		b_cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelClicked();
			}
			
		});
		
		JButton b_save = new JButton("Save");
		b_save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveClicked();
			}
			
		});
		
		//create an endpanel to marry these two buttons for the end
		JPanel endPanel = new JPanel(new BorderLayout(10,15));
		
		endPanel.add(b_save,BorderLayout.LINE_START);
		endPanel.add(b_cancel,BorderLayout.LINE_END);
		
		this.add(m_select,BorderLayout.LINE_START);
		this.add(midPanel,BorderLayout.CENTER);
		this.add(endPanel,BorderLayout.LINE_END);
		
		m_descrip = new JTextArea("",4,50);
		m_descrip.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// if ctl+enter is pressed then save the dialog
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					saveClicked();
				}
			}
			
		});
		this.add(m_descrip,BorderLayout.PAGE_END);
		
		this.setSize(HEIGHT, WIDTH);
	}
	
	public String getDescription(){
		return m_descrip.getText();
	}

	@Override
	public Map<String,Object> getResults() {
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("selected", m_selected);
		
		return result;
	}
}
