package io.warsaw.ical2gcal

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import spock.lang.Specification

class BaseSpecification extends Specification {

    def getResource(filename) {
        return getClass().getClassLoader().getResource(filename)
    }

    def dateWithTimeZone(dateString) {
        Date.parse('yyyy-MM-dd HH:mm:ss z', dateString)
    }

    def createGCal() {
        def config = new ConfigSlurper().parse(getResource('test_config.groovy'))
        return new GoogleCalendar(
                config.google.serviceId,
                config.google.keyFile,
                config.google.calendarId
        )
    }

    def createEventWithSourceId(id) {
        def extendedProperties = new Event.ExtendedProperties()
        extendedProperties.setShared([sourceId: id.toString()]) // casting needed for gCal API
        new Event(
                summary: 'Cool meetup',
                description: 'You got to be there',
                start: new EventDateTime(dateTime: new DateTime(new Date())),
                end: new EventDateTime(dateTime: new DateTime(new Date())),
                extendedProperties: extendedProperties
        )
    }


    def createICal(fileName) {
        return new ICal(getResource(fileName))
    }

    // !!! Warning: clearing google calendar before test
    // Also google keeps all deleted events which may increase time of loading event list
    // and finally slow tests down
    // see comments in GoogleCalendar class
    def clearGCal(gCal) {
        gCal.events().each {
            gCal.delete(it)
        }
    }
}
