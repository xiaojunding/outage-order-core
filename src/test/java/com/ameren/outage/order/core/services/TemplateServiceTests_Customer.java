package com.ameren.outage.order.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.CustomerNotification;
import com.ameren.outage.core.model.Order;
import com.ameren.outage.core.model.OrderDevice;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TemplateService.class)
public class TemplateServiceTests_Customer {

    @Autowired
    private TemplateService service;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void test_customer_object_is_set() {

        // arrange
        OrderDevice orderDevice = Mockito.mock(OrderDevice.class);
        Order order = Mockito.mock(Order.class);
        Customer customer = Mockito.mock(Customer.class);
        String billAccount = "billAccount";

        when(orderDevice.getOrder()).thenReturn(order);
        when(customer.getBillAccount()).thenReturn(billAccount);
        
        // act
        CustomerNotification customerNotification = service.generateNotificationUsingOrderAndCustomer(orderDevice, customer);
        
        // assert
        assertNotNull(customerNotification.getCustomer());
        assertEquals(billAccount, customerNotification.getCustomer().getBillAccount());
    }
    
    @Test
    public void test_translate_esrt_to_AmericaChicago() throws JsonParseException, JsonMappingException, IOException {
    	OrderDevice device = mapper.readValue(this.getClass().getClassLoader().getResourceAsStream("orderDeviceForTimestamp.json"), OrderDevice.class);
    	String ts_str = service.translateEsrt(device.getOrder().getEsrt());
    	assertEquals("01:00 PM on Sep 11", ts_str);
    }

}
