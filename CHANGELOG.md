# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## 1.8.3

### Added

- start/stop the timer on a double left-click of the mouse on the icon, just a quicker way to access

## 1.8.2

### Added

- TableSorter class to allow reports to specify default sorts and columns that can be custom sorted
- Use CellRenderers to customize look of data, especially for Date types, helps with sorting

## 1.8.1

### Added

- tray-running.png and tray-running-small.png
- change color of the tray icon when the timer is running as a visual indicator
- added total time spent at the bottom of reporting screen

## 1.8.0

### Changed

- updated min JVM version

## 1.5.0

### Changed 

- closed database after all transactions so that the SQLite DB is not left open
- implicitly close DB when program exits

## 1.4.3

### Changed

- swapped locations of save/cancel to make them more intuative

## 1.4.2

### Added

- throw an error if DB file cannot be found, this will show a user pop-up as well
- added a cancel button to the activity save prompt. This could always be done with the X but this makes it specific

## 1.4.1

### Added

- added GPLv3 license

### Changed

- updated README with more information
- changed "Not Running" to "Start" so users can tell exactly how to start a timer

## 1.4.0

### Added

- added a Maven assembly file to build deployable zip upon build

### Changed

- converted to Maven project
- changed source structure to match Maven layout

### Removed

- removed ant build file
