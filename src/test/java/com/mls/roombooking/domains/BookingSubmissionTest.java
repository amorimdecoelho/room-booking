package com.mls.roombooking.domains;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.mls.roombooking.utils.DateTimeUtils.parseDateTime;

@RunWith(SpringRunner.class)
public class BookingSubmissionTest {

    private final String pattern = "yyy-MM-dd HH:mm:ss";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void buildBookingSubmission() throws Exception {
        final BookingSubmission bookingSubmission = BookingSubmission.parse("2018-05-17 10:17:06 EMP001");
        final DateTime time = parseDateTime("2018-05-17 10:17:06", pattern);

        Assert.assertEquals(time, bookingSubmission.timestamp);
        Assert.assertEquals("EMP001", bookingSubmission.employee);
    }

    @Test
    public void failBuildBookingSubmissionWithInvalidTimestamp() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid format: \"2018-05-17 10:17\" is too short");
        BookingSubmission.parse("2018-05-17 10:17 EMP001");
    }


}