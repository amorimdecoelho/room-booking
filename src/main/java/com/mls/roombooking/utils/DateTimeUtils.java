package com.mls.roombooking.utils;

import com.mls.roombooking.domains.BoundedTime;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Set;

public class DateTimeUtils {

    public static DateTime parseDateTime(String entry, String pattern) {
        return DateTime.parse(entry, DateTimeFormat.forPattern(pattern));
    }

    public static boolean isSameDay(DateTime date1, DateTime date2) {
        return date1.withTimeAtStartOfDay().isEqual(date2.withTimeAtStartOfDay());
    }

    public static boolean isConflicting(BoundedTime event, Set<? extends BoundedTime> otherEvents) {
        final long eventStartTime = event.getStart().getMillis();
        final long eventEndTime = event.getEnd().getMillis();

        //checks if the event conflicts with any of the other events
        return otherEvents.stream().anyMatch(request -> {
            final long startTime = request.getStart().getMillis();
            final long endTime = request.getEnd().getMillis();
            return (eventStartTime >= startTime && eventStartTime < endTime) || (
                    eventEndTime > startTime && eventEndTime <= endTime);
        });
    }

}
