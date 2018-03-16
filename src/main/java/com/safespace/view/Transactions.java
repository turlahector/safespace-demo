package com.safespace.view;

public class Transactions {
	public String date;
	public String type;
	public String name;
	public String sender_recipient;
	public String fee;
	public String unit;
	private String ledger;
	private String pagingToken;
	
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public String getPagingToken() {
		return pagingToken;
	}
	public void setPagingToken(String pagingToken) {
		this.pagingToken = pagingToken;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSender_recipient() {
		return sender_recipient;
	}
	public void setSender_recipient(String sender_recipient) {
		this.sender_recipient = sender_recipient;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	

}
