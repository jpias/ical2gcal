package io.warsaw.ical2gcal

import biweekly.component.VEvent
import spock.lang.Shared


class EventTranslatorSpec extends BaseSpecification {


    @Shared
    def eventTranslator = new EventTranslator('myCal')

    @Shared
    def iCalEvent = createICalEvent()

    def 'should create unique id for calendar prefix and event'() {
        when:
        def gCalEvent = eventTranslator.iCalEventToGcalEvent(iCalEvent)

        then:
        gCalEvent.extendedProperties.shared.get('sourceId') == 'myCal|event123@example.com'
    }

    def 'should translate properties of iCal event to gCal event'() {
        when:
        def gCalEvent = eventTranslator.iCalEventToGcalEvent(iCalEvent)

        then:
        gCalEvent.summary == 'An awesome event'
        gCalEvent.description == 'Best event ever. See you on Wednesday.'
        gCalEvent.start.dateTime.toStringRfc3339() == '2014-09-30T16:00:00.000+02:00'
        gCalEvent.end.dateTime.toStringRfc3339() == '2014-09-30T18:00:00.000+02:00'
        gCalEvent.location == 'Electric Avenue'
        gCalEvent.source.url == 'http://example.com/myevents'
    }

    def 'should return source id for gCal event if exists'() {
        given:
        def gCalEvent = eventTranslator.iCalEventToGcalEvent(iCalEvent)

        expect:
        eventTranslator.sourceId(gCalEvent) == 'myCal|event123@example.com'
    }

    def 'should determine if gCal event was synchronized from iCal with given id'() {
        given:
        def gCalEvent = eventTranslator.iCalEventToGcalEvent(iCalEvent)
        def otherEventTranslator = new EventTranslator('otherCal')

        expect:
        eventTranslator.isSynchronizedFromThisCalendar(gCalEvent) == true
        otherEventTranslator.isSynchronizedFromThisCalendar(gCalEvent) == false
    }


    def createICalEvent() {
        def event = new VEvent()
        // using plain setters so groovy can pick correct type
        event.setUid('event123@example.com')
        event.setSummary('An awesome event')
        event.setDescription('Best event ever. See you on Wednesday.')
        event.setDateStart(dateWithTimeZone('2014-09-30 16:00:00 CEST'))
        event.setDateEnd(dateWithTimeZone('2014-09-30 18:00:00 CEST'))
        event.setLocation('Electric Avenue')
        event.setUrl('http://example.com/myevents')
        return event
    }


}