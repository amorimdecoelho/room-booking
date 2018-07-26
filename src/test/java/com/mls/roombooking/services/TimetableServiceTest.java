package com.mls.roombooking.services;

import com.google.common.collect.TreeMultimap;
import com.mls.roombooking.domains.BookingSubmission;
import com.mls.roombooking.domains.MeetingRequest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.TreeMap;

import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class TimetableServiceTest {

    @TestConfiguration
    static class RoomBookingServiceTestContextConfiguration {

        @Bean
        public TimetableService roomBookingService() {
            return new TimetableService();
        }
    }

    @Autowired
    private TimetableService timetableService;

    @MockBean
    private TimetableParser timetableParser;

    @MockBean
    private TimetableBuilder timetableBuilder;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * createTimetable
     */

    @Test
    public void createTimetableWithValidNonConflictingEntries() throws Exception {

        final String timetableRequest = "0900 1730\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 2\n" +
                "2018-05-16 12:34:57 EMP007\n" +
                "2018-05-21 09:30 0.5\n" +
                "2018-05-16 12:34:56 EMP002\n" +
                "2018-05-21 09:00 2\n" +
                "2018-05-16 09:28:23 EMP003\n" +
                "2018-05-22 14:00 2\n" +
                "2018-05-17 11:23:45 EMP004\n" +
                "2018-05-22 16:00 1\n" +
                "2018-05-15 17:29:12 EMP005\n" +
                "2018-05-21 16:00 3\n" +
                "2018-05-30 17:29:12 EMP006\n" +
                "2018-05-21 10:00 3";

        final String output = "2018-05-21\n" +
                "09:00 11:00 EMP002\n" +
                "2018-05-22\n" +
                "14:00 16:00 EMP003\n" +
                "16:00 17:00 EMP004";

        final TreeMap<BookingSubmission, MeetingRequest> submissionToMeeting = new TreeMap<>();
        submissionToMeeting.put(BookingSubmission.parse("2018-05-17 10:17:06 EMP001"), MeetingRequest.parse("2018-05-21 09:00 2", "EMP001"));
        submissionToMeeting.put(BookingSubmission.parse("2018-05-16 12:34:56 EMP002"), MeetingRequest.parse("2018-05-21 09:00 2", "EMP002"));
        submissionToMeeting.put(BookingSubmission.parse("2018-05-16 09:28:23 EMP003"), MeetingRequest.parse("2018-05-22 14:00 2", "EMP003"));
        submissionToMeeting.put(BookingSubmission.parse("2018-05-17 11:23:45 EMP004"), MeetingRequest.parse("2018-05-22 16:00 1", "EMP004"));
        submissionToMeeting.put(BookingSubmission.parse("2018-05-30 17:29:12 EMP006"), MeetingRequest.parse("2018-05-21 10:00 3", "EMP006"));
        submissionToMeeting.put(BookingSubmission.parse("2018-05-16 12:34:57 EMP007"), MeetingRequest.parse("2018-05-21 09:30 0.5", "EMP007"));

        when(timetableParser.parse(timetableRequest)).thenReturn(submissionToMeeting);

        final TreeMultimap<DateTime, MeetingRequest> dateToMeetings = TreeMultimap.create();
        dateToMeetings.put(DateTime.parse("2018-05-21"), MeetingRequest.parse("2018-05-21 09:00 2", "EMP002"));
        dateToMeetings.put(DateTime.parse("2018-05-22"), MeetingRequest.parse("2018-05-22 14:00 2", "EMP003"));
        dateToMeetings.put(DateTime.parse("2018-05-22"), MeetingRequest.parse("2018-05-22 16:00 1", "EMP004"));

        when(timetableBuilder.build(dateToMeetings)).thenReturn(output);

        final String timetable = timetableService.createTimetable(timetableRequest);
        Assert.assertEquals(output, timetable);
    }

}
