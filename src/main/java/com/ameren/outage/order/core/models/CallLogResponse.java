package com.ameren.outage.order.core.models;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CallLogResponse extends BaseResponse{
	
	@JsonProperty("Data")
	private Calls data;
	
	public List<CallLog> getCallLogs() {
		return data == null? Collections.emptyList() :data.getCallLogs();
	}
}