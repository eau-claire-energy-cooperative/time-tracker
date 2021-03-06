package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.ecec.rweber.time.tracker.Activity;
import com.ecec.rweber.time.tracker.ActivityManager;

public class ActivityTable extends GuiWindow{
	private static final long serialVersionUID = -8178111224516826734L;

	private JTable m_table = null;
	
	public ActivityTable(ActivityManager manager){
		super("Activities",manager);
	}
	
	private void deleteSelectedRow(){
		int row = m_table.getSelectedRow();
		
		if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this?") == JOptionPane.YES_OPTION)
		{
			DefaultTableModel model = (DefaultTableModel)m_table.getModel();
			model.removeRow(row);
		}
	}
	
	@Override
	protected void setupInformation(){
		this.HEIGHT = 400;
		this.WIDTH = 600;
		
		List<Activity> actList = g_manage.getActivities(); 
		DefaultTableModel model = new DefaultTableModel(new String[]{"Activity Name","Description"},actList.size());
		
		Activity act = null;
		for(int count = 0; count < actList.size(); count ++)
		{
			act = actList.get(count);
			
			model.setValueAt(act.getName(), count, 0);
			model.setValueAt(act.getDescription(), count, 1);
		}

		m_table = new JTable(model);
		m_table.setRowSorter(new TableSorter(m_table.getModel(),TableSorter.generateSortOrder(0, SortOrder.ASCENDING)));
		m_table.getTableHeader().setReorderingAllowed(false);
		m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
						
						JPopupMenu popup = new JPopupMenu();
						
						//create the delete popup
						JMenuItem deleteAction = new JMenuItem("Delete Row");
						deleteAction.addActionListener(new ActionListener(){
				
							@Override
							public void actionPerformed(ActionEvent arg0) {
								deleteSelectedRow();
							}
							
						});
						
						popup.add(deleteAction);
						popup.show(event.getComponent(), event.getX(), event.getY());
					}
					else
					{
						m_table.clearSelection();
					}
				}
			}
			
		});
	}
	
	@Override
	public void viewModal(Container layoutPane){
		
		layoutPane.setLayout(new BoxLayout(layoutPane,BoxLayout.Y_AXIS));
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,20)));
		
		JComponent wrapper1= new JPanel();
		wrapper1.setSize(new Dimension(WIDTH,100));
		
		//add some buttons
		JButton b_addRow = new JButton("Add Row");
		b_addRow.setAlignmentX(Component.RIGHT_ALIGNMENT);
		b_addRow.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//add a new row
				DefaultTableModel model = (DefaultTableModel)m_table.getModel();
				model.addRow(new String[]{"New Activity",""});
				
				m_table.editCellAt(m_table.getRowCount() -1, 0);
			}
			
		});
		wrapper1.add(b_addRow);
		
		layoutPane.add(wrapper1);
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,10)));
		
		//add the scroller
		JScrollPane scroller = new JScrollPane(m_table);
		scroller.setAlignmentX(Component.CENTER_ALIGNMENT);
		scroller.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		layoutPane.add(scroller);
		
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,15)));
		
		//add a save button
		JButton b_save = new JButton("Save");
		b_save.setAlignmentX(Component.CENTER_ALIGNMENT);
		b_save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				DefaultTableModel model = (DefaultTableModel)m_table.getModel();
				
				g_manage.setActivities(model.getDataVector());
				
				dispose();
			}
			
		});
		layoutPane.add(b_save);
		
		layoutPane.add(Box.createRigidArea(new Dimension(WIDTH,10)));
	}
}
