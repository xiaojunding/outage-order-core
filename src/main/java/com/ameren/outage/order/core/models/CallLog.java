package com.ameren.outage.order.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CallLog{

	@JsonProperty("callId")
	private String callId;
	@JsonProperty("billAccount")
	private String billaccount;
	@JsonProperty("createTimestamp")
	private String createTimestamp;
	@JsonProperty("transformer")
	private String transformer;
	@JsonProperty("processed")
	private String processed;
	@JsonProperty("processTimestamp")
	private String processTimestamp;
	@JsonProperty("processedFlag")
	private String processedflag;
	//Newly added data points
	@JsonProperty("reportedOutageId")
	private String reportedOutageId;
	@JsonProperty("okToCallback")
	private boolean okToCallBack;
	@JsonProperty("source")
	private String source;
	@JsonProperty("serviceState")
	private String serviceState;
	@JsonProperty("altPhoneNumber")
	private String altPhoneNumber;
	
	public String getProcessedflag() {
		return processedflag;
	}
	public void setProcessedflag(String processedflag) {
		this.processedflag = processedflag;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public String getBillaccount() {
		return billaccount;
	}
	public void setBillaccount(String billaccount) {
		this.billaccount = billaccount;
	}
	public String getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(String insertts) {
		this.createTimestamp = insertts;
	}
	public String getTransformer() {
		return transformer;
	}
	public void setTransformer(String transformer) {
		this.transformer = transformer;
	}
	public String getProcessed() {
		return processed;
	}
	public void setProcessed(String processed) {
		this.processed = processed;
	}
	public String getProcessTimestamp() {
		return processTimestamp;
	}
	public void setProcessTimestamp(String processTimestamp) {
		this.processTimestamp = processTimestamp;
	}
	//newly added data points setters and getters 
	public String getReportedOutageId() {
		return reportedOutageId;
	}
	public void setReportedOutageId(String reportedOutageId) {
		this.reportedOutageId = reportedOutageId;
	}
	public boolean isOkToCallBack() {
		return okToCallBack;
	}
	public void setOkToCallBack(boolean okToCallBack) {
		this.okToCallBack = okToCallBack;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getServiceState() {
		return serviceState;
	}
	public void setServiceState(String serviceState) {
		this.serviceState = serviceState;
	}
	public String getAltPhoneNumber() {
		return altPhoneNumber;
	}
	public void setAltPhoneNumber(String altPhoneNumber) {
		this.altPhoneNumber = altPhoneNumber;
	}
}