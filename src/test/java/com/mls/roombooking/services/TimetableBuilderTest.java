package com.mls.roombooking.services;

import com.google.common.collect.TreeMultimap;
import com.mls.roombooking.domains.MeetingRequest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
public class TimetableBuilderTest {

    private final TimetableBuilder timetableBuilder = new TimetableBuilder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final DateTime today = DateTime.now().withDate(2018, 7, 24);
    private final DateTime tomorrow = today.plusDays(1);
    private final DateTime yesterday = today.minusDays(1);

    @Test
    public void buildTimetable() throws Exception {

        final TreeMultimap<DateTime, MeetingRequest> requests = TreeMultimap.create();
        requests.put(tomorrow, MeetingRequest.parse("2018-05-22 09:00 2\n", "EMP01"));
        requests.put(today, MeetingRequest.parse("2018-05-21 11:00 2\n", "EMP03"));
        requests.put(yesterday, MeetingRequest.parse("2018-05-21 10:00 2\n", "EMP02"));
        requests.put(today, MeetingRequest.parse("2018-05-21 09:00 2\n", "EMP03"));

        final String expected = "2018-07-23\n" +
                "10:00 12:00 EMP02\n" +
                "2018-07-24\n" +
                "09:00 11:00 EMP03\n" +
                "11:00 13:00 EMP03\n" +
                "2018-07-25\n" +
                "09:00 11:00 EMP01";

        Assert.assertEquals(expected, timetableBuilder.build(requests));

    }

    @Test
    public void buildEmptyTimetable() throws Exception {

        final TreeMultimap<DateTime, MeetingRequest> requests = TreeMultimap.create();

        final String expected = "";

        Assert.assertEquals(expected, timetableBuilder.build(requests));

    }

}
