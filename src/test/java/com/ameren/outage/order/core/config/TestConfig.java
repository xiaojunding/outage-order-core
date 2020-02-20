package com.ameren.outage.order.core.config;

import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ameren.outage.api.config.OrderCoreConfig;
import com.ameren.outage.order.core.services.CustomerLookupService;
import com.ameren.outage.order.core.services.CustomerLookupServiceImpl;

@Component
public class TestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OrderCoreConfig orderCoreConfig() {
        OrderCoreConfig mock = Mockito.mock(OrderCoreConfig.class);
        when(mock.getCustomerByTransformerUrl()).thenReturn("https://hawscorpd.ameren.com:8443/sys/eadms/account/lookup/v1/customerByTransformers/");
        when(mock.getMaxNoOfTransformers()).thenReturn(500);
        return mock;
    }
    
    @Bean
    public CustomerLookupService customerLookupService() {
        return new CustomerLookupServiceImpl(restTemplate(), orderCoreConfig());
    }
}
