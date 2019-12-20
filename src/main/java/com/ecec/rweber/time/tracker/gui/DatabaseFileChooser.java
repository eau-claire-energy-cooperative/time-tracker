package com.ecec.rweber.time.tracker.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class DatabaseFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public DatabaseFileChooser(File startingLoc) {
		super(startingLoc);
		
		this.setSelectedFile(startingLoc);
		this.setMultiSelectionEnabled(false);
		this.setApproveButtonText("Choose");
		
		//filter on SQLite files
		this.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				boolean result = f.isDirectory(); //always show directories
				
				//check for .db extension
				if(f.getAbsolutePath().contains("."))
				{
					String ext = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("."));
					result = ext.toLowerCase().equals(".db");
				}
				
				return result;
			}

			@Override
			public String getDescription() {
				return "*.db - SQLite Database File";
			}
			
		});
	}
	
	@Override
	protected JDialog createDialog(Component parent) {
		JDialog dialog = super.createDialog(parent);
		
		dialog.setIconImage(TrayService.PROGRAM_ICON.getImage());
		
		return dialog;
	}
}
