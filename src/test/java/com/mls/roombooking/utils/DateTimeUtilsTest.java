package com.mls.roombooking.utils;

import com.mls.roombooking.domains.BoundedTime;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.TreeSet;

import static com.mls.roombooking.utils.DateTimeUtils.parseDateTime;

@RunWith(SpringRunner.class)
public class DateTimeUtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * isSameDay
     */

    @Test
    public void identifyDatesOfSameDay() throws Exception {
        final String pattern = "yyyy-MM-dd HH:mm";
        final DateTime date1 = DateTimeUtils.parseDateTime("2018-12-15 15:03", pattern);
        final DateTime date2 = DateTimeUtils.parseDateTime("2018-12-15 00:59", pattern);
        Assert.assertTrue(DateTimeUtils.isSameDay(date1, date2));
    }

    @Test
    public void identifyDatesOfDifferentDays() throws Exception {
        final String pattern = "yyyy-MM-dd HH:mm";
        final DateTime date1 = DateTimeUtils.parseDateTime("2018-12-15 00:00", pattern);
        final DateTime date2 = DateTimeUtils.parseDateTime("2018-12-14 23:59", pattern);
        Assert.assertFalse(DateTimeUtils.isSameDay(date1, date2));
    }

    /**
     * parseDateTime
     * */

    @Test
    public void parseDateTimeUsingPattern() throws Exception {
        final String pattern = "yyyy-MM-dd HH:mm";
        final String dateTimeString = "2017-05-03 03:29";
        final DateTime dateTime = DateTimeUtils.parseDateTime(dateTimeString, pattern);
        assert(dateTime).equals(DateTime.parse(dateTimeString, DateTimeFormat.forPattern(pattern)));
    }

    @Test()
    public void failParsingDateTimeForWrongPattern() throws Exception {
        final String pattern = "yyyy-MM-dd HH:mm:ss";
        final String dateTimeString = "2017-05-03 03:29";
        thrown.expectMessage("Invalid format: \"2017-05-03 03:29\" is too short");
        DateTimeUtils.parseDateTime(dateTimeString, pattern);
    }

    /**
     * isConflicting
     */

    private class TimeBox implements BoundedTime, Comparable<TimeBox> {

        private static final String pattern = "yyyy-MM-dd HH:mm";

        private final DateTime start;
        private final DateTime end;

        TimeBox(String timestamp, float hours) {
            final DateTime start = parseDateTime(timestamp, pattern);
            final int minutes = Math.round(hours*60);
            final DateTime end = start.plusMinutes(minutes);
            this.start = start;
            this.end = end;
        }

        public DateTime getStart() {
            return start;
        }

        public DateTime getEnd() {
            return end;
        }

        @Override
        public int compareTo(TimeBox o) {
            return getStart().compareTo(o.getStart());
        }
    }

    @Test
    public void notConflictWithEmptySet() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 09:00", 2);
        Assert.assertFalse(DateTimeUtils.isConflicting(timeBox, new TreeSet<>()));
    }

    @Test
    public void conflictWitEventStartingLater() throws Exception {
        final TimeBox otherTimeBox1 = new TimeBox("2018-05-22 10:00", 2);
        final TimeBox otherTimeBox2 = new TimeBox("2018-05-21 09:00", 2);

        final TimeBox timeBox = new TimeBox("2018-05-22 09:00", 2);

        final TreeSet<TimeBox> approvedRequests = new TreeSet<>();
        approvedRequests.add(otherTimeBox1);
        approvedRequests.add(otherTimeBox2);

        Assert.assertTrue(DateTimeUtils.isConflicting(timeBox, approvedRequests));
    }

    @Test
    public void conflictWithEventStartingBefore() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 11:00", 2);

        final TimeBox otherBox1 = new TimeBox("2018-05-22 10:00", 2);
        final TimeBox otherBox2 = new TimeBox("2018-05-21 09:00", 2);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherBox1);
        otherTimeBoxes.add(otherBox2);

        Assert.assertTrue(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void conflictWithEventThatStartsBeforeAndEndsLater() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 11:00", 1);

        final TimeBox otherBox1 = new TimeBox("2018-05-22 10:00", 2);
        final TimeBox otherBox2 = new TimeBox("2018-05-21 09:00", 2);


        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherBox1);
        otherTimeBoxes.add(otherBox2);

        Assert.assertTrue(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void conflictWithOverlappingDaysEvent() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-23 01:00", 1);

        final TimeBox otherBox1 = new TimeBox("2018-05-22 20:00", 6);
        final TimeBox otherBox2 = new TimeBox("2018-05-21 09:00", 2);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherBox1);
        otherTimeBoxes.add(otherBox2);

        Assert.assertTrue(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void notConflictWithOverlappingDaysEvent() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-23 01:00", 1);

        final TimeBox otherBox1 = new TimeBox("2018-05-22 20:00", 5 );
        final TimeBox otherBox2 = new TimeBox("2018-05-21 09:00", 2);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherBox1);
        otherTimeBoxes.add(otherBox2);

        Assert.assertFalse(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void notConflictWithLeftEdge() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 08:00",2 );

        final TimeBox otherTimeBox1 = new TimeBox("2018-05-22 10:00", 2);
        final TimeBox otherTimeBox2 = new TimeBox("2018-05-21 09:00",  2);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherTimeBox1);
        otherTimeBoxes.add(otherTimeBox2);

        Assert.assertFalse(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void notConflictWithRightEdge() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 12:00", 2);

        final TimeBox otherTimeBox1 = new TimeBox("2018-05-22 10:00", 2);
        final TimeBox otherTimeBox2 = new TimeBox("2018-05-21 09:00", 2);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherTimeBox1);
        otherTimeBoxes.add(otherTimeBox2);

        Assert.assertFalse(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void conflictWithinMinutes() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 12:00", 0.25f);

        final TimeBox otherTimeBox1 = new TimeBox("2018-05-22 11:00", 1.5f);
        final TimeBox otherTimeBox2 = new TimeBox("2018-05-21 09:00", 2);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherTimeBox1);
        otherTimeBoxes.add(otherTimeBox2);

        Assert.assertTrue(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

    @Test
    public void notConflictForMinutesEdge() throws Exception {
        final TimeBox timeBox = new TimeBox("2018-05-22 12:15", 0.25f);

        final TimeBox otherTimeBox1 = new TimeBox("2018-05-22 11:00", 1.25f);
        final TimeBox otherTimeBox2 = new TimeBox("2018-05-22 12:00", 0.25f);

        final TreeSet<TimeBox> otherTimeBoxes = new TreeSet<>();
        otherTimeBoxes.add(otherTimeBox1);
        otherTimeBoxes.add(otherTimeBox2);

        Assert.assertFalse(DateTimeUtils.isConflicting(timeBox, otherTimeBoxes));
    }

}