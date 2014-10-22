package io.warsaw.ical2gcal

def cli = new CliBuilder(usage: 'Main')
cli.i(longOpt: 'icalurl', args: 1, 'iCalendar url, must start with http, https, eg. http://example.com/meetup.ics, use one this or -f')
cli.f(longOpt: 'icalfile', args: 1, 'iCalendar file eg. /user/joe/meetup.ics')
cli.c(longOpt: 'gcalid', args: 1, required: true, 'Google Calendar ID e.g. 123@group.calendar.google.com')
cli.s(longOpt: 'gserviceid', args: 1, required: true, 'Google service ID e.g. abc@developer.gserviceaccount.com')
cli.k(longOpt: 'gkey', args: 1, required: true, 'google p12 key file e.g. /user/joe/google-key.p12')
cli.p(longOpt: 'prefix', args: 1, required: true, 'arbitrary unique prefix to identify iCalendar feed in Google Calendar e.g. meetup')
cli.e(longOpt: 'futureevents', 'do not touch Google Calendar events from the past, use if iCalendar feed contains future events only')
def options = cli.parse(args)
if (!options) {
    System.exit(1)
}

if (!(options.i || options.f)) {
    println 'missing -i or -f'
    cli.usage()
    System.exit(1)
}

def iCal = new ICal(options.f ? new File(options.f) : new URL(options.i))

def gCal = new GoogleCalendar(options.s, options.k, options.c)

def sychronization = new Synchronization(iCal, gCal, options.p)

if(options.e){
    sychronization.synchronizeFutureEvents()
}else{
    sychronization.synchronize()
}



