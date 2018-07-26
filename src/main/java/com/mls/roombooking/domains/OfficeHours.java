package com.mls.roombooking.domains;

import com.mls.roombooking.exceptions.OfficeHourException;
import com.mls.roombooking.utils.DateTimeUtils;
import org.joda.time.DateTime;

public class OfficeHours {

    public final DateTime start;
    public final DateTime end;
    public final boolean fullDay;

    public static OfficeHours parse(String entry) throws OfficeHourException {
        final String[] entries = entry.split(" ");
        final DateTime start = DateTimeUtils.parseDateTime(entries[0], "HHmm");
        final DateTime end = DateTimeUtils.parseDateTime(entries[1], "HHmm");
        if (start.isEqual(end)) throw new OfficeHourException("Office start hour must be different than end hour");
        if (start.isAfter(end)) throw new OfficeHourException("Office start hour must be before the end hour");
        final boolean fullDay = start.isEqual(start.withTimeAtStartOfDay()) && end.isEqual(end.plusDays(1).withTimeAtStartOfDay().minusMinutes(1));
        return new OfficeHours(start, end, fullDay);
    }

    private OfficeHours(DateTime start, DateTime end, boolean fullDay) {
        this.start = start;
        this.end = end;
        this.fullDay = fullDay;
    }

    @Override
    public String toString() {
        return "OfficeHours{" +
                "start=" + start +
                ", end=" + end +
                ", fullDay=" + fullDay +
                '}';
    }
}
