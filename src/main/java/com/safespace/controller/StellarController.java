package com.safespace.controller;


import java.io.IOException;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.safespace.service.StellarService;
import com.stellar.StellarUtil;

import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping(value="/api/stellar")
public class StellarController {
	
	final static Logger logger = (Logger) LoggerFactory.getLogger(StellarController.class);
	public StellarUtil stellarUtil = new StellarUtil();
	
	@Value("${issuer.publicKey}")
	private String publicKeyIssuer;
		
	@Value("${issuer.privateKey}")
	private String secretKeyIssuer;
		
		
	@Value("${receiver.publicKey}")
	private String publicKeyReciever;
		
	@Value("${receiver.privateKey}")
	private String secretKeyReciever;
	@Autowired
	private StellarService stellarService;
	
	@RequestMapping(value="/generateKeypair", method=RequestMethod.GET)
	public Map<String, Object> generateKeyPair() {
		return stellarService.generateKeyPair();
	}
	
	@RequestMapping(value="/generateLumen/{accountId}", method=RequestMethod.GET)
	public Map<String, Object> generateLumen(@PathVariable ("accountId") String accountId) {
		return stellarService.requestFreeLumen(accountId);
	}
	
	@RequestMapping(value="/generateAsset/{assetCode}/{limit}/{amount}", method=RequestMethod.GET)
	public Map<String, Object> generateAsset(@PathVariable ("assetCode") String assetCode, @PathVariable ("limit") String limit, @PathVariable ("amount") String amount) throws IOException {
		KeyPair source = KeyPair.fromSecretSeed(secretKeyIssuer);
		Asset customAsset = Asset.createNonNativeAsset(assetCode, source);
		return stellarService.issuingNewAsset(secretKeyIssuer, secretKeyReciever, customAsset, limit, amount);
	}
	
	@RequestMapping(value="/sendPayment/{assetCode}/{destinationAccountId}/{amount}/{transactionMemo}", method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> sendPayment(
			@PathVariable ("assetCode") String assetCode,  
			@PathVariable("destinationAccountId") String destinationAccountId, 
			@PathVariable ("amount") String amount,
			@PathVariable("transactionMemo") String transactionMemo,
			@RequestBody String requestBody) throws IOException {
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (requestBody, JsonElement.class);
		JsonObject requestJson = element.getAsJsonObject();
		KeyPair issuingKeysSecret  = KeyPair.fromSecretSeed(secretKeyReciever);
		KeyPair receivingKeysSecret  = KeyPair.fromSecretSeed(requestJson.get("secretCode").getAsString());
		KeyPair assetIssuingKeys = KeyPair.fromAccountId(publicKeyIssuer);
		
		Asset customAsset = Asset.createNonNativeAsset(assetCode, assetIssuingKeys);
		
		if (assetCode.equalsIgnoreCase("LUMENS")) {
			customAsset = new AssetTypeNative();
		}
		
		return stellarService.sendPayment(customAsset, issuingKeysSecret, receivingKeysSecret, amount, transactionMemo,requestJson.get("limit").getAsString());
	}

	
	@RequestMapping(value="/createOffer/{souceSecretSeed}/{assetCodeSell}/{assetAmountSell}/{assetCodeBuy}/{assetAmountBuy}/{transactionMemo}", method=RequestMethod.POST)
		public Map<String, Object> createOffer(
				@PathVariable ("souceSecretSeed") String souceSecretSeed, 
				@PathVariable ("assetCodeSell") String assetCodeSell, 
				@PathVariable("assetAmountSell") String assetAmountSell, 
				@PathVariable ("assetCodeBuy") String assetCodeBuy,
				@PathVariable("assetAmountBuy") String assetAmountBuy,
				@PathVariable("transactionMemo") String transactionMemo) throws IOException {
			KeyPair issuer = KeyPair.fromAccountId(publicKeyIssuer);
			
			
			Asset customAssetSell = Asset.createNonNativeAsset(assetCodeSell, issuer);
			Asset customAssetBuy = Asset.createNonNativeAsset(assetCodeBuy, issuer);
		
			if (assetCodeSell.equalsIgnoreCase("LUMENS")) {
				customAssetSell = new AssetTypeNative();			
			}
			
			if (assetCodeBuy.equalsIgnoreCase("LUMENS")) {
				customAssetBuy = new AssetTypeNative();			
			}
			return stellarService.createOffer(souceSecretSeed, customAssetSell, customAssetBuy, assetAmountSell, assetAmountBuy, transactionMemo);
		}

	@RequestMapping(value="/sendPaymentWallet", method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> sendPaymentWallet(@RequestBody String requestBody){
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (requestBody, JsonElement.class);
		JsonObject requestJson = element.getAsJsonObject();
		String amount = requestJson.get("amount").getAsString();
		String assetCode = requestJson.get("assetCode").getAsString();
		KeyPair sourceSecret = KeyPair.fromSecretSeed(requestJson.get("secretCode").getAsString());
		KeyPair destinationAccount = KeyPair.fromAccountId(requestJson.get("recipient").getAsString());
		KeyPair assetIssuingKeys = KeyPair.fromSecretSeed(secretKeyIssuer);
		String transactionMemo = requestJson.get("transactionMemo").getAsString();
		Asset customAsset = Asset.createNonNativeAsset(assetCode, assetIssuingKeys);
		
		if (assetCode == "LUMENS") {
			customAsset = new AssetTypeNative();
		}
		return stellarService.sendTransaction(customAsset, sourceSecret, destinationAccount, amount, transactionMemo);
	}
			

}
