package com.mls.roombooking.services;

import com.google.common.collect.TreeMultimap;
import com.mls.roombooking.domains.MeetingRequest;
import com.mls.roombooking.domains.BookingSubmission;
import com.mls.roombooking.utils.DateTimeUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.SortedMap;

@Service
public class TimetableService {

    @Autowired
    private TimetableParser timetableParser;

    @Autowired
    private TimetableBuilder timetableBuilder;

    private TreeMultimap<DateTime, MeetingRequest> timetable(
            SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting) {

        //A tree multimap is used to group multiple meetings by meeting start date
        //and to keep the meetings also ordered by date
        TreeMultimap<DateTime, MeetingRequest> dateToMeetings = TreeMultimap.create();

        //the submissionToMeeting map is already sorted by submission timestamp, it ensures
        //that higher priority requests are analysed first and take precedence over conflicts
        submissionToMeeting.forEach((bookingSubmission, bookingRequest) -> {

            final DateTime dateKey = bookingRequest.start.withTimeAtStartOfDay();
            final Set<MeetingRequest> approvedRequests = dateToMeetings.get(dateKey);

            if (!DateTimeUtils.isConflicting(bookingRequest, approvedRequests)) {
                dateToMeetings.put(dateKey, bookingRequest);
            }

        });

        return dateToMeetings;
    }

    public String createTimetable(String timetableRequest) {
        SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(timetableRequest);
        return timetableBuilder.build(timetable(submissionToMeeting));
    }

}
