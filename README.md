# Time Tracker

This is a very basic time tracking utility with a reporting option. The need for this arose when I needed an easy way to report time to different organizations, or different projects. My requirements were that it should be with the least amount of effort as possible, and record time in a reportable format. 

This program achieves both those issues. It runs as a Windows task bar item. Double-click to start a timer. Double-click again to stop the timer and you'll see a small prompt to choose the activity you just timed. These are set up ahead of time from the Settings->Edit Activities menu. This is then logged to the database. 

## Compiling

Building the project with Maven will compile the code and create a zip file in the ```target/``` directory that contains a ready to run version. 

### Launch4j

You can run the program directly by launching the jar file, however there is also a [Launch4j](http://launch4j.sourceforge.net/) settings file that can be used to wrap it in a windows EXE. 

## Using the Program

Pretty simple operation, you can either double-click the icon or right click and select "Start Timer" to start the timer. When done another double-click or right-click and select the "Running" menu item, to stop the timer. Additionally a right-click will show the currently elapsed time. The program icon will change state from orange to red when the timer is running as a visual indicator. Once the timer is stopped you will get a prompt for where to log to the activity. 

Reporting is done by right-clicking the tray icon and selecting reports. From here you can choose date options and run reports from your logged history. Exporting to CSV is available. 

Additionally a Countdown timer is available. This simply counts down a specified number of minutes and alerts you when time is up. No logging available here, just another handy timer function. 

### Splitting Time

When saving your log you can select the split time box to split the total time among more than one activity. When you do this you'll see a second prompt with every activity displayed. Ctl-click (or shift click) to select as many activities as you need. The total time will be split and logged equally to each one. 

### Reporting

There are two types of reports, the All Logs Report and the Grouped Logs Report. The All Logs Report lists every log entry for a specific date period (a month by default). You can edit these to adjust the start date, end date, and description by clicking the row/column you wish to edit. 

The Grouped Logs Report will group all log entries by activity type and sum the total time spent. 

## Settings

### Edit Activities

The Activities window allows you to edit the various activities that show up in the time entry dialog. Editing or deleting entries will not affect already saved items as they are not linked directly to this list. 

Using the __Add Row__ button will add a row to the table. To delete a row, select it and right-click to bring up the __Delete Row__ option. Make sure to clicke __Save__ to save any changes and exit this window. 

### Setting Database Path

By default the program looks for the database file ```resources/activities.db```. You can set a custom location by somewhere else through the file chooser. This path is saved and will get read on startup. If there is no file in this location a blank database file will be created. 

Since the database is closed after each transaction it is OK to switch DB paths without a program restart. 