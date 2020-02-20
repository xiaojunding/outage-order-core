package com.ameren.outage.order.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ameren.outage.api.config.OrderCoreConfig;
import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.CustomerLookupResponseModel;
import com.ameren.outage.core.model.LookupByTransformerRequestModel;

public class CustomerLookupServiceImpl implements CustomerLookupService {

    private Logger log = LogManager.getLogger(CustomerLookupServiceImpl.class);

    private RestTemplate restTemplate;
    private OrderCoreConfig config;

    public CustomerLookupServiceImpl(RestTemplate restTemplate, OrderCoreConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public Customer getCustomersOnDevice(final String transformerNumber, final String premiseNumber) {
        Map<String, Object> uriParameters = new HashMap<>();
        uriParameters.put("transformerNumber", transformerNumber);
        UriComponentsBuilder fromHttpUrl = UriComponentsBuilder.fromHttpUrl(config.getCustomerByPremiseUrl());
        fromHttpUrl.uriVariables(uriParameters);
        fromHttpUrl.queryParam("premiseNumber", premiseNumber);

        ResponseEntity<CustomerLookupResponseModel> response = restTemplate.exchange(fromHttpUrl.toUriString(), HttpMethod.GET, new HttpEntity<>(config.getHeaderMap()),
                CustomerLookupResponseModel.class);

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            CustomerLookupResponseModel responseBody = response.getBody();
            if (null != responseBody && null != responseBody.getCustomers() && 1 == responseBody.getCustomers().size()) {
                return responseBody.getCustomers().get(0);
            } else {
                log.info("GetCustmerByPremise Returned more than one customer for the premise returning only one please check premiseNumber::" + premiseNumber);
                return responseBody.getCustomers().get(0);
            }
        }
        return null;
    }

    public List<Customer> getCustomersOnDevice(final List<String> transformerList) {
        ArrayList<Customer> customerList = new ArrayList<Customer>();
        int maxNoOfTransformers = config.getMaxNoOfTransformers();
        int maxNoOfSplits = (transformerList.size() / maxNoOfTransformers) + 1;
        if (transformerList.size() > maxNoOfTransformers) {
            for (int noOfSplits = 0; noOfSplits < maxNoOfSplits;) {
                List<String> subList = transformerList.subList(noOfSplits++ * maxNoOfTransformers,
                        (noOfSplits * maxNoOfTransformers) > transformerList.size() ? transformerList.size() : (noOfSplits * maxNoOfTransformers));
                callGetCustomerByTransformer(customerList, subList);
            }
        } else {
            callGetCustomerByTransformer(customerList, transformerList);
        }
        return customerList;
    }

    private void callGetCustomerByTransformer(ArrayList<Customer> customerList, List<String> subList) {
        LookupByTransformerRequestModel requestModel = new LookupByTransformerRequestModel();
        requestModel.setTransformerFromList(subList);
        HttpEntity<LookupByTransformerRequestModel> httpEntity = new HttpEntity<LookupByTransformerRequestModel>(requestModel, config.getHeaderMap());
        ResponseEntity<CustomerLookupResponseModel> response;
		try {
			response = restTemplate.exchange(config.getCustomerByTransformerUrl(), HttpMethod.POST, httpEntity,
			        CustomerLookupResponseModel.class);
			if (null != response && HttpStatus.OK.equals(response.getStatusCode())) {
	            CustomerLookupResponseModel responseBody = response.getBody();
	            if (null != responseBody && null != responseBody.getCustomers()) {
	                customerList.addAll(responseBody.getCustomers());
	            }
	        }
		} catch (RestClientException e) {
			log.error("Failted with error: {}", e.getLocalizedMessage());
		}
        
    }

}
