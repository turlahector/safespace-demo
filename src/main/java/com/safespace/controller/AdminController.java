package com.safespace.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.stellar.sdk.KeyPair;

import com.safespace.service.StellarService;
import com.safespace.view.Transactions;
import com.safespace.view.Wallet;

@Controller
public class AdminController {

	//issuer keys
	private String publicKeyIssuer = "GAU6F4I4ZJE6B6AMGDSAK6VJI6SAX4HBKRX4FWE2BMLWYKWSOENQVQ34";
	private String secretKeyIssuer = "SAZQCB5OJW422LVOMD2V3YUE6627BSPEOE3Y4C3OENTIXI2FI5VM52KO";
	
	private String publicKeyReciever = "GBMTKZMUCNQBPDEHCGCTDCGDDQ4L65KZLOK5FY4PVFD5PEGO2RQEP5J5";
	private String secretKeyReciever = "SDUHIVPNRPSPQ2U6MY4ZGXRG7SORKOL6KKYC6P4IROKUPNNRATG3NLH6";
	
	@Autowired
	private StellarService stellarService;
	

	@RequestMapping("/admin")
	public ModelAndView account() throws IOException {
		ModelAndView model = new ModelAndView("admin");
		
		KeyPair keyPair =KeyPair.fromAccountId(publicKeyReciever);
		
		model.addObject("accountId", publicKeyReciever);
		//stellarService.requestFreeLumen(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		if (wallet == null) {
			model.addObject("wallet","empty");
		}else {
			model.addObject("wallet",wallet);
		}
		ArrayList<Transactions> transactions = stellarService.transactionsPerAccount(publicKeyReciever);
		if(null != transactions && transactions.size() > 0){
			model.addObject("transactions",transactions);
		} else {
			model.addObject("transactions","empty");
		}
		//stellarService.transactionsPerAccount(publicKeyReciever);
		return model;
	}
	
	

}