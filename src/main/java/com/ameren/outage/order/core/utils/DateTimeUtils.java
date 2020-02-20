package com.ameren.outage.order.core.utils;

import java.sql.Timestamp;
import java.time.Clock;

public class DateTimeUtils {

	/**
	 * Check if passed in java.sql.Timestamp is after current UTC date time
	 * return true if timeStamp is future compared to current UTC date time
	 */
	public static boolean isAfterCurrentUtcTime(Timestamp timeStamp) {
		long currentUtcTime = Clock.systemUTC().millis();
        Timestamp currentUtcTimeStamp = new Timestamp(currentUtcTime);
        return currentUtcTimeStamp.before(timeStamp);
	}
}
