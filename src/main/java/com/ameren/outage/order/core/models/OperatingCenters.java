package com.ameren.outage.order.core.models;

import java.util.List;

import com.ameren.outage.core.model.PilotOperatingCenter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OperatingCenters {
	
	@JsonProperty("opCenters")
    private List<PilotOperatingCenter> pilotOperatingCenters;

	List<PilotOperatingCenter> getPilotOperatingCenters() {
		return pilotOperatingCenters;
	}

}
