package com.ameren.outage.order.core.services;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.util.CollectionUtils;
import com.ameren.outage.api.config.OrderCoreConfig;
import com.ameren.outage.core.model.PilotOperatingCenter;
import com.ameren.outage.order.core.models.OperatingCenterResponse;

public abstract class AbstractOperatingCenterServiceImpl implements OperatingCenterService {

    private static Logger logger = LogManager.getLogger(AbstractOperatingCenterServiceImpl.class);

    private RestTemplate restTemplate;
    protected OrderCoreConfig config;

    @Autowired
    public AbstractOperatingCenterServiceImpl(RestTemplate restTemplate, OrderCoreConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @Override
    public void updateVoiceEligibleOperatingCenters() {

        Set<String> voiceOperatingCenterCodes = null;
        try {
            ResponseEntity<OperatingCenterResponse> response = restTemplate.getForEntity(config.getVoiceOperatingCenterUrl(), OperatingCenterResponse.class);
            voiceOperatingCenterCodes = new HashSet<String>();

            if (response != null && response.getBody() != null && !CollectionUtils.isNullOrEmpty(response.getBody().getOperatingCenters())) {
                for (PilotOperatingCenter oc : response.getBody().getOperatingCenters()) {
                    voiceOperatingCenterCodes.add(oc.getOpCenterCode());
                }
            }
            config.setVoiceOperatingCenters(voiceOperatingCenterCodes);
        } catch (Exception ex) {
            logger.error("Failed to retrieve Voice Operating Centers:", ex);
            throw ex;
        }
    }

}
