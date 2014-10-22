package io.warsaw.ical2gcal

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime

class SynchronizationSpec extends BaseSpecification {

    public static final CALENDAR_PREFIX = 'meetup'
    def gCal
    def synchronization

    def setup() {
        gCal = createGCal()

        clearGCal(gCal)
        synchronization = new Synchronization(
                createICal('meetup_jug.ics'),
                gCal,
                CALENDAR_PREFIX)
    }



    def 'should copy events from iCal to gCal'() {
        when:
        synchronization.synchronize()

        then:
        gCal.events().size() == 3
    }

    def 'should remove events that are not longer in the calendar'() {
        given:
        // let's add one event from same calendar
        gCal.insert(createEventWithSourceId("${CALENDAR_PREFIX}|1234467"))

        when:
        synchronization.synchronize()

        then:
        // should leave event from other calendar and add 3 new events
        gCal.events().size() == 3
    }

    def 'should update events with same source id'() {
        given:
        // let's add one event with same id as one in iCal feed
        def originalEvent = createEventWithSourceId("${CALENDAR_PREFIX}|event_209417392@meetup.com")
        gCal.insert(originalEvent)
        originalEvent.summary = 'Cool meetup original summary'
        def eventGCalId = gCal.events()[0].id

        when:
        synchronization.synchronize()

        then:
        def events = gCal.events()
        def sameEvent = events.find { it.id == eventGCalId }
        events.size() == 3
        sameEvent.summary == 'GeeCON Prague conference ticket raffle'
    }

    def 'should not touch events that were not synchronized from this iCal'() {
        given:
        // let's add one event from other calendar
        gCal.insert(createEventWithSourceId('otherMeetup|1234467'))

        when:
        synchronization.synchronize()

        then:
        // should leave event from other calendar and add 3 new events
        gCal.events().size() == 4
    }

    def 'should synchronize future events'(){
        given:
        def originalEvent = createEventWithSourceId("${CALENDAR_PREFIX}|1234@meetup.com")
        def start = dateWithTimeZone('2014-01-30 16:00:00 CET')
        def end = dateWithTimeZone('2014-01-30 18:00:00 CET')
        originalEvent.start = new EventDateTime(dateTime: new DateTime(start))
        originalEvent.end = new EventDateTime(dateTime: new DateTime(end))
        gCal.insert(originalEvent)

        when:
        synchronization.synchronizeFutureEvents()

        then:
        // should previous events intact
        gCal.events().size() == 4

    }

}
