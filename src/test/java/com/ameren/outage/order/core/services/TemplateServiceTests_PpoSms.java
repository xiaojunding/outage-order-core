package com.ameren.outage.order.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.OrderDevice;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TemplateService.class)
public class TemplateServiceTests_PpoSms {

    @Autowired
    private TemplateService service;

    private ObjectMapper mapper = new ObjectMapper();
    private Timestamp futureEsrt;

    private OrderDevice orderDevice;
    private Customer customer;

    @Before
    public void setUp() throws JsonParseException, JsonMappingException, IOException {
        // here we load the notification object from a base file
        // the arrange in each test modifies the object for the specific test
        orderDevice = mapper.readValue(this.getClass().getClassLoader().getResourceAsStream("PPO_NotificationType.json"), OrderDevice.class);
        customer = new Customer();
        customer.setBillAccount("111111");
        customer.setAddress("1901 CHOUTEAU AVE");
        // futureEsrt will be used in the test cases that require a future or not
        // expired esrt
        futureEsrt = new Timestamp(System.currentTimeMillis() + 60 * 60 * 1000);
    }

    @Test
    public void test_ppo_no_esrt_no_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setCauseCode("");
        orderDevice.getOrder().setCauseCodeDescription("");
        orderDevice.getOrder().setEsrt(null);

        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNull(objectMap.get("cause"));
        assertEquals("ppo", objectMap.get("key"));
    }

    @Test
    public void test_ppo_auto_no_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(true);
        orderDevice.getOrder().setCauseCode("");
        orderDevice.getOrder().setCauseCodeDescription("");
        orderDevice.getOrder().setEsrt(futureEsrt);

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNull(objectMap.get("cause"));
        assertEquals("ppo.auto", objectMap.get("key"));
    }

    @Test
    public void test_ppo_manual_no_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(false);
        orderDevice.getOrder().setCauseCode("");
        orderDevice.getOrder().setCauseCodeDescription("");
        orderDevice.getOrder().setEsrt(futureEsrt);

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNull(objectMap.get("cause"));
        assertEquals("ppo.manual", objectMap.get("key"));
    }

    @Test
    public void test_ppo_auto_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(true);
        orderDevice.getOrder().setEsrt(futureEsrt);

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNotNull(objectMap.get("cause"));
        assertEquals("ppo.auto.cause", objectMap.get("key"));
    }

    @Test
    public void test_ppo_manual_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(false);
        orderDevice.getOrder().setEsrt(futureEsrt);

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNotNull(objectMap.get("cause"));
        assertEquals("ppo.manual.cause", objectMap.get("key"));
    }

    @Test
    public void test_ppo_manual_expired_no_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(false);
        orderDevice.getOrder().setCauseCode("");
        orderDevice.getOrder().setCauseCodeDescription("");

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNull(objectMap.get("cause"));
        assertEquals("ppo.manual.expired", objectMap.get("key"));
    }

    @Test
    public void test_ppo_auto_expired_no_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(true);
        orderDevice.getOrder().setCauseCode("");
        orderDevice.getOrder().setCauseCodeDescription("");

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNull(objectMap.get("cause"));
        assertEquals("ppo.auto.expired", objectMap.get("key"));
    }

    @Test
    public void test_ppo_manual_expired_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(false);

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNotNull(objectMap.get("cause"));
        assertEquals("ppo.manual.expired.cause", objectMap.get("key"));
    }

    @Test
    public void test_ppo_auto_expired_cause() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        orderDevice.getOrder().setAutoEsrt(true);

        // act
        Map<String, String> objectMap = service.getTemplateIdNoChannelAndMapForPlaceholderValues(orderDevice);

        // assert
        assertNotNull(objectMap);
        assertNotNull(objectMap.get("key"));
        assertNotNull(objectMap.get("cause"));
        assertEquals("ppo.auto.expired.cause", objectMap.get("key"));
    }
}
