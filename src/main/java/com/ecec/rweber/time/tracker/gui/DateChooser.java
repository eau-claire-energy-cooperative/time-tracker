package com.ecec.rweber.time.tracker.gui;

/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2002 Alexandre Pereira Calsavara
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  iolalla@yahoo.com
 *
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;


/**
 * Custom dialog box to enter dates. The <code>DateChooser</code>
 * class presents a calendar and allows the user to visually select a
 * day, month and year so that it is impossible to enter an invalid
 * date.
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 **/
public class DateChooser extends JDialog 
    implements ItemListener, MouseListener, FocusListener, KeyListener, ActionListener{
	private static final long serialVersionUID = 1L;

	/** Names of the months. */
	protected static final String[] MONTHS = 
	  new String[] {
	      "January",
	      "February",
	      "March",
	      "April",
	      "May",
	      "June",
	      "July",
	      "August",
	      "September",
	      "October",
	      "November",
	      "December"
	  };

    /** Names of the days of the week. */
    protected static final String[] DAYS =
	  new String[] {
	      "Sun",
	      "Mon",
	      "Tue",
	      "Wed",
	      "Thu",
	      "Fri",
	      "Sat"
	  };
    
    /** Hours of the day */
    protected static final Integer[] HOURS = 
    	new Integer[] {
    		1,2,3,4,5,6,7,8,9,10,11,12
    };
    
    /** Minutes in the an hour */
    protected static final Integer[] MINUTES = 
    	new Integer[] {
    		0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59
    };

    /** Text color of the days of the weeks, used as column headers in
        the calendar. */
    private static final Color WEEK_DAYS_FOREGROUND = Color.black;

    /** Text color of the days' numbers in the calendar. */
    private static final Color DAYS_FOREGROUND = Color.black;

    /** Background color of the selected day in the calendar. */
    private static final Color SELECTED_DAY_FOREGROUND = Color.white;

    /** Text color of the selected day in the calendar. */
    private static final Color SELECTED_DAY_BACKGROUND = new Color(237,126,14);

    /** Empty border, used when the calendar does not have the focus. */
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1,1,1,1);

    /** Border used to highlight the selected day when the calendar
        has the focus. */
    private static final Border FOCUSED_BORDER = BorderFactory.createLineBorder(Color.yellow,1);

    /** First year that can be selected. */
    private static final int FIRST_YEAR = 2014;

    /** Last year that can be selected. */
    private static int LAST_YEAR = 2050;

    /** Auxiliary variable to compute dates. */
    private GregorianCalendar calendar;

    /** Calendar, as a matrix of labels. The first row represents the
        first week of the month, the second row, the second week, and
        so on. Each column represents a day of the week, the first is
        Sunday, and the last is Saturday. The label's text is the
        number of the corresponding day. */
    private JLabel[][] days;

    /** Day selection control. It is just a panel that can receive the
        focus. The actual user interaction is driven by the
        <code>DateChooser</code> class. */
    private FocusablePanel daysGrid;

    /** Month selection control. */
    private JComboBox<String> month;

    /** Year selection control. */
    private JComboBox<String> year;

    /** hours selection control. */
    private JComboBox<Integer> hour;
    
    /** minutes selection control. */
    private JComboBox<Integer> minutes;
    
    /** AM/PM selection control. */
    private JComboBox<String> am_pm;
    
    /** "Ok" button. */
    private JButton ok;

    /** "Cancel" button. */
    private JButton cancel;

    /** Day of the week (0=Sunday) corresponding to the first day of
        the selected month. Used to calculate the position, in the
        calendar ({@link #days}), corresponding to a given day. */
    private int offset;

    /** Last day of the selected month. */
    private int lastDay;

    /** Selected day. */
    private JLabel day;

    /** <code>true</code> if the "Ok" button was clicked to close the
        dialog box, <code>false</code> otherwise. */
    private boolean okClicked;

    /** if the time editor should be shown, defaults to false */
    private boolean showTime = false;

    /**
     * Custom panel that can receive the focus. Used to implement the
     * calendar control.
     **/
    private static class FocusablePanel extends JPanel
    {
		private static final long serialVersionUID = 1077688936855478805L;


	/**
	   * Constructs a new <code>FocusablePanel</code> with the given
	   * layout manager.
	   *
	   * @param layout layout manager
	   **/
	  public FocusablePanel( LayoutManager layout ) {
	      super( layout );
	  }


	  /**
	   * Always returns <code>true</code>, since
	   * <code>FocusablePanel</code> can receive the focus.
	   *
	   * @return <code>true</code>
	   **/
	  public boolean isFocusTraversable() {
	      return true;
	  }
    }



    /**
     * Initializes this <code>DateChooser</code> object. Creates the
     * controls, registers listeners and initializes the dialog box.
     **/
    private void construct()
    {
    	this.setIconImage(new ImageIcon("resources/timer-small.png").getImage());
	  calendar = new GregorianCalendar();
	
	  month = new JComboBox<String>(MONTHS);
	  month.addItemListener( this );
	
	  //figure out the first and last years
	  Calendar c = new GregorianCalendar();
	  LAST_YEAR = c.get(Calendar.YEAR);
	 
	  year = new JComboBox<String>();
	  for ( int i=FIRST_YEAR; i<=LAST_YEAR; i++ )
	      year.addItem( Integer.toString(i) );
	  year.addItemListener( this );
	
	  days = new JLabel[7][7];
	  for ( int i=0; i<7; i++ ) {
	      days[0][i] = new JLabel(DAYS[i],JLabel.RIGHT);
	      days[0][i].setForeground( WEEK_DAYS_FOREGROUND );
	  }
	  for ( int i=1; i<7; i++ )
	      for ( int j=0; j<7; j++ )
	    {
	        days[i][j] = new JLabel(" ",JLabel.RIGHT);
	        days[i][j].setForeground( DAYS_FOREGROUND );
	        days[i][j].setBackground( SELECTED_DAY_BACKGROUND );
	        days[i][j].setBorder( EMPTY_BORDER );
	        days[i][j].addMouseListener( this );
	    }
	
	  ok = new JButton("Ok");
	  ok.addActionListener( this );
	  cancel = new JButton("Cancel");
	  cancel.addActionListener( this );
	
	  JPanel monthYear = new JPanel();
	  monthYear.add( month );
	  monthYear.add( year );
	
	  daysGrid = new FocusablePanel(new GridLayout(7,7,5,0));
	  daysGrid.addFocusListener( this );
	  daysGrid.addKeyListener( this );
	  for ( int i=0; i<7; i++ )
	      for ( int j=0; j<7; j++ )
	    daysGrid.add( days[i][j] );
	  daysGrid.setBackground( Color.white );
	  daysGrid.setBorder( BorderFactory.createLoweredBevelBorder() );
	  
	  JPanel daysPanel = new JPanel();
	  daysPanel.setLayout(new BoxLayout(daysPanel,BoxLayout.Y_AXIS));
	  daysPanel.add(daysGrid );

	  JLabel l_time = new JLabel("Time: ");
	  
	  hour = new JComboBox<Integer>(HOURS);
	  minutes = new JComboBox<Integer>(MINUTES);
	  am_pm = new JComboBox<String>(new String[] {"AM","PM"});

	  if(showTime)
	  {
		  JPanel timePanel = new JPanel();
		  timePanel.add(l_time);
		  timePanel.add(hour);
		  timePanel.add(minutes);
		  timePanel.add(am_pm);
		  
		  daysPanel.add(timePanel);
	  }
	  
	  JPanel buttons = new JPanel();
	  buttons.add( ok );
	  buttons.add( cancel );
	
	  Container dialog = getContentPane();
	  dialog.add( "North", monthYear );
	  dialog.add( "Center", daysPanel );
	  dialog.add( "South", buttons );
	
	  pack();
	  setResizable( false );
    }



    /**
     * Gets the selected day, as an <code>int</code>. Parses the text
     * of the selected label in the calendar to get the day.
     *
     * @return the selected day or -1 if there is no day selected
     **/
    private int getSelectedDay()
    {
	  if ( day == null )
	      return -1 ;
	  try {
	      return Integer.parseInt(day.getText());
	  } catch ( NumberFormatException e ) {
	  }
	  return -1;
    }



    /**
     * Sets the selected day. The day is specified as the label
     * control, in the calendar, corresponding to the day to select.
     *
     * @param newDay day to select
     **/
    private void setSelected( JLabel newDay )
    {
	  if ( day != null ) {
	      day.setForeground( DAYS_FOREGROUND );
	      day.setOpaque( false );
	      day.setBorder( EMPTY_BORDER );
	  }
	  day = newDay;
	  day.setForeground( SELECTED_DAY_FOREGROUND );
	  day.setOpaque( true );
	  if ( daysGrid.hasFocus() )
	      day.setBorder( FOCUSED_BORDER );
    }



    /**
     * Sets the selected day. The day is specified as the number of
     * the day, in the month, to selected. The function compute the
     * corresponding control to select.
     *
     * @param newDay day to select
     **/
    private void setSelected( int newDay )
    {
    	setSelected( days[(newDay+offset-1)/7+1][(newDay+offset-1)%7] );
    }



    /**
     * Updates the calendar. This function updates the calendar panel
     * to reflect the month and year selected. It keeps the same day
     * of the month that was selected, except if it is beyond the last
     * day of the month. In this case, the last day of the month is
     * selected.
     **/
    private void update()
    {
	  int iday = getSelectedDay();
	  for ( int i=0; i<7; i++ ) {
	      days[1][i].setText( " " );
	      days[5][i].setText( " " );
	      days[6][i].setText( " " );
	  }
	  calendar.set( Calendar.DATE, 1 );
	  calendar.set( Calendar.MONTH, month.getSelectedIndex()+Calendar.JANUARY );
	  calendar.set( Calendar.YEAR, year.getSelectedIndex()+FIRST_YEAR );
	
	  offset = calendar.get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY;
	  lastDay = calendar.getActualMaximum(Calendar.DATE);
	  for ( int i=0; i<lastDay; i++ )
	      days[(i+offset)/7+1][(i+offset)%7].setText( String.valueOf(i+1) );
	  if ( iday != -1 ) {
	      if ( iday > lastDay )
	    iday = lastDay;
	      setSelected( iday );
	  }
    }



    /**
     * Called when the "Ok" button is pressed. Just sets a flag and
     * hides the dialog box.
     **/
    public void actionPerformed( ActionEvent e ) {
	  if ( e.getSource() == ok )
	      okClicked = true;
	  this.setVisible(false);
    }

    /**
     * Called when the calendar gains the focus. Just re-sets the
     * selected day so that it is redrawn with the border that
     * indicate focus.
     **/
    public void focusGained( FocusEvent e ) {
    	setSelected( day );
    }

    /**
     * Called when the calendar loses the focus. Just re-sets the
     * selected day so that it is redrawn without the border that
     * indicate focus.
     **/
    public void focusLost( FocusEvent e ) {
    	setSelected( day );
    }

    /**
     * Called when a new month or year is selected. Updates the calendar
     * to reflect the selection.
     **/
    public void itemStateChanged( ItemEvent e ) {
    	update();
    }

    /**
     * Called when a key is pressed and the calendar has the
     * focus. Handles the arrow keys so that the user can select a day
     * using the keyboard.
     **/
    public void keyPressed( KeyEvent e ) {
	  int iday = getSelectedDay();
	  switch ( e.getKeyCode() ) {
	  case KeyEvent.VK_LEFT:
	      if ( iday > 1 )
	    setSelected( iday-1 );
	      break;
	  case KeyEvent.VK_RIGHT:
	      if ( iday < lastDay )
	    setSelected( iday+1 );
	      break;
	  case KeyEvent.VK_UP:
	      if ( iday > 7 )
	    setSelected( iday-7 );
	      break;
	  case KeyEvent.VK_DOWN:
	      if ( iday <= lastDay-7 )
	    setSelected( iday+7 );
	      break;
	  }
    }

    /**
     * Called when the mouse is clicked on a day in the
     * calendar. Selects the clicked day.
     **/
    public void mouseClicked( MouseEvent e ) {
	  JLabel day = (JLabel)e.getSource();
	  if ( !day.getText().equals(" ") )
	      setSelected( day );
	  daysGrid.requestFocus();
    }

    public void keyReleased( KeyEvent e ) {}
    public void keyTyped( KeyEvent e ) {}
    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e) {}
    public void mousePressed( MouseEvent e ) {}
    public void mouseReleased( MouseEvent e) {}



    /**
     * Constructs a new <code>DateChooser</code> with the given title.
     *
     * @param owner owner dialog
     *
     * @param title dialog title
     * 
     * @param showTime if the time editing dialog should be shown
     **/
    public DateChooser( Dialog owner, String title, boolean showTime )
    {
	  super( owner, title, true );
	  this.showTime = showTime;
	  construct();
    }
    
    /**
     * Constructs a new <code>DateChooser</code> with the given title.
     *
     * @param owner owner dialog
     *
     * @param title dialog title
     **/
    public DateChooser( Dialog owner, String title )
    {
	  super( owner, title, true );
	  construct();
    }


    /**
     * Constructs a new <code>DateChooser</code>.
     *
     * @param owner owner dialog
     **/
    public DateChooser( Dialog owner )
    {
	  super( owner, true );
	  construct();
    }


    /**
     * Constructs a new <code>DateChooser</code> with the given title.
     *
     * @param owner owner frame
     *
     * @param title dialog title
     * 
     * @param showTime if the time editor should be shown
     **/
    public DateChooser( Frame owner, String title, boolean showTime )
    {
	  super( owner, title, true );
	  this.showTime = showTime;
	  construct();
    }

    /**
     * Constructs a new <code>DateChooser</code> with the given title.
     *
     * @param owner owner frame
     *
     * @param title dialog title
     **/
    public DateChooser( Frame owner, String title )
    {
	  super( owner, title, true );
	  construct();
    }



    /**
     * Constructs a new <code>DateChooser</code>.
     *
     * @param owner owner frame
     **/
    public DateChooser( Frame owner )
    {
	  super( owner, true );
	  construct();
    }
    

    /**
     * Selects a date. Displays the dialog box, with a given date as
     * the selected date, and allows the user select a new date.
     *
     * @param date initial date
     *
     * @return the new date selected or <code>null</code> if the user
     * press "Cancel" or closes the dialog box
     **/
    public Date select( Date date )
    {
		  calendar.setTime( date );
		  int _day = calendar.get(Calendar.DATE);
		  int _month = calendar.get(Calendar.MONTH);
		  int _year = calendar.get(Calendar.YEAR);
		  int _hour = calendar.get(Calendar.HOUR);
		  
		  if(_hour == 0)
		  {
			  _hour = 12;
		  }
		  
		  year.setSelectedIndex( _year-FIRST_YEAR );
		  month.setSelectedIndex( _month-Calendar.JANUARY );
		  hour.setSelectedIndex(_hour - 1);
		  minutes.setSelectedIndex(calendar.get(Calendar.MINUTE));
		  am_pm.setSelectedIndex(calendar.get(Calendar.AM_PM));
		  
		  setSelected( _day );
		  okClicked = false;
		  this.setVisible(true);
		  if ( !okClicked )
		      return null;
		  calendar.set( Calendar.DATE, getSelectedDay() );
		  calendar.set( Calendar.MONTH, month.getSelectedIndex()+Calendar.JANUARY );
		  calendar.set( Calendar.YEAR, year.getSelectedIndex()+FIRST_YEAR );
		  
		  if(this.showTime)
		  {
			  _hour = hour.getSelectedIndex() + 1; //add one as is 0 indexed
			  if(_hour == 12)
			  {
				  _hour = 0; //0 is 12
			  }
			  
			  calendar.set(Calendar.HOUR, _hour);
			  calendar.set(Calendar.MINUTE, minutes.getSelectedIndex()); //starts with 0 and 0 indexed
			  calendar.set(Calendar.AM_PM, am_pm.getSelectedIndex());
		  }
		  else
		  {
			  calendar.set(Calendar.HOUR_OF_DAY, 0);
			  calendar.set(Calendar.MINUTE, 0);
		  }
		  
		  return calendar.getTime();
    }



    /**
     * Selects new date. Just calls {@link #select(Date)} with the
     * system date as the parameter.
     *
     * @return the same as the function {@link #select(Date)}
     **/
    public Date select()
    {
    	return select(new Date());
    }
}