ical2gcal
=========

This is a tool to load iCalendar feeds to Google Calendar and synchronize them. As of time of writing Google offers automated synchronization of iCalendar feeds which refreshes periodically. This script was written to allow immediate synchronization.

Script behaviour:

* Reads events from iCalendar feed
* Creates corresponding events in Google Calendar and assigns unique ID to every event. Unique ID consists of unique calendar ID and iCalendar event ID.
* Updates existing Google Calendar events
* Removes Google Calendar events, that originate from iCalendar feed, but do not exist any more.
* Works for future events only (does not change past events)
* Works for many source iCalendar feeds synchronized to single Google Calendar

Assumptions:

* Source feed should contain future events only

Prerequisites
=============

* Java (tested with 8.0, but should run on earlier versions without problems)
* Groovy (tested with 2.3.6)
* Gradle (tested with 2.1)




http://stackoverflow.com/questions/17360719/running-groovy-scripts-from-gradle
