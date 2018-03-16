package com.safespace.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;

import com.safespace.service.StellarService;
import com.stellar.StellarUtil;

import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping(value="/api/stellar")
public class StellarController {
	
	final static Logger logger = (Logger) LoggerFactory.getLogger(StellarController.class);
	public StellarUtil stellarUtil = new StellarUtil();
	
	private String publicKeyIssuer = "GDBRARDYDKK6R2RVZY6XSV4SRG47AHZN6HRWJA767E7V3QJLGPRTYK3C";
	private String secretKeyIssuer = "SDVIFXJSPD4K4JYGOOAZCNB7HLEXH4NIBNGCEJFGDUHIXGPSH6XUIDJD";
	
	private String publicKeyReciever = "GD5B2YJEYKA2NEJABZTYV2FZMWFODZI7NCG66FRX26MGCFNN73BSZHTP";
	private String secretKeyReciever = "SCMYNKD7MAWHJDVSNWSK3XDXUQRMIUG4BM7M744XGVKGZKMYWTCZTOO4";
	
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
	
	@RequestMapping(value="/sendPayment/{assetCode}/{sourceSecretKey}/{destinationAccountId}/{amount}/{transactionMemo}", method=RequestMethod.POST)
	public Map<String, Object> sendPayment(
			@PathVariable ("assetCode") String assetCode, 
			@PathVariable ("sourceSecretKey") String sourceSecretKey, 
			@PathVariable("destinationAccountId") String destinationAccountId, 
			@PathVariable ("amount") String amount,
			@PathVariable("transactionMemo") String transactionMemo) throws IOException {
		KeyPair issuer = KeyPair.fromAccountId(publicKeyIssuer);
		KeyPair source = KeyPair.fromSecretSeed(sourceSecretKey);
		KeyPair destination = KeyPair.fromAccountId(destinationAccountId);
		
		Asset customAsset = Asset.createNonNativeAsset(assetCode, issuer);
		
		if (assetCode.equalsIgnoreCase("LUMENS")) {
			customAsset = new AssetTypeNative();
			
		}
		return stellarService.sendTransaction(customAsset, source, destination, amount, transactionMemo);
	}
}
