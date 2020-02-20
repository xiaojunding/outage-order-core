package com.ameren.outage.order.core.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Calls {
	
	@JsonProperty("calls")
	private List<CallLog> callLogs;

	List<CallLog> getCallLogs() {
		return callLogs;
	}
}
