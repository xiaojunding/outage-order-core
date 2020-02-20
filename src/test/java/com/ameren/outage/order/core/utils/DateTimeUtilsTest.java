package com.ameren.outage.order.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.Clock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeUtilsTest {

	@Test
	public void testIsAfter_True() {
		long currentUtcTime = Clock.systemUTC().millis();
        Timestamp timeStamp = new Timestamp(currentUtcTime + 10000000);
		assertTrue(DateTimeUtils.isAfterCurrentUtcTime(timeStamp));
	}
	
	@Test
	public void testIsAfter_False() {
		long currentUtcTime = Clock.systemUTC().millis();
        Timestamp timeStamp = new Timestamp(currentUtcTime - 10000000);
		assertFalse(DateTimeUtils.isAfterCurrentUtcTime(timeStamp));
	}
}
