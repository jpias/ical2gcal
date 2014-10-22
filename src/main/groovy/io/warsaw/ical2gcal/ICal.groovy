package io.warsaw.ical2gcal

import biweekly.Biweekly
import biweekly.ICalendar
import biweekly.component.VEvent

class ICal {

    ICalendar calendar

    ICal(URL url) {
        parse(url.getText(requestProperties: ['User-Agent': 'ical2gcal synchronizer']))
    }

    ICal(File file) {
        parse(file.text)
    }

    List<VEvent> getEvents() {
        return calendar.events
    }

    private parse(text){
        this.calendar = Biweekly.parse(text).first()
    }
}
