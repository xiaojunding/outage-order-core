package com.ameren.outage.order.core.services;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.Order;
import com.ameren.outage.core.model.OrderDevice;
import com.ameren.outage.core.services.queue.QueueService;
import com.ameren.outage.order.core.enums.FilterResult;
import com.ameren.outage.order.core.models.CallLog;
import com.ameren.outage.order.core.repositories.FilterDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class AbstractOutageCustomerProcessorTest {

    private AbstractOutageCustomerProcessor processor;

    @Mock
    private CustomerLookupService customerLookupService;
    @Mock
    private QueueService queueService;
    @Mock
    private CallLogService callLogService;
    @Mock
    private FilterDetailRepository filterDetailRepository;
    @Mock
    private TemplateService templateService;

    @Before
    public void setUp(){
        processor = Mockito.mock(AbstractOutageCustomerProcessor.class, Mockito.CALLS_REAL_METHODS);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @Ignore
    public void test_process() throws JsonProcessingException {
        // arrange
        OrderDevice orderDeviceMock = Mockito.mock(OrderDevice.class);
        Order orderMock = Mockito.mock(Order.class);
        Customer customerMock = Mockito.mock(Customer.class);
        CustomerFilter filterMock = Mockito.mock(CustomerFilter.class);
        Logger loggerMock = Mockito.mock(Logger.class);
        List callLogMock = new ArrayList();
        callLogMock.add(new CallLog());
        
        when(orderDeviceMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getOrderNumber()).thenReturn("ordernumber");
        when(orderDeviceMock.getPremiseNumber()).thenReturn("premiseNumber");
        when(orderDeviceMock.getTransformerList().get(0)).thenReturn("transformerNumber");
        
        when(processor.getLogger()).thenReturn(loggerMock);
        when(processor.getFilter()).thenReturn(filterMock);
        when(customerLookupService.getCustomersOnDevice("transformerNumber", "premiseNumber")).thenReturn(customerMock);
        when(callLogService.getCallLogsByTransformers(orderDeviceMock.getTransformerList())).thenReturn(callLogMock);
        when(filterMock.doFilter(Mockito.any(Customer.class), Mockito.any(Map.class))).thenReturn(FilterResult.SUCCESS);

        // act
        //processor.process(orderDeviceMock);
    }

}
