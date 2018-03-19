package com.safespace.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.safespace.service.StellarService;

@Controller
public class ExchangeController {

	//issuer keys
	private String publicKeyIssuer = "GDBRARDYDKK6R2RVZY6XSV4SRG47AHZN6HRWJA767E7V3QJLGPRTYK3C";
	private String secretKeyIssuer = "SDVIFXJSPD4K4JYGOOAZCNB7HLEXH4NIBNGCEJFGDUHIXGPSH6XUIDJD";
	
	private String publicKeyReciever = "GD5B2YJEYKA2NEJABZTYV2FZMWFODZI7NCG66FRX26MGCFNN73BSZHTP";
	private String secretKeyReciever = "SCMYNKD7MAWHJDVSNWSK3XDXUQRMIUG4BM7M744XGVKGZKMYWTCZTOO4";
	
	@Autowired
	private StellarService stellarService;
	

	@RequestMapping("/exchange")
	public ModelAndView exchange() throws IOException {
		ModelAndView model = new ModelAndView("exchange");
		
		
		return model;
	}
	
	

}