package io.warsaw.ical2gcal

import static spock.util.matcher.HamcrestMatchers.closeTo
import static spock.util.matcher.HamcrestSupport.that

class ICalSpec extends BaseSpecification {


    def 'should read correct number of events'() {
        when:
        def iCal = readMeetupICal()

        then:
        iCal.events.size() == 3
    }

    def 'should read meetup iCal event properties'() {
        when:
        def event = readMeetupICal().events[0]

        then:
        event.uid.value == 'event_209417392@meetup.com'
        event.dateStart.value == dateWithTimeZone('2014-09-30 16:00:00 CEST')
        event.dateEnd.value == dateWithTimeZone('2014-09-30 19:00:00 CEST')
        event.summary.value == 'GeeCON Prague conference ticket raffle'
        // Unfortunately Spock has problems with some multiline conditions so we cannot
        // use Groovy multiline string and we will leverage old-school '+' sign concatenation
        // see. https://code.google.com/p/spock/issues/detail?id=214
        event.description.value == 'Polish Java User Group\nTuesday, September 30 at 4:00 PM\n\n' +
                'This is not a physical meeting. Sign up to participate in a draw of 1 free ticket for GeeCON ' +
                'Prague       The winner will be picked at random. To ente...\n\n'+
                'Details: http://www.meetup.com/Polish-Java-User-Group/events/209417392/'
        event.location.value == 'Online (Virtualna 65535, Kraków, Poland)'
        that event.geo.latitude, closeTo(50.06, 0.001)
        that event.geo.longitude, closeTo(19.95, 0.001)
        event.url.value == 'http://www.meetup.com/Polish-Java-User-Group/events/209417392/'
    }


    def 'should read facebook iCal evhttps://www.facebook.com/events/12345/ent properties'() {
        when:
        def event = new ICal(getResource('facebook.ics')).events[0]

        then:
        event.uid.value == '123@facebook.com'
        event.dateStart.value == dateWithTimeZone('2014-11-14 20:00:00 CET')
        event.dateEnd.value == dateWithTimeZone('2014-11-14 23:00:00 CET')
        event.summary.value == 'Event 1'
        event.description.value == ' Description line 1.' +
                'Description line 1 continues.\n\nhttps://www.facebook.com/events/12345/'
        event.location.value == 'A.D.A. Puławska / ul. Puławska 123456/ Warszawa'
        event.url.value == 'https://www.facebook.com/events/12345/'
    }




    def readMeetupICal(){
        return new ICal(getResource('meetup_jug.ics'))
    }
}  