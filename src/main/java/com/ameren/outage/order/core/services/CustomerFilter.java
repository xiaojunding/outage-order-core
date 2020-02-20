package com.ameren.outage.order.core.services;

import java.util.Map;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.order.core.enums.FilterResult;
import com.ameren.outage.order.core.models.CallLog;

/**
 * Interface for filtering customer
 */
public interface CustomerFilter {

	
	default FilterResult doFilter(Customer customer, Map<String, CallLog> callLogMap) {
		return FilterResult.SUCCESS;
	}
}