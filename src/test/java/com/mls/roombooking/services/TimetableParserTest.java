package com.mls.roombooking.services;

import com.mls.roombooking.domains.MeetingRequest;
import com.mls.roombooking.domains.BookingSubmission;
import com.mls.roombooking.exceptions.OfficeHourException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
public class TimetableParserTest {

    private final TimetableParser timetableParser = new TimetableParser();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void buildValidBookingRequestsMap() throws Exception {

        final String input = "0900 1700\n" +
                "2018-02-17 10:17:06 EMP001\n" +
                "2018-02-21 08:00 1\n" +
                "2018-02-16 12:34:56 EMP002\n" +
                "2018-03-21 09:00 2\n" +
                "2018-04-16 12:34:56 EMP012\n" +
                "2018-05-05 19:00 5\n" +
                "2018-05-07 09:28:23 EMP003\n" +
                "2018-05-08 09:00 8\n" +
                "2018-05-09 11:23:45 EMP004\n" +
                "2018-05-11 16:00 1\n" +
                "2018-05-15 17:29:12 EMP005\n" +
                "2018-05-13 16:00 3\n" +
                "2018-05-15 17:29:12 EMP006\n" +
                "2018-05-30 10:00 24\n" +
                "2018-05-17 17:29:12 EMP008\n" +
                "2018-05-30 10:00 -3\n" +
                "2018-05-19 17:29:12 EMP009\n" +
                "2018-05-30 10:00 0\n" +
                "2018-05-20 17:29:12 EMP010\n" +
                "2018-05-30 110:00 0\n" +
                "2018-05-23 17:29:12 EMP011\n" +
                "2018-05-30 10:00 ,4\n" +
                "2018-05-25 17:29:12 EMP007\n" +
                "2018-05-30 10:00 1.5";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);

        Assert.assertEquals(submissionToMeeting.keySet().stream().sorted().collect(Collectors.toSet()), submissionToMeeting.keySet());

        final TreeMap<BookingSubmission, MeetingRequest> expected = new TreeMap<>();
        expected.put(BookingSubmission.parse("2018-02-16 12:34:56 EMP002"), MeetingRequest.parse("2018-03-21 09:00 2", "EMP002"));
        expected.put(BookingSubmission.parse("2018-05-07 09:28:23 EMP003"), MeetingRequest.parse("2018-05-08 09:00 8", "EMP003"));
        expected.put(BookingSubmission.parse("2018-05-09 11:23:45 EMP004"), MeetingRequest.parse("2018-05-11 16:00 1", "EMP004"));
        expected.put(BookingSubmission.parse("2018-05-25 17:29:12 EMP007"), MeetingRequest.parse("2018-05-30 10:00 1.5", "EMP007"));
        expected.put(BookingSubmission.parse("2018-05-23 17:29:12 EMP011"), MeetingRequest.parse("2018-05-30 10:00 ,4", "EMP011"));

        Assert.assertEquals(expected, submissionToMeeting);
    }

    @Test
    public void parseEmptyInput() throws Exception {
        final String input = "  \n ";
        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());
    }

    @Test
    public void parseOfficeHoursOnlyInput() throws Exception {
        final String input = "0900 1700\n";
        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());
    }

    @Test
    public void parseInvalidInput() throws Exception {
        final String input = "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 2";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid format: \"2018-05-17\" is malformed at \"-05-17\"");
        timetableParser.parse(input);
    }

    @Test
    public void buildMeetingWithinWorkingHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 2";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);

        final TreeMap<BookingSubmission, MeetingRequest> expected = new TreeMap<>();
        expected.put(BookingSubmission.parse("2018-05-17 10:17:06 EMP001"), MeetingRequest.parse("2018-05-21 09:00 2", "EMP001"));
        Assert.assertEquals(expected, submissionToMeeting);
    }

    @Test
    public void buildMeetingWithEdgeHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 8";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);

        final TreeMap<BookingSubmission, MeetingRequest> expected = new TreeMap<>();
        expected.put(BookingSubmission.parse("2018-05-17 10:17:06 EMP001"), MeetingRequest.parse("2018-05-21 09:00 8", "EMP001"));
        Assert.assertEquals(expected, submissionToMeeting);
    }

    @Test
    public void ignoreMeetingBeforeWorkingHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 08:00 8";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());

    }

    @Test
    public void ignoreMeetingAfterWorkingHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 17:00 1";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());

    }

    @Test
    public void ignoreMeetingOutsideWorkingHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 08:00 10";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());

    }

    @Test
    public void ignoreMeetingLongerThanWorkingHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 10";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());

    }

    @Test
    public void ignoreMeetingPartiallyOutsideWorkingHours() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 16:00 1.3";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());

    }

    @Test
    public void ignoreMeetingWithTimeBeforeSubmission() throws Exception {

        final String input = "0900 1700\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-17 10:015 1";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);
        assert(submissionToMeeting.isEmpty());

    }

    @Test
    public void buildMeetingsForAlwaysClosedOffice() throws Exception {

        final String input = "0900 0900\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 0";

        thrown.expect(OfficeHourException.class);
        thrown.expectMessage("Office start hour must be different than end hour");

        timetableParser.parse(input);
    }

    @Test
    public void buildMeetingsFor24x7Office() throws Exception {

        final String input = "0000 2359\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 08:00 20";

        final SortedMap<BookingSubmission, MeetingRequest> submissionToMeeting = timetableParser.parse(input);

        final TreeMap<BookingSubmission, MeetingRequest> expected = new TreeMap<>();
        expected.put(BookingSubmission.parse("2018-05-17 10:17:06 EMP001"), MeetingRequest.parse("2018-05-21 08:00 20", "EMP001"));

        Assert.assertEquals(expected, submissionToMeeting);

    }

}
