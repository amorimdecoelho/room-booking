package com.mls.roombooking.services;

import com.google.common.collect.TreeMultimap;
import com.mls.roombooking.domains.MeetingRequest;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
class TimetableBuilder {

    String build(TreeMultimap<DateTime, MeetingRequest> bookingMap) {

        final StringBuilder bookingTimetable = new StringBuilder();

        bookingMap.keySet().forEach(date -> {
            bookingTimetable.append(date.toString("yyyy-MM-dd")).append("\n");

            for (MeetingRequest meetingRequest : bookingMap.get(date)) {

                final String startTime = meetingRequest.start.toString("HH:mm");
                final String endTime = meetingRequest.end.toString("HH:mm");
                bookingTimetable.append(startTime).append(" ").append(endTime).append(" ").
                        append(meetingRequest.employee).append("\n");
            }

        });

        return bookingTimetable.toString().trim();
    }

}
