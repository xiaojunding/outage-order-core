package com.ameren.outage.order.core.services;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ameren.outage.core.model.Customer;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TemplateService.class)
public class TemplateServiceTests_addCustomerAttributes {

    @Autowired
    private TemplateService service;
    
	@Test
	public void test_customer_attributes_are_added() {
		//arrange
		Customer customer = new Customer();
		customer.setBillAccount("2222");
		customer.setAddress("address2");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "ppo.manual.cause");
		map.put("esrt", "esrt");
		map.put("cause", "cause");
		map.put("account-number-ending", "1111");
		map.put("address", "address1");
		
		//act
		service.addCustomerAttributes(map, customer);
		
		//assert
		assertEquals(5, map.size());
		assertEquals("ppo.manual.cause", map.get("key"));
		assertEquals("esrt", map.get("esrt"));
		assertEquals("cause", map.get("cause"));
		assertEquals("2222", map.get("account-number-ending"));
		assertEquals("address2", map.get("address"));
	}
	
	@Test
	public void test_customer_attributes_bill_account_is_null() {
		//arrange
		Customer customer = new Customer();
		customer.setBillAccount(null);
		customer.setAddress("address2");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "ppo.manual.cause");
		map.put("esrt", "esrt");
		map.put("cause", "cause");
		map.put("account-number-ending", "1111");
		map.put("address", "address1");
		
		//act
		service.addCustomerAttributes(map, customer);
		
		//assert
		assertEquals(4, map.size());
		assertEquals("ppo.manual.cause", map.get("key"));
		assertEquals("esrt", map.get("esrt"));
		assertEquals("cause", map.get("cause"));
		assertEquals(null, map.get("account-number-ending"));
		assertEquals("address2", map.get("address"));
	}

}
