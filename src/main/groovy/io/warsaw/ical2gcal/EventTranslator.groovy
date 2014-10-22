package io.warsaw.ical2gcal

import biweekly.component.VEvent
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime

class EventTranslator {

    String calendarPrefix

    EventTranslator(String calendarPrefix) {
        this.calendarPrefix = calendarPrefix
    }

    Event iCalEventToGcalEvent(VEvent event) {
        def uniqueId = uniqueId(event)
        def extendedProperties = new Event.ExtendedProperties()
        extendedProperties.setShared([sourceId: uniqueId])
        def params = [
                summary: event.summary?.value,
                description: event.description?.value,
                start: event.dateStart?.value ? toEventDateTime(event.dateStart.value) : null,
                end: event.dateEnd?.value ? toEventDateTime(event.dateEnd.value) : null,
                location: event.location?.value,
                source: new Event.Source(url: event.url?.value),
                extendedProperties: extendedProperties
        ]
        return new Event(params)
    }

    String uniqueId(VEvent event) {
        return "${this.calendarPrefix}|${event.uid.value}"
    }

    String sourceId(Event event) {
        return event.extendedProperties?.shared?.get('sourceId')
    }

    boolean isSynchronizedFromThisCalendar(Event event) {
        return sourceId(event)?.startsWith("${calendarPrefix}|")
    }

    private toEventDateTime(Date date){
        new EventDateTime(dateTime: new DateTime(date))
    }
}
