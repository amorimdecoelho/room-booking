package com.mls.roombooking.domains;

import com.mls.roombooking.exceptions.MeetingRequestException;
import org.joda.time.DateTime;

import static com.mls.roombooking.utils.DateTimeUtils.parseDateTime;

public class MeetingRequest implements BoundedTime, Comparable<MeetingRequest> {

    private static final String pattern = "yyyy-MM-dd HH:mm";

    public final DateTime start;
    public final DateTime end;
    public final String employee;

    public final DateTime getStart() {
        return start;
    }

    public final DateTime getEnd() {
        return end;
    }

    public static MeetingRequest parse(String entry, String employee) {
        final int durationSeparator = entry.lastIndexOf(" ");
        final DateTime start = parseDateTime(entry.substring(0, durationSeparator), pattern);
        final float hours = Float.parseFloat(entry.substring(durationSeparator + 1).replace(",", "."));

        final int minutes = Math.round(hours*60);

        if (minutes < 0) {
            throw new MeetingRequestException("Meeting duration must be a positive value");
        } else if (minutes < 5) {
            throw new MeetingRequestException("Meeting duration must be greater than 5 minutes");
        } else if (hours > 24) {
            throw new MeetingRequestException("Meeting duration must not exceed 24 hours");
        }

        final DateTime end = start.plusMinutes(minutes);

        return new MeetingRequest(start, end, employee);
    }

    private MeetingRequest(DateTime start, DateTime end, String employee) {
        this.start = start;
        this.end = end;
        this.employee = employee;
    }

    @Override
    public int compareTo(MeetingRequest request) {
        return this.start.compareTo(request.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingRequest that = (MeetingRequest) o;

        if (!start.equals(that.start)) return false;
        if (!end.equals(that.end)) return false;
        return employee.equals(that.employee);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + employee.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MeetingRequest{" +
                "start=" + start +
                ", end=" + end +
                ", employee='" + employee + '\'' +
                '}';
    }


}
