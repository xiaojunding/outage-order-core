package com.ameren.outage.order.core.services;

import java.util.List;

import com.ameren.outage.order.core.models.CallLog;

public interface CallLogService {
	/**
	 * Retrieve a list of {@link CallLog} for the given transformers
	 * @param transformerList
	 * @return
	 */
	List<CallLog> getCallLogsByTransformers(List<String> transformerList);
}
