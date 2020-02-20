package com.ameren.outage.order.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegexCheckerTest {

	private String regex = "\\d{4}";
	
	private RegexChecker checker = new RegexChecker(regex);
	
	@Test
	public void testNull() {
		assertFalse(checker.test(null));
	}
	
	@Test
	public void testMatch() {
		assertTrue(checker.test("1234"));
	}
	
	@Test
	public void testNotMatch() {
		assertFalse(checker.test("123"));
	}
}
