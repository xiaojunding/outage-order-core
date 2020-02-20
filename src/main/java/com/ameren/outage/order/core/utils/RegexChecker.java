package com.ameren.outage.order.core.utils;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegexChecker implements Predicate<String>{
	
	private final Pattern validatePattern;
    private static final Logger logger = LogManager.getLogger(RegexChecker.class);
	
	public RegexChecker(final String regex) {
		validatePattern = Pattern.compile(regex);
	}
	
	
	@Override
	public boolean test(String toBeChecked) {
	    logger.info("validating {} against regular expression {}",toBeChecked,  validatePattern.pattern());
		return toBeChecked == null? false: validatePattern.matcher(toBeChecked).matches();
	}
	
}
