package com.ameren.outage.order.core.services;

public interface OperatingCenterService {
	/**
	 * Retrieve operating center codes for voice PPO eligible notification
	 * @return Set of operating center codes 
	 */
	public void updateVoiceEligibleOperatingCenters();
}
