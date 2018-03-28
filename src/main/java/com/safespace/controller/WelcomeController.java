package com.safespace.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
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
public class WelcomeController {

	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";
	@Value("${issuer.publicKey}")
	private String publicKeyIssuer;
	@Value("${welcome.messageJay:test}")
	private String messageJay = "Hello World";

	@Autowired
	private StellarService stellarService;
	
	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}
	final static Logger LOGGER = Logger.getLogger(WelcomeController.class);

	@RequestMapping("/wallet/{accountId}")
	public ModelAndView account(@PathVariable("accountId") String accountId) throws IOException {
		ModelAndView model = new ModelAndView("wallet");
		KeyPair keyPair = KeyPair.fromAccountId(accountId);
		model.addObject("currentPage", "wallet");
		model.addObject("accountId", accountId);
		// stellarService.requestFreeLumen(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		if (wallet == null) {
			model.addObject("wallet", "empty");
		} else {
			model.addObject("wallet", wallet);
		}

		model.addObject("transactions", stellarService.transactionsPerAccount(accountId));
		return model;
	}

	@RequestMapping("/exchange/{accountId}")
	public ModelAndView exchange(@PathVariable("accountId") String accountId) throws IOException {
			
		return exchange(accountId, "lumens", "lumens");
	}

	@RequestMapping("/exchange/{accountId}/{sellAsset}/{buyAsset}")
	public ModelAndView exchange(@PathVariable("accountId") String accountId,
			@PathVariable("sellAsset") String sellType, @PathVariable("buyAsset") String buyAssetType)
			throws IOException {
		ModelAndView model = new ModelAndView("exchange");
		KeyPair keyPair = KeyPair.fromAccountId(accountId);

		model.addObject("accountId", accountId);
		// stellarService.requestFreeLumen(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		model.addObject("currentPage", "exchange");
		if (wallet == null) {
			model.addObject("wallet", "empty");
		} else {
			model.addObject("wallet", wallet);
		}
		model.addObject("exchangeGraphUrl",
				stellarService.exchangeUrlBuilder(buyAssetType, sellType, publicKeyIssuer, publicKeyIssuer));

		model.addObject("orderBook",
				stellarService.orderBook(buyAssetType, sellType, publicKeyIssuer, publicKeyIssuer));
		return model;

	}

	@RequestMapping("/account")
	public String wallet(Map<String, Object> model) {
		model.put("message", this.messageJay);
		return "account";
	}

}