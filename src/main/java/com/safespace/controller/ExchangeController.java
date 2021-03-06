package com.safespace.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.stellar.sdk.KeyPair;

import com.safespace.service.StellarService;
import com.safespace.view.Wallet;

@Controller
public class ExchangeController {

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
	
	@RequestMapping("/admin/exchange")
	public ModelAndView exchange() throws IOException {
		return exchange("lumens","lumens");
	}
	
	@RequestMapping("/admin/exchange/{sellAsset}/{buyAsset}")
	public ModelAndView exchange(@PathVariable ("sellAsset")String sellType,@PathVariable ("buyAsset")String buyAssetType) throws IOException {
		ModelAndView model = new ModelAndView("admin-exchange");
		
		KeyPair keyPair =KeyPair.fromAccountId(publicKeyReciever);
		
		model.addObject("accountId", publicKeyReciever);
		//stellarService.requestFreeLumen(accountId);
		model.addObject("exchangeGraphUrl",
				stellarService.exchangeUrlBuilder(buyAssetType, sellType, publicKeyIssuer, publicKeyIssuer));
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		model.addObject("currentPage", "exchange");
		if (wallet == null) {
			model.addObject("wallet","empty");
		}else {
			model.addObject("wallet",wallet);
		}
		
		model.addObject("orderBook", stellarService.orderBook(buyAssetType,sellType,publicKeyIssuer,publicKeyIssuer));
		
		model.addObject("secretKey", secretKeyReciever);
		return model;
	}
	
	
	


	
	

}