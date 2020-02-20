package com.ameren.outage.order.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ameren.outage.order.core.enums.FilterResult;

@Entity
@Table(name="FILTER_DETAIL")
public class FilterDetail extends Auditable {

	@Id()
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="filter_summary_id")
	private Long filterSummaryId;
	
	@Column(name="bill_account_number")
	private String billAccountNumber;
		
	@Column(name="notification_type")
	private String notificationType;

	@Column(name="filter_code")
	@Enumerated(EnumType.STRING)
	private FilterResult filterCode;
	
	@Column(name="outage_order_id")
	private Long outageOrderId;
	
	public FilterDetail() {};
	
	public FilterDetail(final String billAccountNumber, final FilterResult filterCode, final Long outageOrderId) {
		this.billAccountNumber = billAccountNumber;
		this.filterCode = filterCode;
		this.outageOrderId = outageOrderId;
	}
	
	public FilterDetail(final String billAccountNumber, final FilterResult filterCode, final Long outageOrderId,final String notificationType) {
		this.billAccountNumber = billAccountNumber;
		this.filterCode = filterCode;
		this.outageOrderId = outageOrderId;
		this.notificationType = notificationType;
	}

	public Long getId() {
		return id;
	}
	
	public String getBillAccountNumber() {
		return billAccountNumber;
	}

	public void setBillAccountNumber(String billAccountNumber) {
		this.billAccountNumber = billAccountNumber;
	}

	public FilterResult getFilterCode() {
		return filterCode;
	}

	public void setFilterCode(FilterResult filterCode) {
		this.filterCode = filterCode;
	}

    public Long getOutageOrderId() {
        return outageOrderId;
    }

    public void setOutageOrderId(Long outageOrderId) {
        this.outageOrderId = outageOrderId;
    }

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
}
