package com.safespace.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.stellar.sdk.Asset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.AccountsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.safespace.service.StellarService;
import com.safespace.view.Assets;
import com.safespace.view.KeyPairView;
import com.safespace.view.Transactions;
import com.safespace.view.Wallet;
import com.stellar.StellarUtil;

@Service
public class StellarServiceImpl implements StellarService{
	
	private String network = "https://horizon-testnet.stellar.org";
	
	@Override
	public Map<String, Object> generateKeyPair() {
		KeyPair pair = KeyPair.random();
		KeyPairView keyPair = new KeyPairView();
		Map<String, Object> keyPairMap = new HashMap<String, Object>();
		//System.out.println(pair);
		keyPair.setPublicKey(pair.getAccountId());
		keyPair.setSecretKey(new String(pair.getSecretSeed()));
		keyPairMap.put("keypair", keyPair);
		return keyPairMap;
	}

	@Override
	public Map<String, Object> requestFreeLumen(String accountId) {
		Map<String, Object> status = new HashMap<String, Object>();
		String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s",accountId);
		InputStream response = null;
		try {
			response = new URL(friendbotUrl).openStream();
			status.put("status", "success");
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
			status.put("status", "error");
		}
		//String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
		//System.out.println("SUCCESS! You have a new account :)\n" + body);
		return status;
	}
	
	public Wallet  getWalletDetails(KeyPair accountKeyPair) throws IOException {
		//Map<String, Object> wallet = new HashMap<String, Object>();
		Wallet accountWallet = new Wallet();
		ArrayList<Assets> assetList = new ArrayList<>();
		Server server = new Server(network);
		AccountResponse account = null;
		try {
			account = server.accounts().account(accountKeyPair);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			accountWallet = null;
		}
		
		if (account != null) {
			for (AccountResponse.Balance balance : account.getBalances()) {
				Assets asset = new Assets();
				asset.setAssetCode(balance.getAssetCode());
				asset.setAssetType(balance.getAssetType());
				asset.setBalance(balance.getBalance());
				asset.setLimit(balance.getLimit());
				assetList.add(asset);
			}
			accountWallet.setAccountId(accountKeyPair.getAccountId());
			accountWallet.setAssetList(assetList);
			//wallet.put("wallet", wallet);
		}
		
		return accountWallet;
	}
	
	
	public Map<String, Object>  issuingNewAsset(String issuingSecretKey, String recieveingSecretKey, Asset customAsset, String limit, String amountToSend) throws IOException {
		Map<String, Object> status = new HashMap<String, Object>();
		try {
			Network.useTestNetwork();
			Server server = new Server(network);

			// Keys for accounts to issue and receive the new asset
			KeyPair issuingKeys = KeyPair
			  .fromSecretSeed(issuingSecretKey);
			KeyPair receivingKeys = KeyPair
			  .fromSecretSeed(recieveingSecretKey);

			// Create an object to represent the new asset
			//Asset.createNonNativeAsset("AstroDollar", issuingKeys);

			// First, the receiving account must trust the asset
			AccountResponse receiving = server.accounts().account(receivingKeys);
			Transaction allowNewAsset = new Transaction.Builder(receiving)
			  .addOperation(
			    // The `ChangeTrust` operation creates (or alters) a trustline
			    // The second parameter limits the amount the account can hold
			    new ChangeTrustOperation.Builder(customAsset, limit).build())
			  .build();
			allowNewAsset.sign(receivingKeys);
			server.submitTransaction(allowNewAsset);

			// Second, the issuing account actually sends a payment using the asset
			AccountResponse issuing = server.accounts().account(issuingKeys);
			Transaction newAsset = new Transaction.Builder(issuing)
			  .addOperation(
			    new PaymentOperation.Builder(receivingKeys, customAsset, amountToSend).build())
			  .build();
			newAsset.sign(issuingKeys);
			server.submitTransaction(newAsset);
			status.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			status.put("status", "error");
		}
		return status;
	}
	
	public Map<String, Object>  sendTransaction(Asset asset, KeyPair source, KeyPair destination, String amount, String transactionMemo) {
		Map<String, Object> status = new HashMap<String, Object>();
		Network.useTestNetwork();
		Server server = new Server(network);

		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or there was another error.
		try {
			server.accounts().account(destination);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			status.put("status", e1.getMessage());
		}

		// If there was no error, load up-to-date information on your account.
		AccountResponse sourceAccount = null;
		try {
			sourceAccount = server.accounts().account(source);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			status.put("status", e1.getMessage());
		}
		
		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
		        .addOperation(new PaymentOperation.Builder(destination, asset, amount).build())
		        // A memo allows you to add your own metadata to a transaction. It's
		        // optional and does not affect how Stellar treats the transaction.
		        .addMemo(Memo.text(transactionMemo))
		        .build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(source);

		// And finally, send it off to Stellar!
		try {
		  SubmitTransactionResponse response = server.submitTransaction(transaction);
		  System.out.println("Success!");
		  System.out.println(response);
		  status.put("status","success");
		} catch (Exception e) {
		  status.put("status", e.getMessage());
		  // If the result is unknown (no response body, timeout etc.) we simply resubmit
		  // already built transaction:
		  // SubmitTransactionResponse response = server.submitTransaction(transaction);
		}
		return status;
	}
	
	public void sendPayment(String assetCode) throws IOException {		
		Network.useTestNetwork();		
		Server server = new Server("https://horizon-testnet.stellar.org");		
		KeyPair issuer = KeyPair.fromAccountId("GCZWYIINWDA3KFX374DMOHDYER5QXLQMRLIN7OKI7YS7SHLDOHVWAF7S");		
		Asset customAsset = Asset.createNonNativeAsset(assetCode, issuer);		
		KeyPair source = KeyPair.fromSecretSeed("SBIS3W62BSJVG3TEIOSYJSQT5XFZK2ZZFAJDEIHLCSRHC6RN6NDEQ6Y4");		
		KeyPair destination = KeyPair.fromAccountId("GCREHQOSRDKJNC6CWYP2FCOHVVU4EJA54HHIPI4F5JA6KZMIF2I336PO");				
		KeyPair receivingKeys = KeyPair.fromSecretSeed("SAGHP7BHFXUFLCV4FDZLA2CPSEVJKYCQDMA7CT7FRN2XTINUGLSGRMCL");		
		// First, check to make sure that the destination account exists.		
		// You could skip this, but if the account does not exist, you will be charged		
		// the transaction fee when the transaction fails.		
		// It will throw HttpResponseException if account does not exist or there was another error.		
		server.accounts().account(destination);				
		// First, the receiving account must trust the asset					
		AccountResponse receiving = server.accounts().account(receivingKeys);					
		Transaction allowNewAsset = new Transaction.Builder(receiving).addOperation(					    
				// The `ChangeTrust` operation creates (or alters) a trustline					    
				// The second parameter limits the amount the account can hold					    
				new ChangeTrustOperation.Builder(customAsset, "10000000").build()).build();					
		allowNewAsset.sign(receivingKeys);					
		server.submitTransaction(allowNewAsset);		
		// If there was no error, load up-to-date information on your account.		
		AccountResponse sourceAccount = server.accounts().account(source);		
		//new AssetTypeNative()		
		// Start building the transaction.		
		Transaction transaction = new Transaction.Builder(sourceAccount).addOperation(new PaymentOperation.Builder(destination, customAsset, "100").build())		       
				// A memo allows you to add your own metadata to a transaction. It's		       
				// optional and does not affect how Stellar treats the transaction.		        
				.addMemo(Memo.text("Test Transaction"))		        .build();		
		// Sign the transaction to prove you are actually the person sending it.		
		transaction.sign(source);		
		// And finally, send it off to Stellar!		
		try {		  
			SubmitTransactionResponse response = server.submitTransaction(transaction);		  
			System.out.println("Success!");		 
			System.out.println(response);		
			} catch (Exception e) {		 
				System.out.println("Something went wrong!");		 
				System.out.println(e.getMessage());		  
				// If the result is unknown (no response body, timeout etc.) we simply resubmit		  
				// already built transaction:		 
				// SubmitTransactionResponse response = server.submitTransaction(transaction);		
			}	
		}
		

	public ArrayList<Transactions> transactionsPerAccount(String accountId) {
		StellarUtil stellarUtil = new StellarUtil();
		String endpoint =  network+"/accounts/"+accountId+"/transactions";
		Gson gson = new Gson();
		ArrayList<Transactions> transactions = new ArrayList<Transactions>();
		try {
			String transansactionList = stellarUtil.callMethod("GET", "", endpoint);
			JsonElement jelem = gson.fromJson(transansactionList, JsonElement.class);

			JsonObject embeded = jelem.getAsJsonObject().get("_embedded").getAsJsonObject(); 
			JsonArray recordArray = embeded.get("records").getAsJsonArray(); 
			if(!recordArray.isJsonNull()){
				for(JsonElement element : recordArray){
					Transactions transaction = new Transactions();
					JsonObject recordElement = element.getAsJsonObject();
					transaction.setFee(recordElement.get("fee_paid").getAsString());
					transaction.setDate(recordElement.get("created_at").getAsString());
					transaction.setType(recordElement.get("memo_type").getAsString());
					transaction.setSender_recipient(recordElement.get("source_account").getAsString());
					transaction.setSender_recipient(recordElement.get("source_account").getAsString());
					transaction.setLedger(recordElement.get("ledger").getAsString());
					transaction.setPagingToken(recordElement.get("paging_token").getAsString());
					
					transactions.add(transaction);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return transactions;
	}

	
}
