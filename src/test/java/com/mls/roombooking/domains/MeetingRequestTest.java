package com.mls.roombooking.domains;
import static com.mls.roombooking.utils.DateTimeUtils.parseDateTime;

import com.mls.roombooking.exceptions.MeetingRequestException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MeetingRequestTest {

    private final String pattern = "yyy-MM-dd HH:mm";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void buildMeetingRequest() throws Exception {
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 2", "EMP");
        final DateTime startTime = parseDateTime("2018-05-21 15:00", pattern);
        final DateTime endTime = parseDateTime("2018-05-21 17:00", pattern);

        Assert.assertEquals(startTime, meetingRequest.start);
        Assert.assertEquals(endTime, meetingRequest.end);
        Assert.assertEquals("EMP", meetingRequest.employee);
    }

    @Test
    public void buildMeetingRequestOverlappingDays() throws Exception {
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 23", "EMP");
        final DateTime startTime = parseDateTime("2018-05-21 15:00", pattern);
        final DateTime endTime = parseDateTime("2018-05-22 14:00", pattern);

        Assert.assertEquals(startTime, meetingRequest.start);
        Assert.assertEquals(endTime, meetingRequest.end);
    }

    @Test
    public void buildMeetingRequestWithPartialHours() throws Exception {
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 2.75", "EMP");
        final DateTime startTime = parseDateTime("2018-05-21 15:00", pattern);
        final DateTime endTime = parseDateTime("2018-05-21 17:45", pattern);

        Assert.assertEquals(startTime, meetingRequest.start);
        Assert.assertEquals(endTime, meetingRequest.end);
    }

    @Test
    public void buildMeetingRequestWithPartialHoursUsingComma() throws Exception {
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 2,75", "EMP");
        final DateTime startTime = parseDateTime("2018-05-21 15:00", pattern);
        final DateTime endTime = parseDateTime("2018-05-21 17:45", pattern);

        Assert.assertEquals(startTime, meetingRequest.start);
        Assert.assertEquals(endTime, meetingRequest.end);
    }

    @Test
    public void build5MinutesMeeting() throws Exception {
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 0.083333333", "EMP");
        final DateTime startTime = parseDateTime("2018-05-21 15:00", pattern);
        final DateTime endTime = parseDateTime("2018-05-21 15:05", pattern);

        Assert.assertEquals(startTime, meetingRequest.start);
        Assert.assertEquals(endTime, meetingRequest.end);
    }

    @Test
    public void build24hMeeting() throws Exception {
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 24", "EMP");
        final DateTime startTime = parseDateTime("2018-05-21 15:00", pattern);
        final DateTime endTime = parseDateTime("2018-05-22 15:00", pattern);

        Assert.assertEquals(startTime, meetingRequest.start);
        Assert.assertEquals(endTime, meetingRequest.end);
    }

    @Test
    public void failToBuildMeetingWithInvalidDuration() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 -a", "EMP");
    }

    @Test
    public void failToBuildZeroDurationMeeting() throws Exception {
        thrown.expect(MeetingRequestException.class);
        thrown.expectMessage("Meeting duration must be greater than 5 minutes");
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 0", "EMP");
    }

    @Test
    public void failToBuildLessThan5MinutesMeeting() throws Exception {
        thrown.expect(MeetingRequestException.class);
        thrown.expectMessage("Meeting duration must be greater than 5 minutes");
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 0.07", "EMP");
    }

    @Test
    public void failToBuildMoreThan24hMeeting() throws Exception {
        thrown.expect(MeetingRequestException.class);
        thrown.expectMessage("Meeting duration must not exceed 24 hours");
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 24.01", "EMP");
    }


    @Test
    public void failToBuildNegativeDurationMeeting() throws Exception {
        thrown.expect(MeetingRequestException.class);
        thrown.expectMessage("Meeting duration must be a positive value");
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-21 15:00 -5", "EMP");
    }

    @Test
    public void failToBuildMeetingWithInvalidDateTime() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        final MeetingRequest meetingRequest = MeetingRequest.parse("2018-05-211 15:00 5", "EMP");
    }

}