package com.safespace.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;

import com.safespace.view.OrderBook;
import com.safespace.view.Transactions;
import com.safespace.view.Wallet;


public interface StellarService {
	public Map<String, Object> generateKeyPair();
	public Map<String, Object> requestFreeLumen(String accountId);
	public Wallet getWalletDetails(KeyPair accountKeyPair) throws IOException ;
	public Map<String, Object>issuingNewAsset(String issuingSecretKey, String recieveingSecretKey, Asset customAsset, String limit, String amountToSend) throws IOException ;
	public Map<String, Object>  sendTransaction(Asset asset, KeyPair source, KeyPair destination, String amount, String transactionMemo) ;
	public Map<String, Object> sendPayment(Asset asset, KeyPair source, KeyPair destination, String amount, String transactionMemo) throws IOException;
	public ArrayList<Transactions> transactionsPerAccount(String accountId);
	public void sendPayment(String assetCode) throws IOException; 
	public Map<String, Object> createOffer(String souceSecretSeed, Asset selling, Asset buying, String amountSell, String amountBuy, String memo);
	public ArrayList<OrderBook> orderBook(String sellingAssetType, String buyingAssetType, String buyingAssetCode,
			String sellingAssetCode, String buyingAssetIssuer, String sellingAssetIssuer);

}
