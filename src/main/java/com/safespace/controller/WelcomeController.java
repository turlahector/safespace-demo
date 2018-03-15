package com.safespace.controller;

import java.io.IOException;
import java.util.Map;

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
	
	@Value("${welcome.messageJay:test}")
	private String messageJay = "Hello World";
	
	@Autowired
	private StellarService stellarService;
	

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}

	@RequestMapping("/wallet/{accountId}")
	public ModelAndView account(@PathVariable ("accountId") String accountId) throws IOException {
		ModelAndView model = new ModelAndView("wallet");
		KeyPair keyPair =KeyPair.fromAccountId(accountId);
		
		model.addObject("accountId", accountId);
		//stellarService.requestFreeLumen(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		if (wallet == null) {
			model.addObject("wallet","empty");
		}else {
			model.addObject("wallet",wallet);
		}
		
		
		return model;
	}
	
	@RequestMapping("/account")
	public String wallet(Map<String, Object> model) {
		model.put("message", this.messageJay);
		return "account";
	}

}