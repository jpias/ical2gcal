package io.warsaw.ical2gcal

import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Events

class GoogleCalendarSpec extends BaseSpecification {

    def 'should fetch paginated events'() {
        given:
        def gCalApiMock = Mock(Calendar)
        def calendarEventsMock = Mock(Calendar.Events)
        def eventsListMock = Mock(Calendar.Events.List)
        calendarEventsMock.getNextPageToken() >> 'nextToken'
        calendarEventsMock.list(_) >> eventsListMock
        gCalApiMock.events() >> calendarEventsMock
        eventsListMock.execute() >> calendarEventsMock
        def calendar = new GoogleCalendar('mockService', 'mockKey', 'myCalId1', gCalApiMock)


        when:
        calendar.events()

        then:
        1 * eventsListMock.setPageToken(null) >> eventsListMock
        1 * eventsListMock.execute() >> new Events(items: [], nextPageToken: 'nextToken')

        then:
        1 * eventsListMock.setPageToken('nextToken') >> eventsListMock
        1 * eventsListMock.execute() >> new Events(items: [], nextPageToken: null)
    }


}
