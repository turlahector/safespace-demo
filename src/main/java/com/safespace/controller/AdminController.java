package com.safespace.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.stellar.sdk.KeyPair;

import com.safespace.service.StellarService;
import com.safespace.view.Wallet;

@Controller
public class AdminController {

	//issuer keys
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
	

	@RequestMapping("/admin/wallet")
	public ModelAndView account() throws IOException {
		ModelAndView model = new ModelAndView("admin-wallet");
		
		KeyPair keyPair =KeyPair.fromAccountId(publicKeyReciever);
		
		model.addObject("accountId", publicKeyReciever);
		//stellarService.requestFreeLumen(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		if (wallet == null) {
			model.addObject("wallet","empty");
		}else {
			model.addObject("wallet",wallet);
		}
		model.addObject("transactions",stellarService.transactionsPerAccount(publicKeyReciever));
		
		//stellarService.transactionsPerAccount(publicKeyReciever);
		return model;
	}
	
	

}