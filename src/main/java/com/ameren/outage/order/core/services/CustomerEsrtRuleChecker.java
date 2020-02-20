package com.ameren.outage.order.core.services;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.order.core.enums.FilterResult;
import com.ameren.outage.order.core.models.CallLog;
import com.ameren.outage.order.core.utils.RegexChecker;

@Component
public class CustomerEsrtRuleChecker implements CustomerFilter{
	private static final Logger logger = LogManager.getLogger(CustomerEsrtRuleChecker.class);
	private RegexChecker phoneNumberChecker;
	
	@Autowired
	public CustomerEsrtRuleChecker(RegexChecker phoneNumberChecker) {
		this.phoneNumberChecker = phoneNumberChecker;
	}
	
	@Override
	public FilterResult doFilter(Customer customer, Map<String, CallLog> callLogMap) {
		
		if (callLogMap == null || callLogMap.get(customer.getBillAccount()) == null) {
		    logger.info("doFilter without call log");
			return checkWithoutCallLog(customer);
		} else {
		    logger.info("doFilter with call log");
			return checkWithCallLog(customer, callLogMap);
		}
	}

	private FilterResult checkWithCallLog(Customer customer, Map<String, CallLog> callLogMap) {
		CallLog callLog = callLogMap.get(customer.getBillAccount());
		if (!callLog.isOkToCallBack()) {
			return FilterResult.NOT_OK_TO_CALL_BACK;
		}

		if (isRegistered(customer)) {
			return FilterResult.CUSTOMER_REGISTERED;
		}

		if (phoneNumberChecker.test(callLog.getAltPhoneNumber())) {
			customer.setAltPhone(callLog.getAltPhoneNumber());
			return FilterResult.CUSTOMER_VOICE_ELIGIBLE;
		}

		if (phoneNumberChecker.test(customer.getPhone())) {
			return FilterResult.CUSTOMER_VOICE_ELIGIBLE;
		} 
		
		return FilterResult.OK_INVALID_ALT_PRIMARY_PHONE;
	}

	private FilterResult checkWithoutCallLog(Customer customer) {
		
	    
		if (isRegistered(customer)) {
			return FilterResult.CUSTOMER_REGISTERED;
		}

		boolean isValid = phoneNumberChecker.test(customer.getPhone());
		// TODO remove this logger
        logger.info("checkWithoutCallLog for Customer with phone {} is {} ", customer.getPhone(), isValid);
		if (isValid) {
			return FilterResult.CUSTOMER_VOICE_ELIGIBLE;
		}
		
		return FilterResult.INVALID_PRIMARY_PHONE;
	}

	private boolean isRegistered(Customer customer) {
		return customer.getRegAlerts() != null ? customer.getRegAlerts().booleanValue() : false;
	}

	public RegexChecker getPhoneNumberChecker() {
		return this.phoneNumberChecker;
	}

}
