package com.safespace.service;

import java.io.IOException;
import java.util.Map;

import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;
import com.safespace.view.Wallet;


public interface StellarService {
	public Map<String, Object> generateKeyPair();
	public Map<String, Object> requestFreeLumen(String accountId);
	public Wallet getWalletDetails(KeyPair accountKeyPair) throws IOException ;
	public Map<String, Object>issuingNewAsset(String issuingSecretKey, String recieveingSecretKey, Asset customAsset, String limit, String amountToSend) throws IOException ;
	public Map<String, Object>  sendTransaction(Asset asset, KeyPair source, KeyPair destination, String amount, String transactionMemo) ;
	public Map<String, Object> sendPayment() throws IOException;
	public Map<String, Object> transactionsPerAccount(String accountId);
	
}
