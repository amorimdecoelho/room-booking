package com.mls.roombooking.domains;

import com.mls.roombooking.exceptions.OfficeHourException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class OfficeHoursTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void buildOfficeHours() throws Exception {
        final OfficeHours officeHours = OfficeHours.parse("0900 1730");
        final DateTime baseTime = DateTime.now().withTimeAtStartOfDay();
        final int expectedStart = baseTime.withHourOfDay(9).withMinuteOfHour(0).getSecondOfDay();
        final int expectedEnd = baseTime.withHourOfDay(17).withMinuteOfHour(30).getSecondOfDay();
        Assert.assertEquals(expectedStart, officeHours.start.getSecondOfDay());
        Assert.assertEquals(expectedEnd, officeHours.end.getSecondOfDay());
        Assert.assertEquals(false, officeHours.fullDay);
    }

    @Test
    public void buildFullDayOfficeHours() throws Exception {
        final OfficeHours officeHours = OfficeHours.parse("0000 2359");
        final DateTime baseTime = DateTime.now().withTimeAtStartOfDay();
        final int expectedStart = baseTime.withHourOfDay(0).withMinuteOfHour(0).getSecondOfDay();
        final int expectedEnd = baseTime.withHourOfDay(23).withMinuteOfHour(59).getSecondOfDay();
        Assert.assertEquals(expectedStart, officeHours.start.getSecondOfDay());
        Assert.assertEquals(expectedEnd, officeHours.end.getSecondOfDay());
        Assert.assertEquals(true, officeHours.fullDay);
    }

    @Test
    public void failToBuildOfficeHoursWithNegativeHours() throws Exception {
        thrown.expect(java.lang.IllegalArgumentException.class);
        final OfficeHours officeHours = OfficeHours.parse("-0900 -1730");
    }

    @Test
    public void failToBuildOfficeHoursWithWrongFormat() throws Exception {
        thrown.expect(java.lang.IllegalArgumentException.class);
        final OfficeHours officeHours = OfficeHours.parse("09000 00800");
    }

    @Test
    public void failToBuildOfficeHoursWithReverseStartAndEnd() throws Exception {
        thrown.expect(OfficeHourException.class);
        thrown.expectMessage("Office start hour must be before the end hour");
        final OfficeHours officeHours = OfficeHours.parse("0900 0800");
    }

    @Test
    public void failToBuildOfficeHoursWitZeroDuration() throws Exception {
        thrown.expect(OfficeHourException.class);
        thrown.expectMessage("Office start hour must be different than end hour");
        final OfficeHours officeHours = OfficeHours.parse("0900 0900");
    }

}