package io.warsaw.ical2gcal

def cli = new CliBuilder(usage: 'Main')
cli.i(longOpt: 'icalurl', args: 1, 'ical url, must start with http, https, eg. http://example.com/meetup.ics, use one this or -f')
cli.f(longOpt: 'icalfile', args: 1, 'ical file eg. /user/joe/meetup.ics')
cli.c(longOpt: 'gcalid', args: 1, required: true, 'google calendar id e.g. 123@group.calendar.google.com')
cli.s(longOpt: 'gserviceid', args: 1, required: true, 'google service id e.g. abc@developer.gserviceaccount.com')
cli.k(longOpt: 'gkey', args: 1, required: true, 'google p12 key file id e.g. /user/joe/google-key.p12')
cli.p(longOpt: 'prefix', args: 1, required: true, 'arbitrary unique prefix to identify iCal calendar in google calendar e.g. meetup')
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

new Synchronization(iCal, gCal, options.p).synchronize()



