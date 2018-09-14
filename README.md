# Time Tracker

This is a very basic time tracking utility with a reporting option. The need for this arose when I needed an easy way to report time to different organizations, or different projects. My requirements were that it should be with as least amount of effort as possible, and record time in a reportable format. 

This program achieves both those issues. It runs as a Windows task bar item Right click to start a timer. Right-click again to stop the timer and you'll see a small prompt to choose the activity you just timed. These are set up ahead of time from the Activities menu. This is then logged to the database. 

Reporting is done by right-clicking the tray icon and selecting reports. From here you can choose date options and run reports from your logged history. Exporting to CSV is available. 

### Splitting Time

When saving your log you can select the split time box to split the total time among more than one activity. When you do this you'll see a second prompt with every activity displayed. Ctl-click (or shift click) to select as many activities as you need. The total time will be split and logged equally to each one. 

### Custom DB Path 

By default the program looks for the ```resources/activities.db``` file. You can set a custom location by creating a ```resources/db.conf``` file. This will get read on startup. The file should contain the full path to the db file you wish to load. 