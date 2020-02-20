package com.ameren.outage.order.core.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.sqs.AmazonSQS;
import com.ameren.outage.api.config.CoreConfig;

@Component
public class MockedCoreConfig {
	
	public MockedCoreConfig() {
	    new CoreConfig();
	}
	
	@Bean
    public AmazonSQS amazonSQS() {
        return Mockito.mock(AmazonSQS.class);
    }
	
	@Bean
	public RestTemplate restTemplate() {
		return Mockito.mock(RestTemplate.class);
	}

}
