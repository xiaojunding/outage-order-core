package com.ameren.outage.order.core.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ameren.outage.api.config.OrderCoreConfig;
import com.ameren.outage.order.core.models.CallLog;
import com.ameren.outage.order.core.models.CallLogResponse;
import com.google.common.collect.Lists;

public class CallLogServiceImpl implements CallLogService {

	private static Logger logger = LogManager.getLogger(CallLogServiceImpl.class);

	private RestTemplate restTemplate;
	private OrderCoreConfig config;
	
	public CallLogServiceImpl(final OrderCoreConfig config, final RestTemplate restTemplate) {
	    this.restTemplate = restTemplate;
	    this.config = config;
	}

	@Override
	public List<CallLog> getCallLogsByTransformers(List<String> transformers) {
		List<CallLog> callLogs = new ArrayList<>();

		if (transformers == null || transformers.isEmpty()) {
			logger.info("Getting Call Logs by Transforms with no transformers");
			return callLogs;
		}
		
		List<List<String>> splitedTransformers = Lists.partition(transformers, config.getMaxNoOfTransformers());
		for(List<String> subList: splitedTransformers) {
			callGetCallLogsByTransformers(callLogs, subList);
		}

		return callLogs;
	}

	private void callGetCallLogsByTransformers(List<CallLog> callLogs, List<String> transformers) {
		final String serviceUrl = getServiceUrl(transformers);
		ResponseEntity<CallLogResponse> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(config.getHeaderMap()), CallLogResponse.class);;

		if (null != response && HttpStatus.OK.equals(response.getStatusCode()) && response.getBody() != null) {
			callLogs.addAll(response.getBody().getCallLogs());
		}
	}

	private String getServiceUrl(final List<String> transformers) {
		StringBuilder transformerBuilder = new StringBuilder();
		for (String transformer : transformers) {
			transformerBuilder.append(transformer).append(",");
		}
		StringBuilder urlBuilder = new StringBuilder().append(config.getCallLogServiceUrl())
				.append(transformerBuilder.toString().substring(0, transformerBuilder.length() - 1));
		final String serviceUrl = urlBuilder.toString();
		logger.debug("call logs service url is " + serviceUrl);
		return serviceUrl;
	}
}
