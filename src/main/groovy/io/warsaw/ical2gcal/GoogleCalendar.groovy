package io.warsaw.ical2gcal

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import groovy.util.logging.Log

@Log
class GoogleCalendar {

    Calendar calendarApi
    String calendarId
    Random randomGenerator = new Random();

    GoogleCalendar(String serviceId, String keyFile, String calendarId, Calendar calendarApi = null) {
        this.calendarId = calendarId
        if (calendarApi == null) {
            this.calendarApi = connectToCalendar(serviceId, keyFile)
        } else {
            this.calendarApi = calendarApi
        }
    }

    def events(futureOnly = false) {
        log.fine "Fetching Google Calendar event(s)"
        def events = fetchEvents(futureOnly)
        log.fine "Fetched ${events.size()} Google Calendar event(s)"
        return events
    }

    def insert(Event event) {
        log.fine "Adding Google Calendar event"
        try{
		calendarApi.events().insert(calendarId, event).execute()
	} catch (GoogleJsonResponseException e) {
		if (e.getDetails().getCode() == 403
			&& (e.getDetails().getErrors().get(0).getReason().equals("rateLimitExceeded")
				|| e.getDetails().getErrors().get(0).getReason().equals("userRateLimitExceeded"))) {
			// Apply exponential backoff.
			log.fine "Apply exponential backoff."
			Thread.sleep(1000 + randomGenerator.nextInt(1001));
			insert(event);
		} else {
		        // Other error, re-throw.
	        	throw e;
		}
	}
	
    }

    def update(Event event) {
        log.fine "Updating event with Google Calendar ID: ${event.id}"
	try{
	        calendarApi.events().update(calendarId, event.id, event).execute()
	} catch (GoogleJsonResponseException e) {
		if (e.getDetails().getCode() == 403
			&& (e.getDetails().getErrors().get(0).getReason().equals("rateLimitExceeded")
				|| e.getDetails().getErrors().get(0).getReason().equals("userRateLimitExceeded"))) {
			// Apply exponential backoff.
			log.fine "Apply exponential backoff."
			Thread.sleep(1000 + randomGenerator.nextInt(1001));
			update(event);
		} else {
		        // Other error, re-throw.
	        	throw e;
		}
	}
    }

    def delete(Event event) {
        log.fine "Deleting event with Google Calendar ID: ${event.id}"
	try{
        	calendarApi.events().delete(calendarId, event.id).execute()
	} catch (GoogleJsonResponseException e) {
		if (e.getDetails().getCode() == 403
			&& (e.getDetails().getErrors().get(0).getReason().equals("rateLimitExceeded")
				|| e.getDetails().getErrors().get(0).getReason().equals("userRateLimitExceeded"))) {
			// Apply exponential backoff.
			log.fine "Apply exponential backoff."
			Thread.sleep(1000 + randomGenerator.nextInt(1001));
			delete(event);
		} else {
		        // Other error, re-throw.
	        	throw e;
		}
	}
    }

    private fetchEvents(boolean futureOnly) {
        def events = []
        def pageToken = null
        def i = 0
        def now = new DateTime(new Date())
        // Watch out !
        // Google keeps deleted events forever and use them to calculate offset
        // You may get a few empty pages with nextToken before you get to events
        // more: http://stackoverflow.com/questions/18566386/google-calendar-v3-api-events-list-request-return-empty-list
        while (true) {
            log.fine "Fetching page ${++i} of Google Calendar events"
            def list = calendarApi.events().list(calendarId)
  	    if (futureOnly) {
                list.setTimeMin(now)
            }
            def eventsPage = list.setPageToken(pageToken).execute()
            pageToken = eventsPage.getNextPageToken()
            events.addAll(eventsPage.items)
            if (pageToken == null) {
                break
            }// no do while in groovy :(
        }
        return events
    }

    private Calendar connectToCalendar(String serviceId, String keyFile) {

        def transport = new NetHttpTransport()
        def jacksonFactory = new JacksonFactory()


        def credential = new GoogleCredential.Builder()
                .setTransport(transport)
                .setJsonFactory(jacksonFactory)
                .setServiceAccountId(serviceId)
                .setServiceAccountScopes([CalendarScopes.CALENDAR])
                .setServiceAccountPrivateKeyFromP12File(new File(keyFile))
                .build()

        return new Calendar.Builder(transport, jacksonFactory,
                credential).setApplicationName('Main').build()
    }


}
