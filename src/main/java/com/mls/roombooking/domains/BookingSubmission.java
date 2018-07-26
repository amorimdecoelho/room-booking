package com.mls.roombooking.domains;

import org.joda.time.DateTime;

import static com.mls.roombooking.utils.DateTimeUtils.parseDateTime;

public class BookingSubmission implements Comparable<BookingSubmission> {

    private static final String pattern = "yyyy-MM-dd HH:mm:ss";

    public final DateTime timestamp;
    public final String employee;

    public static BookingSubmission parse(String entry) {
        final int employeeSeparator = entry.lastIndexOf(" ");
        final DateTime timestamp = parseDateTime(entry.substring(0, employeeSeparator), pattern);
        final String employee = entry.substring(employeeSeparator + 1);
        return new BookingSubmission(timestamp, employee);
    }

    private BookingSubmission(DateTime timestamp, String employee) {
        this.timestamp = timestamp;
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "BookingSubmission{" +
                "timestamp=" + timestamp +
                ", employee='" + employee + '\'' +
                '}';
    }

    @Override
    public int compareTo(BookingSubmission submission) {
        return this.timestamp.compareTo(submission.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookingSubmission that = (BookingSubmission) o;

        if (!timestamp.equals(that.timestamp)) return false;
        return employee.equals(that.employee);
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + employee.hashCode();
        return result;
    }

}
