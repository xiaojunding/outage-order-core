package com.ameren.outage.order.core.services;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import com.ameren.outage.core.model.Customer;
import com.ameren.outage.core.model.CustomerNotification;
import com.ameren.outage.core.model.OrderDevice;

public class TemplateService {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("hh:mm aa 'on' MMM dd");
    private Logger logger = LogManager.getLogger(TemplateService.class);

    public Map<String, String> getTemplateIdNoChannelAndMapForPlaceholderValues(final OrderDevice orderDevice) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder builder = new StringBuilder();
        // Notification Type
        if (orderDevice.getNotificationType() == null) {
            return map;
        }
        builder.append(orderDevice.getNotificationType().value().toLowerCase());

        // ESRT
        Timestamp esrt = orderDevice.getOrder().getEsrt();
        if (null != orderDevice.getOrder() && null != esrt) {
            map.put("esrt", translateEsrt(esrt));

            // AUTO vs MANUAL
            if (orderDevice.getOrder().getAutoEsrt()) {
                builder.append(".").append("auto");
            } else {
                builder.append(".").append("manual");
            }

            // EXPIRED
            long currentUtcTime = Clock.systemUTC().millis();
            Timestamp currentUtcTimeStamp = new Timestamp(currentUtcTime);
            boolean hasEsrtExpired = currentUtcTimeStamp.after(esrt);
            logger.info("Current UTC time :{} And ESRT value:{} Comaprison Result: {}", currentUtcTimeStamp, esrt, hasEsrtExpired);
            if (hasEsrtExpired) {
                builder.append(".").append("expired");
            }
        }

        // CAUSE CODE
        if (!StringUtils.isEmpty(orderDevice.getOrder().getCauseCodeDescription())) {
            map.put("cause", orderDevice.getOrder().getCauseCodeDescription());
            builder.append(".").append("cause");
        }

        // OUTAGE SIZE
        map.put("outage-size", Integer.toString(orderDevice.getOrder().getOutageSize()));

        // STATE
        map.put("state", orderDevice.getState().value());

        // Template Id : Key without channel
        map.put("key", builder.toString());

        return map;
    }

    public void addCustomerAttributes(Map<String, String> map, Customer customer) {
        map.remove("account-number-ending");
        map.remove("address");

        // Customer Specific attributes
        if (customer.getBillAccount() != null) {
            map.put("account-number-ending", customer.getBillAccount().substring(customer.getBillAccount().length() - 4));
        }

        map.put("address", customer.getAddress());
    }

    public String translateEsrt(Timestamp esrt) {
    	SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Chicago")));
    	String formattedValue = SIMPLE_DATE_FORMAT.format(esrt);
    	logger.info("Timestamp with time zone ESRT :{} and formatted as America/Chicago ESRT {}", esrt, formattedValue);
    	return formattedValue;
    }

    public CustomerNotification generateNotificationUsingOrderAndCustomer(final OrderDevice orderDevice, Customer customer) {
        CustomerNotification customerNotification = new CustomerNotification();
        customerNotification.setNotificationType(orderDevice.getNotificationType());
        customerNotification.setCustomer(customer);
        customerNotification.setOrder(orderDevice.getOrder());
        customerNotification.setState(orderDevice.getState());
        return customerNotification;
    }

}
