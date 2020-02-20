package com.ameren.outage.order.core.services;

import java.util.List;

import com.ameren.outage.core.model.Customer;

public interface CustomerLookupService {

	/**
	 * This method takes in premise point number as input invokes the eADMS API to
	 * getCustomerByPremise and maps the response onto a {@link Customer} object.
	 * 
	 * @param premiseNumber
	 * @return {@link Customer}
	 */
	public Customer getCustomersOnDevice(String transformerNumber, String premiseNumber);

	/**
	 * This method takes in list of transformers as input and invokes eADMS API to
	 * getCustomersOnTransformers and converts the response into list of
	 * {@link Customer} objects.
	 * 
	 * @param transformerList
	 * @return List of {@link Customer}
	 */
	public List<Customer> getCustomersOnDevice(List<String> transformerList);
}
