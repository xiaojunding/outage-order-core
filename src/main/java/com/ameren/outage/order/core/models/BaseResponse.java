package com.ameren.outage.order.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {
	@JsonProperty("StatusCode")
	private long statusCode;

	@JsonProperty("HasError")
	private boolean hasError;

	@JsonProperty("Message")
	private String message;
	
	public long getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public boolean hasError() {
		return hasError;
	}
}
