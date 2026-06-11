package com.forge.talentacquisitionengine.interviewService.interview.integration;

import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GoogleCalendarClient {

    public GoogleCalendarResponse createEvent(
            Interview interview
    ) {

        String eventId =
                UUID.randomUUID().toString();

        String meetLink =
                "https://meet.google.com/mock-"
                        + UUID.randomUUID();

        return new GoogleCalendarResponse(
                eventId,
                meetLink
        );
    }

    public void updateEvent(
            String eventId,
            Interview interview
    ) {

        System.out.println(
                "Updating Google Calendar Event: "
                        + eventId
        );
    }

    public void deleteEvent(
            String eventId
    ) {

        System.out.println(
                "Deleting Google Calendar Event: "
                        + eventId
        );
    }
}