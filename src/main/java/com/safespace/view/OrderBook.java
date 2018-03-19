package com.safespace.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class OrderBook {
	
	
	private String fromCode;
	private String toCode;
	
	private List<OrderPrices> buyPrices = new ArrayList<OrderPrices>();
	private List<OrderPrices> sellPrices = new ArrayList<OrderPrices>();
	
	public OrderBook(){
		
	}
	public OrderBook(JsonObject selling,JsonObject buying){
		if(!selling.isJsonNull()){
			this.fromCode = "XLM";
			if(!selling.get("asset_type").getAsString().equalsIgnoreCase("native")){
				this.fromCode = selling.get("asset_code").getAsString();
				
			}
		}
		if(!buying.isJsonNull()){
			this.toCode = "XLM";
			if(!buying.get("asset_type").getAsString().equalsIgnoreCase("native")){
				this.toCode = buying.get("asset_code").getAsString(); 
			}			
		}
	}
	
	public String getFromCode() {
		return fromCode;
	}
	public void setFromCode(String fromCode) {
		this.fromCode = fromCode;
	}
	public String getToCode() {
		return toCode;
	}
	public void setToCode(String toCode) {
		this.toCode = toCode;
	}
	public List<OrderPrices> getBuyPrices() {
		return buyPrices;
	}
	public void setBuyPrices(List<OrderPrices> buyPrices) {
		this.buyPrices = buyPrices;
	}
	public List<OrderPrices> getSellPrices() {
		return sellPrices;
	}
	public void setSellPrices(List<OrderPrices> sellPrices) {
		this.sellPrices = sellPrices;
	}

}
