package com.ameren.outage.order.core.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ameren.outage.api.config.OrderCoreConfig;
import com.ameren.outage.order.core.models.CallLog;
import com.ameren.outage.order.core.models.CallLogResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class CallLogServiceTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private CallLogServiceImpl service;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OrderCoreConfig config;

    @Test
    public void test_nullTransformers() throws IOException {
        // act
        List<CallLog> callLogs = service.getCallLogsByTransformers(null);

        // verify
        assertEquals(0, callLogs.size());

    }

    @Test
    public void test_emptyTransformers() throws IOException {
        // act
        List<CallLog> callLogs = service.getCallLogsByTransformers(new ArrayList<String>());

        // verify
        assertEquals(0, callLogs.size());
    }

    @Test(expected = RestClientException.class)
    public void test_serviceFailed() throws IOException {
        // prepare
    	when(config.getMaxNoOfTransformers()).thenReturn(2);
        lenient().when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.eq(CallLogResponse.class))).thenThrow(new RestClientException("test"));

        // act
        List<CallLog> callLogs = service.getCallLogsByTransformers(Arrays.asList("t1", "t2"));

        // verify
        assertEquals(0, callLogs.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_serviceSucceed() throws IOException {
    	when(config.getMaxNoOfTransformers()).thenReturn(2);
        // prepare
        String json = new String(Files.readAllBytes(new File("src/test/resources/call_logs.json").toPath()));
        CallLogResponse data = mapper.readValue(json, CallLogResponse.class);
        ResponseEntity<CallLogResponse> response = new ResponseEntity<CallLogResponse>(data, HttpStatus.OK);
        when(config.getHeaderMap()).thenReturn(Mockito.mock(MultiValueMap.class));

        when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.eq(CallLogResponse.class))).thenReturn(response);
        List<String> transformers = Arrays.asList("t1", "t2");

        // act
        List<CallLog> callLogs = service.getCallLogsByTransformers(transformers);

        // verify
        assertEquals(11, callLogs.size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_serviceSucceed_mulitpleCalls() throws IOException {
    	when(config.getMaxNoOfTransformers()).thenReturn(2);
        // prepare
        String json = new String(Files.readAllBytes(new File("src/test/resources/call_logs.json").toPath()));
        CallLogResponse data = mapper.readValue(json, CallLogResponse.class);
        ResponseEntity<CallLogResponse> response = new ResponseEntity<CallLogResponse>(data, HttpStatus.OK);
        when(config.getHeaderMap()).thenReturn(Mockito.mock(MultiValueMap.class));

        when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.eq(CallLogResponse.class))).thenReturn(response);
        List<String> transformers = Arrays.asList("t1", "t2", "t3", "t4");

        // act
        List<CallLog> callLogs = service.getCallLogsByTransformers(transformers);

        // verify
        assertEquals(22, callLogs.size());
    }
}
