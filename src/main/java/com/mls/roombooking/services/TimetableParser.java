package com.mls.roombooking.services;

import com.mls.roombooking.domains.MeetingRequest;
import com.mls.roombooking.domains.BookingSubmission;
import com.mls.roombooking.domains.OfficeHours;
import com.mls.roombooking.utils.DateTimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.SortedMap;
import java.util.TreeMap;

@Service
class TimetableParser {

    private final Log logger = LogFactory.getLog(this.getClass());

    private boolean validHours(MeetingRequest meetingRequest, OfficeHours officeHours) {

        final boolean differentDays = !DateTimeUtils.isSameDay(meetingRequest.start, meetingRequest.end);

        //meeting hour is always valid for 24x7 offices
        if (officeHours.fullDay) {
            return true;
        //if the meeting start and end are on different days, the office had to be 24x7
        } else if (differentDays) {
            return false;
        } else {
            //checks if meeting is within office hours
           return meetingRequest.start.getSecondOfDay() >= officeHours.start.getSecondOfDay() &&
                    meetingRequest.end.getSecondOfDay() <= officeHours.end.getSecondOfDay();
        }
    }

    SortedMap<BookingSubmission, MeetingRequest> parse(String timetableRequest) {

        //a tree map is used to guarantee the order of the keys, since the meeting
        //priority depends on the booking submission timestamp
        final TreeMap<BookingSubmission, MeetingRequest> bookingMap = new TreeMap<>();

        final String[] lines = timetableRequest.trim().split("\\n");

        if (timetableRequest.trim().isEmpty() || lines.length == 0) {
            return bookingMap;
        }

        //first line represents office hours
        final OfficeHours officeHours = OfficeHours.parse(lines[0]);

        //following pairs of line represent a submission followed by the meeting request
        for (int i = 1 ; i < lines.length ; i+= 2) {

            //invalid pair won't prevent further pairs from being correctly parsed
            try {
                final BookingSubmission bookingSubmission = BookingSubmission.parse(lines[i]);
                final MeetingRequest meetingRequest = MeetingRequest.parse(lines[i+1], bookingSubmission.employee);

                final boolean meetingBeforeSubmission = meetingRequest.start.isBefore(bookingSubmission.timestamp);

                //valid <submission,meeting> pair and meeting within working hours
                if (!meetingBeforeSubmission && validHours(meetingRequest, officeHours)) {
                    bookingMap.put(bookingSubmission, meetingRequest);
                }
            } catch (Throwable t) {
                final String message = "Error when parsing booking lines " + i + " and " + (i+1) + ":\n"
                        + lines[i] + "\n" + lines[i+1] + "\n" + t.getMessage();
               logger.warn(message);
            }

        }

        return bookingMap;

    }

}
