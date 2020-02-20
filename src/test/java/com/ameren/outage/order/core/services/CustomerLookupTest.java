package com.ameren.outage.order.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.OrderDevice;
import com.ameren.outage.order.core.config.TestConfig;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class CustomerLookupTest {

    @Autowired
    private CustomerLookupService customerLookupService;

    private ObjectMapper mapper = new ObjectMapper();

    @Ignore
    @Test
    public void testGetCustomersOnDevicesUsingOrderDevicePayload() throws JsonParseException, JsonMappingException, IOException {
        OrderDevice order = mapper.readValue(this.getClass().getClassLoader().getResourceAsStream("orderDeviceForCustomersOnDevice.json"), OrderDevice.class);

        List<Customer> customersOnDevice = customerLookupService.getCustomersOnDevice(order.getTransformerList());

        assertNotNull(customersOnDevice);
        assertEquals(1148, customersOnDevice.size());
    }
}
