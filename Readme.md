ical2gcal
=========

This is a tool to load iCalendar feeds to Google Calendar and synchronize them. As of time of writing Google offers automated synchronization of iCalendar feeds which refreshes periodically. This script was written to allow immediate synchronization.

Script behaviour:

* Reads events from iCalendar feed.
* Creates or update corresponding events in Google Calendar.
* Every iCalendar event is identified in Google Calendar by unique source ID. Source ID consists of unique iCalendar ID (see prefix option) and unique event ID.
* Removes Google Calendar events, that originate from iCalendar feed, but do not exist any more.
* Can be forced to ignore past Google Calendar events. This is useful if source iCalendar feed contains future events only.
* Works for multiple source iCalendar feeds synchronized with single Google Calendar
* Works for iCal feed from file or URL

Prerequisites
=============

* Java (tested with 8.0, but should run on earlier versions)
* Gradle (tested with 2.1)

Building
=============

To build without tests:

    gradle assemble


Usage
=====

    ical2gcal [OPTIONS and ARGUMENTS]

E.g to sync from file

    ical2gcal \
        -f /Users/joe/facebook.ics \
        -k /Users/joe/google-key.p12 \
        -s joeapi@developer.gserviceaccount.com \
        -c my_calendar@group.calendar.google.com \
        -p facebook_birthday \
        -e

E.g to sync from URL

    ical2gcal \
        -i 'http://www.facebook.com/ical/u.php?uid=123456789&key=ABCD1234' \
        -k /Users/joe/google-key.p12 \
        -s joeapi@developer.gserviceaccount.com \
        -c my_calendar@group.calendar.google.com \
        -p facebook_js_devs
        -e

Options and arguments:

    -c,--gcalid <arg>       Google Calendar ID e.g. 123@group.calendar.google.com
    -e,--futureevents       do not touch Google Calendar events from the past, use if iCalendar feed contains future events only
    -f,--icalfile <arg>     iCalendar file eg. /user/joe/meetup.ics
    -i,--icalurl <arg>      iCalendar url, must start with http, https, eg. http://example.com/meetup.ics, use one this or -f
    -k,--gkey <arg>         google p12 key file e.g. /user/joe/google-key.p12
    -p,--prefix <arg>       arbitrary unique prefix to identify iCalendar feed in Google Calendar e.g. meetup
    -s,--gserviceid <arg>   Google service ID e.g. abc@developer.gserviceaccount.com


Getting iCalendar feed URL
==========================

iCalendar feeds usually start with webcal:// protocol prefix. java.net.URL does not handle this protocol. Please replace webcal:// with http://. Tested for Meetup and Facebook feeds.

Getting Google Calendar API credentials
=======================================

1. Get ID of our Google Calendar - see [this to learn how to find it](http://googleappstroubleshootinghelp.blogspot.com/2012/09/how-to-find-calendar-id-of-google.html)
2. Set up Google Calendar API account - create your dev account according to instructions from https://developers.google.com/google-apps/calendar/firstapp?hl=pl#getaccount. You will get service ID and .p12 key.
3. Grant write access to your generated API service ID - see [this](https://support.google.com/calendar/answer/143754?hl=en)

Testing
=======

IMPORTANT: Some integration tests require access to Google Calendar. Tests delete all events before running tests so make sure that this calendar is used only for testing.

To set-up tests copy src/test/resources/test_config.groovy.example to src/test/resources/test_config.groovy and set properties.

Running tests:

    gradle test

Known test issues:

* Google Calendar keeps deleted events forever and uses them to calculate page offset. That may degrade performance of tests over as number of deleted events grows with every test. Please see [this Stack Overflow thread for details](http://stackoverflow.com/questions/18566386/google-calendar-v3-api-events-list-request-return-empty-list).
