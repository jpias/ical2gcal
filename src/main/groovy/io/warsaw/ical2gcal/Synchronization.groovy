package io.warsaw.ical2gcal

import biweekly.component.VEvent
import com.google.api.services.calendar.model.Event
import groovy.util.logging.Log

@Log
class Synchronization {

    ICal iCal
    GoogleCalendar gCal
    EventTranslator translator
    List<VEvent> iCalEvents
    List<Event> gCalEvents
    List<Event> updatedGCalEvents = []

    Synchronization(ICal iCal, GoogleCalendar gCal, String sourceCalendarUniquePrefix) {
        this.iCal = iCal
        this.gCal = gCal
        this.translator = new EventTranslator(sourceCalendarUniquePrefix)
    }

    def synchronize(futureOnly=false) {
        log.info 'Synchronizing calendars'
        iCalEvents = iCal.events
        log.info "Fetched ${iCalEvents.size()} iCalendar events"
        gCalEvents = gCal.events(futureOnly)
        log.info "Fetched ${gCalEvents.size()} Google Calendar events"
        addOrUpdateEvents()
        removeNonExistingEvents()
    }

    def synchronizeFutureEvents(){
        synchronize(true)
    }

    private addOrUpdateEvents() {
        log.info "Creating or updating ${iCalEvents.size()} iCalendar events"
        iCalEvents.each { iCalEvent ->
            def gCalEvent = translator.iCalEventToGcalEvent(iCalEvent)
            def existingGCalEvent = gCalEvents.find {
                translator.sourceId(it) == translator.uniqueId(iCalEvent)
            }
            if (existingGCalEvent) {
                log.info 'Updating ' + iCalEvent.uid.value
                gCalEvent.setId(existingGCalEvent.getId())
                gCal.update(gCalEvent)
                updatedGCalEvents << existingGCalEvent
            } else {
                log.info 'Creating ' + iCalEvent.uid.value
                gCal.insert(gCalEvent)
            }
        }
    }

    private removeNonExistingEvents() {
        log.info "Deleting iCalendar events that don't exist any more"
        def eventsToDelete = gCalEvents.findAll{shouldBeDeleted(it)}
        log.info "Found ${eventsToDelete.size()} events to delete"
        if(eventsToDelete.isEmpty()){
            return
        }
        eventsToDelete.each{
            log.info "Deleting ${translator.sourceId(it)}"
            gCal.delete(it)
        }
    }

    private shouldBeDeleted(Event event) {
        return translator.isSynchronizedFromThisCalendar(event) && !(event in updatedGCalEvents)
    }


}
