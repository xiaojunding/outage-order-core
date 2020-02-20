package com.ameren.outage.order.core.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.util.CollectionUtils;
import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.CustomerNotification;
import com.ameren.outage.core.model.OrderDevice;
import com.ameren.outage.core.model.QueueHandler;
import com.ameren.outage.core.model.QueueTask;
import com.ameren.outage.core.services.queue.QueueService;
import com.ameren.outage.order.core.entities.FilterDetail;
import com.ameren.outage.order.core.enums.FilterResult;
import com.ameren.outage.order.core.models.CallLog;
import com.ameren.outage.order.core.repositories.FilterDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This abstract class encapsulates the logic to process
 * {@link com.ameren.outage.core.model.OrderDevice}, mapping out affected
 * customers, and sending the result to next queue.
 * 
 * @author e149473
 *
 */
public abstract class AbstractOutageCustomerProcessor {

    private static final int QUEUE_FAILURE_STATUS_CODE = 400;
    private static final Logger logger = LogManager.getLogger(AbstractOutageCustomerProcessor.class);
    protected static final ObjectMapper mapper = new ObjectMapper();
    private CustomerLookupService customerLookupService;
    private QueueService queueService;
    private CallLogService callLogService;
    private FilterDetailRepository filterDetailRepository;
    private TemplateService templateService;
    
    public AbstractOutageCustomerProcessor(CustomerLookupService customerLookupService, QueueService queueService, CallLogService callLogService,
            FilterDetailRepository filterDetailRepository, TemplateService templateService) {
        this.customerLookupService = customerLookupService;
        this.queueService = queueService;
        this.callLogService = callLogService;
        this.filterDetailRepository = filterDetailRepository;
        this.templateService = templateService;
    }

    protected void process(final OrderDevice order) throws JsonProcessingException {

        // get customers
        List<Customer> customers = getCustomers(order);
        if (CollectionUtils.isNullOrEmpty(customers)) {
            throw new RuntimeException("There is no customer found");
        }
        // get call logs for the transformers
        Map<String, CallLog> callLogMap = getCallLogs(order);

        Map<String, String> placeholderValues = null;
        for (Customer customer : customers) {
            //if(null != customer.getPremise() && customer.getPremise().equalsIgnoreCase(order.getExcludePremiseNumber())) {
        	if(null != customer.getPremise() && 
        			order.getExcludePremiseList() != null &&
        			order.getExcludePremiseList().stream().anyMatch(str -> str.equalsIgnoreCase(customer.getPremise()))) {
                logger.info("premise # {} has been already contacted so excluding it from device level", customer.getPremise());
                continue;
            }
            
            FilterResult filterCode = getFilter().doFilter(customer, callLogMap);
            logger.info("Filter code returned for bill account {} is {}",customer.getBillAccount(), filterCode);
            
            //handle filtered customer
            if(!FilterResult.isSuccess(filterCode)) {
            	saveFilteredCustomer(customer, filterCode, order.getOrder().getMetadata().getOrderDetailId(),order.getNotificationType().toString());
            	continue;
            }
                        
            //handle reported customer
            if(isSpecialCaseHandled(filterCode, order, customer)) {
            	continue;
            }
            
            // not special case, go ahead notify customer
            if (placeholderValues == null) {
                placeholderValues = getNotificationTemplate(order);
            }
            publishNotification(placeholderValues, customer, order);
        }

    }

	protected Map<String, String> getNotificationTemplate(final OrderDevice order) {
		return templateService.getTemplateIdNoChannelAndMapForPlaceholderValues(order);
	}
    
    protected boolean isSpecialCaseHandled(final FilterResult filterCode, OrderDevice order, Customer customer) {
    	return false;
    }

    protected void publishNotification(Map<String, String> placeholderValues, Customer customer, OrderDevice order) {        
        templateService.addCustomerAttributes(placeholderValues, customer);
        CustomerNotification customerNotification = templateService.generateNotificationUsingOrderAndCustomer(order, customer);
        customerNotification.setKeyMap(placeholderValues);
        try {
        	final QueueTask queueTask = new QueueTask(getSendToQueueName(), mapper.writeValueAsString(customerNotification), getQueueHandler(customerNotification));
            queueService.sendSingleMessage(queueTask);
        } catch (Exception e) {
            saveFilteredCustomer(customer, FilterResult.FAILED_PUBLISH_TO_QUEUE, order.getOrder().getMetadata().getOrderDetailId(),order.getNotificationType().toString());
        }
		
	}

	protected void saveFilteredCustomer(Customer customer, FilterResult filterCode, Long outageOrderId, String notificationType) {
        try {
            FilterDetail filterEntity = new FilterDetail(customer.getBillAccount(), filterCode, outageOrderId,notificationType);
            filterDetailRepository.save(filterEntity);
        } catch (Exception e) {
            getLogger().error("Failed to persist Filter Detail");
        }
    }

    private Map<String, CallLog> getCallLogs(OrderDevice order) {
        Map<String, CallLog> callLogMap = null;
        try {
            getLogger().debug("Getting callLogs for the transformerList");
            List<CallLog> callLogs = callLogService.getCallLogsByTransformers(order.getTransformerList());
            callLogMap = new HashMap<String, CallLog>();
            for (CallLog callLog : callLogs) {
                callLogMap.put(callLog.getBillaccount(), callLog);
            }
        } catch (Exception e) {
            getLogger().error("Failed to retrieve {} call logs for transformers: {}", order.getNotificationType(), order.getTransformerList().toString());
        }

        return callLogMap;
    }

    private List<Customer> getCustomers(final OrderDevice order) {
        if (order == null) {
            getLogger().error("Order is null");
            return null;
        } else {
            getLogger().debug("process order number:{}", order.getOrder() == null ? "null" : order.getOrder().getOrderNumber());
        }

        if (order.getPremiseNumber() != null) {
            getLogger().debug("Getting customer on premise");
            return getCustomerByPremiseNumber(order);
        } else if (!CollectionUtils.isNullOrEmpty(order.getTransformerList())) {
            getLogger().debug("Getting customers on transformers");
            return getCustomersByTransformers(order);
        } else {
            getLogger().error("There is no device or premiseNumber in the order number:{}", order.getOrder() == null ? "null" : order.getOrder().getOrderNumber());
            return null;
        }
    }

    private List<Customer> getCustomerByPremiseNumber(final OrderDevice order) {

        Customer customer = customerLookupService.getCustomersOnDevice(order.getTransformerList().get(0), order.getPremiseNumber());
        if (customer == null) {
            getLogger().error("There is no customer found for {}", order.getPremiseNumber());
            return null;
        } else {
            return Arrays.asList(customer);
        }
    }

    private List<Customer> getCustomersByTransformers(final OrderDevice order) {

        List<Customer> customersByTransformers = customerLookupService.getCustomersOnDevice(order.getTransformerList());
        if (CollectionUtils.isNullOrEmpty(customersByTransformers)) {
            getLogger().debug("There is no customer returned for {}", Arrays.toString(order.getTransformerList().toArray()));
            return null;
        }
        return customersByTransformers;
    }

    /**
     * Retrieve [{@link com.ameren.outage.core.model.QueueHandler} after queue
     * operation returned. Here provide default handler for logging information or
     * attempt to retry, but sub-classes might override with specific actions.
     * 
     * @param {@link CustomerNotification}
     * @return QueueHandler implementation
     */
    protected QueueHandler getQueueHandler(final CustomerNotification notify) {
        return new QueueHandler() {

            @Override
            public void onSuccess(QueueTask queueTask) {
                if (queueTask.getResult().getStatusCode() < QUEUE_FAILURE_STATUS_CODE) {
                    getLogger().debug("Successfully publish for following customer:{}", notify.getCustomer().getBillAccount());
                } else {
                    throw new RuntimeException("Failed to publish affected customer");
                }
            }

            @Override
            public void onError(QueueTask t) {
                getLogger().error("Failed to publish for following cusotmer: {}", notify.getCustomer().getBillAccount());
                throw new RuntimeException("Failed to publish affected customer");
            }

        };
    }

    protected abstract Logger getLogger();

    protected abstract CustomerFilter getFilter();

    protected abstract String getSendToQueueName();
}
