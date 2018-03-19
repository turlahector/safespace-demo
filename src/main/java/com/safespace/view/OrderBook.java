package com.safespace.view;

import com.google.gson.JsonObject;

public class OrderBook {
	
	private String sellingAssetType;
	private String sellingAssetCode;
	private String sellingAssetIsuer;
	private String buyingAssetType;
	private String buyingAssetCode;
	private String buyingAssetIsuer;
	private String buyingAmount;
	private String sellingAmount;
	
	public OrderBook(){
		
	}
	public OrderBook(JsonObject selling,JsonObject buying){
		if(!selling.isJsonNull()){
			this.sellingAssetCode = "LUMENS";
			if(!selling.get("asset_type").getAsString().equalsIgnoreCase("native")){
				this.sellingAssetCode = selling.get("asset_code").getAsString();
				this.sellingAssetIsuer = selling.get("asset_issuer").getAsString();
			}
		}
		if(!buying.isJsonNull()){
			this.buyingAssetCode = "LUMENS";
			if(!buying.get("asset_type").getAsString().equalsIgnoreCase("native")){
				this.buyingAssetCode = buying.get("asset_code").getAsString();
				this.buyingAssetIsuer = buying.get("asset_issuer").getAsString();  
			}			
		}
	}
	public String getSellingAssetType() {
		return sellingAssetType;
	}
	public void setSellingAssetType(String sellingAssetType) {
		this.sellingAssetType = sellingAssetType;
	}
	public String getSellingAssetCode() {
		return sellingAssetCode;
	}
	public void setSellingAssetCode(String sellingAssetCode) {
		this.sellingAssetCode = sellingAssetCode;
	}
	public String getSellingAssetIsuer() {
		return sellingAssetIsuer;
	}
	public void setSellingAssetIsuer(String sellingAssetIsuer) {
		this.sellingAssetIsuer = sellingAssetIsuer;
	}
	public String getBuyingAssetType() {
		return buyingAssetType;
	}
	public void setBuyingAssetType(String buyingAssetType) {
		this.buyingAssetType = buyingAssetType;
	}
	public String getBuyingAssetCode() {
		return buyingAssetCode;
	}
	public void setBuyingAssetCode(String buyingAssetCode) {
		this.buyingAssetCode = buyingAssetCode;
	}
	public String getBuyingAssetIsuer() {
		return buyingAssetIsuer;
	}
	public void setBuyingAssetIsuer(String buyingAssetIsuer) {
		this.buyingAssetIsuer = buyingAssetIsuer;
	}
	public String getBuyingAmount() {
		return buyingAmount;
	}
	public void setBuyingAmount(String buyingAmount) {
		this.buyingAmount = buyingAmount;
	}
	public String getSellingAmount() {
		return sellingAmount;
	}
	public void setSellingAmount(String sellingAmount) {
		this.sellingAmount = sellingAmount;
	}

}
