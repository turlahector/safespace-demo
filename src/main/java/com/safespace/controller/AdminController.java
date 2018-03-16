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
	private String publicKeyIssuer = "GDBRARDYDKK6R2RVZY6XSV4SRG47AHZN6HRWJA767E7V3QJLGPRTYK3C";
	private String secretKeyIssuer = "SDVIFXJSPD4K4JYGOOAZCNB7HLEXH4NIBNGCEJFGDUHIXGPSH6XUIDJD";
	
	private String publicKeyReciever = "GD5B2YJEYKA2NEJABZTYV2FZMWFODZI7NCG66FRX26MGCFNN73BSZHTP";
	private String secretKeyReciever = "SCMYNKD7MAWHJDVSNWSK3XDXUQRMIUG4BM7M744XGVKGZKMYWTCZTOO4";
	
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