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
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.safespace.service.StellarService;
import com.safespace.view.Assets;
import com.safespace.view.KeyPairView;
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
		String friendbotUrl = String.format("https://horizon-testnet.stellar.org/friendbot?addr=%s",accountId);
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
	
	public Map<String, Object>  sendPayment() throws IOException {
		Map<String, Object> status = new HashMap<String, Object>();
		Network.useTestNetwork();
		Server server = new Server("https://horizon-testnet.stellar.org");

		// Keys for accounts to issue and receive the new asset
		KeyPair issuingKeys = KeyPair
		  .fromSecretSeed("SDVIFXJSPD4K4JYGOOAZCNB7HLEXH4NIBNGCEJFGDUHIXGPSH6XUIDJD");
		KeyPair receivingKeys = KeyPair
		  .fromSecretSeed("SCNJDYDMCQVD67Y6JAYVC7BNCCEMAOZNHBEHOTYJIUKEOVGRK4GJ46PE");

		// Create an object to represent the new asset
		Asset astroDollar = Asset.createNonNativeAsset("MVPToken", issuingKeys);
		
		AccountResponse receiving = server.accounts().account(receivingKeys);
		Transaction allowAstroDollars = new Transaction.Builder(receiving)
		  .addOperation(
		    // The `ChangeTrust` operation creates (or alters) a trustline
		    // The second parameter limits the amount the account can hold
		    new ChangeTrustOperation.Builder(astroDollar, "1000").build())
		  .build();
		allowAstroDollars.sign(receivingKeys);
		server.submitTransaction(allowAstroDollars);

		// First, the receiving account must trust the asset
		
		// Second, the issuing account actually sends a payment using the asset
		AccountResponse issuing = server.accounts().account(issuingKeys);
		Transaction sendAstroDollars = new Transaction.Builder(issuing)
		  .addOperation(
		    new PaymentOperation.Builder(receivingKeys, astroDollar, "100").build())
		  .build();
		sendAstroDollars.sign(issuingKeys);
		server.submitTransaction(sendAstroDollars);
		return status;
	}

	public Map<String, Object> transactionsPerAccount(String accountId) {
		StellarUtil stellarUtil = new StellarUtil();
		String endpoint =  network+"/accounts/"+accountId+"/transactions";
		try {
			stellarUtil.callMethod("GET", "", endpoint);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}
