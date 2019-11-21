package com.ecec.rweber.time.tracker.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TableSorter extends TableRowSorter<TableModel>{

	//helper methods to generate a simple one-column sort order
	public static List<RowSorter.SortKey> generateSortOrder(int col){
		return TableSorter.generateSortOrder(col, SortOrder.ASCENDING);
	}
	
	public static List<RowSorter.SortKey> generateSortOrder(int col, SortOrder order){
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		 
		//sort by the col and order given
		sortKeys.add(new RowSorter.SortKey(col, order));
		
		return sortKeys;
	}
	
	public TableSorter(TableModel model) {
		super(model);
	}
	
	public TableSorter(TableModel model, List<RowSorter.SortKey> defaultSort) {
		this(model);
		
		this.setSortKeys(defaultSort);
	}
	
	public TableSorter(TableModel model, List<RowSorter.SortKey> defaultSort, int[] nonSortable) {
		this(model,defaultSort);
		
		//these columns are non-sortable
		for(int count = 0; count < nonSortable.length; count ++)
		{
			this.setSortable(nonSortable[count], false);
		}
	}
}
