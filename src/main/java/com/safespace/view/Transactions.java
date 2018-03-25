package com.safespace.view;

public class Transactions {
	private String date;
	private String type;
	private String name;
	private String sender_recipient;
	private String fee;
	private String unit;
	private String ledger;
	private String pagingToken;
	private String assetCode;
	private String fromAccount;
	private String toAccount;
	private String amount;
	
	
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
	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
	public String getToAccount() {
		return toAccount;
	}
	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	

}
