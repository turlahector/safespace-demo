package com.safespace.view;

import java.util.List;

public class Wallet {
	public List<Assets> assetList;
	public String accountId;
	
	public List<Assets> getAssetList() {
		return assetList;
	}
	public void setAssetList(List<Assets> assetList) {
		this.assetList = assetList;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
