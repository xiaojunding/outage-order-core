package com.ameren.outage.order.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.ameren.outage.api.config.OrderCoreConfig;
import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.CustomerLookupResponseModel;
import com.ameren.outage.core.model.LookupByTransformerRequestModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class CustomerLookupServiceImplTests {

    @InjectMocks
    private CustomerLookupServiceImpl customerLookupService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OrderCoreConfig config;

    private ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        when(config.getMaxNoOfTransformers()).thenReturn(2);
        when(config.getHeaderMap()).thenReturn(Mockito.mock(MultiValueMap.class));
        when(config.getCustomerByPremiseUrl()).thenReturn("http://test");
        when(config.getCustomerByTransformerUrl()).thenReturn("http://test");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), Mockito.any(HttpEntity.class), eq(CustomerLookupResponseModel.class)))
                .thenAnswer(new Answer<ResponseEntity<CustomerLookupResponseModel>>() {
                    @SuppressWarnings("rawtypes")
                    @Override
                    public ResponseEntity<CustomerLookupResponseModel> answer(InvocationOnMock invocation) throws Throwable {
                        InputStream json = null;
                        if (((LookupByTransformerRequestModel) ((HttpEntity) invocation.getArgument(2)).getBody()).getTransformers().size() == 1) {
                            json = this.getClass().getClassLoader().getResourceAsStream("customerOnTransformerResponse.json");
                        } else if (((LookupByTransformerRequestModel) ((HttpEntity) invocation.getArgument(2)).getBody()).getTransformers().size() == 2) {
                            json = this.getClass().getClassLoader().getResourceAsStream("customersOnTwoTransformersResponse.json");
                        }
                        CustomerLookupResponseModel response = mapper.readValue(json, CustomerLookupResponseModel.class);

                        ResponseEntity<CustomerLookupResponseModel> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
                        return responseEntity;
                    }
                });
    }

    @Test
    public void testGetCustomersOnPremise() throws JsonParseException, JsonMappingException, IOException {
        // arrange
        String premiseNumber = "413722934";
        String transformerNumber = "1234567";

        Map<String, Object> uriParameters = new HashMap<>();
        uriParameters.put("transformerNumber", transformerNumber);
        
        InputStream json = this.getClass().getClassLoader().getResourceAsStream("customerByPremiseResponse.json");
        CustomerLookupResponseModel response = mapper.readValue(json, CustomerLookupResponseModel.class);

        ResponseEntity<CustomerLookupResponseModel> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), Mockito.any(HttpEntity.class), eq(CustomerLookupResponseModel.class)))
                .thenReturn(responseEntity);

        // act
        Customer customerOnPremise = customerLookupService.getCustomersOnDevice(transformerNumber, premiseNumber);

        // assert
        assertNotNull(customerOnPremise);
        assertEquals(true, customerOnPremise.isActive());
        assertEquals("4104513293", customerOnPremise.getBillAccount());
        assertEquals("2177352759", customerOnPremise.getPhone());
        assertEquals("720 OKLAHOMA AVE", customerOnPremise.getAddress());
        assertEquals("LIC", customerOnPremise.getOperatingCenter());
        assertEquals("32010705007", customerOnPremise.getTransformerNumber());

    }

    @Test
    public void testGetCustomersOnTransformer() throws JsonParseException, JsonMappingException, IOException {

        // arrange
        ArrayList<String> listOfTransformers = new ArrayList<String>();
        listOfTransformers.add("02070260007");
        LookupByTransformerRequestModel requestModel = new LookupByTransformerRequestModel();
        requestModel.setTransformerFromList(listOfTransformers);

        // act
        List<Customer> customers = customerLookupService.getCustomersOnDevice(listOfTransformers);

        // assert
        assertNotNull(customers);
        assertEquals(5, customers.size());

    }

    @Test
    public void testGetCustomersOnTransformerMoreThanMaxAllowedInAReq() throws JsonParseException, JsonMappingException, IOException {

        // arrange
        ArrayList<String> listOfTransformers = new ArrayList<String>();
        listOfTransformers.add("32010705007");
        listOfTransformers.add("01141010002");
        listOfTransformers.add("02070260007");
        LookupByTransformerRequestModel requestModel = new LookupByTransformerRequestModel();
        requestModel.setTransformerFromList(listOfTransformers);

        // act
        List<Customer> customers = customerLookupService.getCustomersOnDevice(listOfTransformers);

        // assert
        assertNotNull(customers);
        assertEquals(13, customers.size());

    }
    
    @Test
    public void testGetCustomersOnTransformer_throwException() throws JsonParseException, JsonMappingException, IOException {
    	setupException();
        // arrange
        ArrayList<String> listOfTransformers = new ArrayList<String>();
        listOfTransformers.add("32010705007");
        listOfTransformers.add("01141010002");
        listOfTransformers.add("02070260007");
        LookupByTransformerRequestModel requestModel = new LookupByTransformerRequestModel();
        requestModel.setTransformerFromList(listOfTransformers);

        // act
        List<Customer> customers = customerLookupService.getCustomersOnDevice(listOfTransformers);

        // assert
        assertNotNull(customers);
        assertEquals(13, customers.size());

    }

	private void setupException() {
		when(config.getMaxNoOfTransformers()).thenReturn(2);
        when(config.getHeaderMap()).thenReturn(Mockito.mock(MultiValueMap.class));
        when(config.getCustomerByPremiseUrl()).thenReturn("http://test");
        when(config.getCustomerByTransformerUrl()).thenReturn("http://test");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), Mockito.any(HttpEntity.class), eq(CustomerLookupResponseModel.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
		
	}

}
