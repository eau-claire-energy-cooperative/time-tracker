# Time Tracker

This is a very basic time tracking utility with a reporting option. The need for this arose when I needed an easy way to report time to different organizations, or different projects. My requirements were that it should be with the least amount of effort as possible, and record time in a reportable format. 

This program achieves both those issues. It runs as a Windows task bar item. Double-click to start a timer. Double-click again to stop the timer and you'll see a small prompt to choose the activity you just timed. These are set up ahead of time from the Settings->Activities menu. This is then logged to the database. 

## Compiling

Building the project with Maven will compile the code and create a zip file in the ```target/``` directory that contains a ready to run version. 

### Launch4j

You can run the program directly by launching the jar file, however there is also a [Launch4j](http://launch4j.sourceforge.net/) settings file that can be used to wrap it in a windows EXE. 

## Using the Program

Pretty simple operation, you can either double-click the icon or right click and select "Start Timer" to start the timer. When done another double-click or right-click and select the "Running" menu item, to stop the timer. Additionally a right-click will show the currently elapsed time. Once the timer is stopped you will get a prompt for where to log to the activity. 

Reporting is done by right-clicking the tray icon and selecting reports. From here you can choose date options and run reports from your logged history. Exporting to CSV is available. 

Additionally a Countdown timer is available. This simply counts down a specified number of minutes and alerts you when time is up. No logging available here, just another handy timer function. 

### Splitting Time

When saving your log you can select the split time box to split the total time among more than one activity. When you do this you'll see a second prompt with every activity displayed. Ctl-click (or shift click) to select as many activities as you need. The total time will be split and logged equally to each one. 

### Reporting

There are two types of reports, the All Logs Report and the Grouped Logs Report. The All Logs Report lists every log entry for a specific date period (a month by default). You can edit these to adjust the start date, end date, and description. 

The Grouped Logs Report will group all log entries by activity type and sum the total time spent. 

### Custom DB Path 

By default the program looks for the ```resources/activities.db``` file. You can set a custom location by modifying ```resources/db.conf``` file. This will get read on startup. The file should contain the full path to the db file you wish to load. If there is no file in this location a blank database file will be created in this location when the program starts. 