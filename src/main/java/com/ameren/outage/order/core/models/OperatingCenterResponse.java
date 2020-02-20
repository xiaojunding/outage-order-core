package com.ameren.outage.order.core.models;

import java.util.List;

import com.ameren.outage.core.model.PilotOperatingCenter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OperatingCenterResponse extends BaseResponse{
	@JsonProperty("Data")
	private OperatingCenters data;
	
	public List<PilotOperatingCenter> getOperatingCenters() {
		return data == null? null : data.getPilotOperatingCenters();
	}
}
