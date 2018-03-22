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
public class AccountController {
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
	
	@RequestMapping("/account/balances/{accountId}")
	public ModelAndView accountBalances(@PathVariable ("accountId") String accountId) throws IOException{
		ModelAndView modelAndView = new ModelAndView("account-balances");
		KeyPair keyPair =KeyPair.fromAccountId(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		if (wallet == null) {
			modelAndView.addObject("wallet","empty");
		}else {
			modelAndView.addObject("wallet",wallet);
		}		
		modelAndView.addObject("accountId", accountId);
		
		return modelAndView;	
	}
	
	@RequestMapping("/account/latest-transaction/{accountId}")
	public ModelAndView accountLatestTransaction(@PathVariable ("accountId") String accountId) throws IOException{
		ModelAndView modelAndView = new ModelAndView("latest-transaction");
		KeyPair keyPair =KeyPair.fromAccountId(accountId);
		
		modelAndView.addObject("transactions", stellarService.fetchTransactionViaAccountId(accountId));
		modelAndView.addObject("accountId", accountId);
		
		return modelAndView;	
	}
	

}
