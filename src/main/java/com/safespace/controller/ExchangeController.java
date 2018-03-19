package com.safespace.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.stellar.sdk.KeyPair;

import com.safespace.service.StellarService;
import com.safespace.view.Wallet;

@Controller
public class ExchangeController {

	//issuer keys
	private String publicKeyIssuer = "GBHQNJK3JTTFLHCNLCRV3IZ4LZNQQJUHDGQ2U7VQJHW2EPL2P2MPWZLW";
	private String secretKeyIssuer = "SAPLRJE2N5PUPZRVXFXZVQ2FSZJMMWWSRSLB5BNUWEMKONRZ63NCZO3P";
	
	private String publicKeyReciever = "GAZVQ4RPBY3LRCGAZLAMT4UOUM3HGVF7O7L3JPIOBWFSUDQW4N3JTX2F";
	private String secretKeyReciever = "SBGKAZYOHY5VTMXRJCOXGYCBNQKXBOOZVA2H4VVZAAXCQBOXIWWFLGP7";
	
	@Autowired
	private StellarService stellarService;
	
	@RequestMapping("/admin/exchange")
	public ModelAndView exchange() throws IOException {
		ModelAndView model = new ModelAndView("admin-exchange");
		
		KeyPair keyPair =KeyPair.fromAccountId(publicKeyReciever);
		
		model.addObject("accountId", publicKeyReciever);
		//stellarService.requestFreeLumen(accountId);
		Wallet wallet = stellarService.getWalletDetails(keyPair);
		if (wallet == null) {
			model.addObject("wallet","empty");
		}else {
			model.addObject("wallet",wallet);
		}
		model.addObject("orderBook", stellarService.orderBook("credit_alphanum12","credit_alphanum12","customAsset","skyFlakes","GAU6F4I4ZJE6B6AMGDSAK6VJI6SAX4HBKRX4FWE2BMLWYKWSOENQVQ34","GAU6F4I4ZJE6B6AMGDSAK6VJI6SAX4HBKRX4FWE2BMLWYKWSOENQVQ34"));
	

		model.addObject("secretKey", secretKeyReciever);
		return model;
	}


	
	

}