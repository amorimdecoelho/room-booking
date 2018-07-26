package com.mls.roombooking.domains;

import org.joda.time.DateTime;

public interface BoundedTime {

    public DateTime getStart();
    public DateTime getEnd();

}
