package com.safespace.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.stellar.sdk.Asset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageOfferOperation;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.safespace.service.StellarService;
import com.safespace.view.Assets;
import com.safespace.view.KeyPairView;
import com.safespace.view.OrderBook;
import com.safespace.view.OrderPrices;
import com.safespace.view.Transactions;
import com.safespace.view.Wallet;
import com.stellar.StellarUtil;

@Service
public class StellarServiceImpl implements StellarService {

	private String network = "https://horizon-testnet.stellar.org";

	@Override
	public Map<String, Object> generateKeyPair() {
		KeyPair pair = KeyPair.random();
		KeyPairView keyPair = new KeyPairView();
		Map<String, Object> keyPairMap = new HashMap<String, Object>();
		// System.out.println(pair);
		keyPair.setPublicKey(pair.getAccountId());
		keyPair.setSecretKey(new String(pair.getSecretSeed()));
		keyPairMap.put("keypair", keyPair);
		return keyPairMap;
	}

	@Override
	public Map<String, Object> requestFreeLumen(String accountId) {
		Map<String, Object> status = new HashMap<String, Object>();
		String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", accountId);
		InputStream response = null;
		try {
			response = new URL(friendbotUrl).openStream();
			status.put("status", "success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			status.put("status", "error");
		}
		// String body = new Scanner(response,
		// "UTF-8").useDelimiter("\\A").next();
		// System.out.println("SUCCESS! You have a new account :)\n" + body);
		return status;
	}

	public Wallet getWalletDetails(KeyPair accountKeyPair) throws IOException {
		// Map<String, Object> wallet = new HashMap<String, Object>();
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
			// wallet.put("wallet", wallet);
		}

		return accountWallet;
	}

	public Map<String, Object> issuingNewAsset(String issuingSecretKey, String recieveingSecretKey, Asset customAsset,
			String limit, String amountToSend) throws IOException {
		Map<String, Object> status = new HashMap<String, Object>();
		try {
			Network.useTestNetwork();
			Server server = new Server(network);

			// Keys for accounts to issue and receive the new asset
			KeyPair issuingKeys = KeyPair.fromSecretSeed(issuingSecretKey);
			KeyPair receivingKeys = KeyPair.fromSecretSeed(recieveingSecretKey);

			// Create an object to represent the new asset
			// Asset.createNonNativeAsset("AstroDollar", issuingKeys);

			// First, the receiving account must trust the asset
			AccountResponse receiving = server.accounts().account(receivingKeys);
			Transaction allowNewAsset = new Transaction.Builder(receiving).addOperation(
					// The `ChangeTrust` operation creates (or alters) a
					// trustline
					// The second parameter limits the amount the account can
					// hold
					new ChangeTrustOperation.Builder(customAsset, limit).build()).build();
			allowNewAsset.sign(receivingKeys);
			server.submitTransaction(allowNewAsset);

			// Second, the issuing account actually sends a payment using the
			// asset
			AccountResponse issuing = server.accounts().account(issuingKeys);
			Transaction newAsset = new Transaction.Builder(issuing)
					.addOperation(new PaymentOperation.Builder(receivingKeys, customAsset, amountToSend).build())
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

	public Map<String, Object> sendTransaction(Asset asset, KeyPair sourceSecret, KeyPair destinationAccount,
			String amount, String transactionMemo) {
		Map<String, Object> status = new HashMap<String, Object>();
		Network.useTestNetwork();
		Server server = new Server(network);

		// KeyPair source =
		// KeyPair.fromSecretSeed("SCFLETXVOZXIBBMQU26EEXXVYZQF44LE4SAEALUWPONZ6DKKJ5EHBPAB");
		// KeyPair destination =
		// KeyPair.fromAccountId("GBOM7QDTWF66QB4WE3ETWUPKBSYSH2ZVKWY5YADDW72BWXRWMZDKZGKX");

		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be
		// charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or
		// there was another error.
		AccountResponse sourceAccount = null;
		try {
			server.accounts().account(destinationAccount);
			sourceAccount = server.accounts().account(sourceSecret);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// If there was no error, load up-to-date information on your account.

		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
				.addOperation(new PaymentOperation.Builder(destinationAccount, asset, amount).build())
				// A memo allows you to add your own metadata to a transaction.
				// It's
				// optional and does not affect how Stellar treats the
				// transaction.
				.addMemo(Memo.text(transactionMemo)).build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(sourceSecret);

		// And finally, send it off to Stellar!
		try {
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			System.out.println("Success!");
			System.out.println(response);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			System.out.println(e.getMessage());
			// If the result is unknown (no response body, timeout etc.) we
			// simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response =
			// server.submitTransaction(transaction);
		}
		return status;
	}

	public Map<String, Object> sendPayment(Asset asset, KeyPair issuingKeys, KeyPair receivingKeys, String amount,
			String transactionMemo) throws IOException {
		Map<String, Object> status = new HashMap<String, Object>();
		Network.useTestNetwork();
		Server server = new Server("https://horizon-testnet.stellar.org");

		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be
		// charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or
		// there was another error.
		// First, the receiving account must trust the asset
		server.accounts().account(issuingKeys);
		AccountResponse receiving = server.accounts().account(receivingKeys);
		Transaction allowNewAsset = new Transaction.Builder(receiving).addOperation(
				// The `ChangeTrust` operation creates (or alters) a trustline
				// The second parameter limits the amount the account can hold
				new ChangeTrustOperation.Builder(asset, "1000").build()).build();
		allowNewAsset.sign(receivingKeys);
		SubmitTransactionResponse response1 = server.submitTransaction(allowNewAsset);
		System.out.println(response1.getLedger());
		// Second, the issuing account actually sends a payment using the asset
		server.accounts().account(receivingKeys);
		AccountResponse issuing = server.accounts().account(issuingKeys);

		Transaction transaction = new Transaction.Builder(issuing)
				.addOperation(new PaymentOperation.Builder(receivingKeys, asset, amount).build())
				// A memo allows you to add your own metadata to a transaction.
				// It's
				// optional and does not affect how Stellar treats the
				// transaction.
				.addMemo(Memo.text(transactionMemo)).build();
		transaction.sign(issuingKeys);
		try {
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			System.out.println("Success!");
			System.out.println(response.getLedger());
		} catch (Exception e) {
			status.put("error", e.getMessage());
			// If the result is unknown (no response body, timeout etc.) we
			// simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response =
			// server.submitTransaction(transaction);
		}
		return status;
	}

	public void sendPayment(String assetCode) throws IOException {
		Network.useTestNetwork();
		Server server = new Server("https://horizon-testnet.stellar.org");
		KeyPair issuer = KeyPair.fromAccountId("GBKE36X7SEOUKEWM37WDKPY5ZH4FEJTBMBHVQLJUHPBQHYMU6RCE4YJE"); // token
																											// creator
		Asset customAsset = Asset.createNonNativeAsset(assetCode, issuer);
		KeyPair source = KeyPair.fromSecretSeed("SAHFFLLZZDP4LKIR2VOCPPC5PEGLHVIVE7SSLHDDGMSDWWHHSO56TO53"); // reciever
																												// 3rd
																												// account
		KeyPair destination = KeyPair.fromAccountId("GBKE36X7SEOUKEWM37WDKPY5ZH4FEJTBMBHVQLJUHPBQHYMU6RCE4YJE");
		KeyPair receivingKeys = KeyPair.fromSecretSeed("SAPWDUEMD3A5IGYLKVY65YB32E73X7FIYYAIPGOLKJBRFECJ6MSX7JGY");
		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be
		// charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or
		// there was another error.
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
		// new AssetTypeNative()
		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
				.addOperation(new PaymentOperation.Builder(destination, customAsset, "100").build())
				// A memo allows you to add your own metadata to a transaction.
				// It's
				// optional and does not affect how Stellar treats the
				// transaction.
				.addMemo(Memo.text("Test Transaction")).build();
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
			// If the result is unknown (no response body, timeout etc.) we
			// simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response =
			// server.submitTransaction(transaction);
		}
	}

	public ArrayList<Transactions> transactionsPerAccount(String accountId) {
		StellarUtil stellarUtil = new StellarUtil();
		String endpoint = network + "/accounts/" + accountId + "/transactions";
		Gson gson = new Gson();
		ArrayList<Transactions> transactions = new ArrayList<Transactions>();
		try {
			String transansactionList = stellarUtil.callMethod("GET", "", endpoint);
			JsonElement jelem = gson.fromJson(transansactionList, JsonElement.class);

			JsonObject embeded = jelem.getAsJsonObject().get("_embedded").getAsJsonObject();
			JsonArray recordArray = embeded.get("records").getAsJsonArray();
			if (!recordArray.isJsonNull()) {
				for (JsonElement element : recordArray) {
					Transactions transaction = new Transactions();
					JsonObject recordElement = element.getAsJsonObject();
					transaction.setFee(recordElement.get("fee_paid").getAsString());
					transaction.setDate(getDateFromString("MM/dd/yyyy", recordElement.get("created_at").getAsString()));
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

	public static String getDateFromString(String format, String dateStr) {

		String date = null;

		Date dateToFormat = Date.from(Instant.parse(dateStr));
		DateFormat formatter = new SimpleDateFormat(format);
		date = formatter.format(dateToFormat);

		return date;
	}

	public Map<String, Object> createOffer(String souceSecretSeed, Asset selling, Asset buying, String amountSell,
			String amountBuy, String memo) {
		Network.useTestNetwork();
		Map<String, Object> status = new HashMap<String, Object>();
		Server server = new Server(network);

		// KeyPair source =
		// KeyPair.fromSecretSeed("SAIYAPGVLID3URRTR7YOBRVCIC4EA6XVFZ5FZOP6TJ3QFMAT44C6RDFY");
		// KeyPair issuer =
		// KeyPair.fromAccountId("GCCV5TIFJMJUIIIIZODUGAL3Q55CFWMZ5ZYPRYR2UUFBZA4NVUYR6GXV");

		KeyPair source = KeyPair.fromSecretSeed(souceSecretSeed);

		// Asset assetMvp = selling; //Asset.createNonNativeAsset("MVPToken",
		// issuer);
		// AssetTypeNative lumen = new AssetTypeNative();
		// If there was no error, load up-to-date information on your account.
		AccountResponse sourceAccount = null;
		try {
			sourceAccount = server.accounts().account(source);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			status.put("status", e1.getMessage());
		}

		Transaction transaction = new Transaction.Builder(sourceAccount)
				.addOperation(
						new ManageOfferOperation.Builder(selling, buying, amountSell, amountBuy).setOfferId(0).build())
				// A memo allows you to add your own metadata to a transaction.
				// It's
				// optional and does not affect how Stellar treats the
				// transaction.
				.addMemo(Memo.text(memo)).build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(source);

		try {
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			System.out.println("Success!");
			System.out.println(response);
			status.put("status", "success");
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			System.out.println(e.getMessage());
			status.put("status", e.getMessage());
			// If the result is unknown (no response body, timeout etc.) we
			// simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response =
			// server.submitTransaction(transaction);
		}
		return status;

	}

	public OrderBook orderBook(String buyingAssetCode,
			String sellingAssetCode, String buyingAssetIssuer, String sellingAssetIssuer) {
		KeyPair issuerKeyPair =KeyPair.fromAccountId(buyingAssetIssuer);
		Asset sellAsset = Asset.createNonNativeAsset(sellingAssetCode, issuerKeyPair);
		Asset buyAsset = Asset.createNonNativeAsset(buyingAssetCode, issuerKeyPair);
		
		InputStream response = null;
		String buyerUrl = orderBookUrlBuilder(sellAsset.getType(), buyAsset.getType(), buyingAssetCode, sellingAssetCode, buyingAssetIssuer, sellingAssetIssuer);
				
				Gson gson = new Gson();
				OrderBook orderBook = null;
	    try {
			
	    	response = new URL(buyerUrl).openStream();
			String returnString = IOUtils.toString(response);
			JsonElement element = gson.fromJson (returnString, JsonElement.class);
			JsonObject requestJson = element.getAsJsonObject();
			orderBook = 	new OrderBook(requestJson.get("base").getAsJsonObject(),requestJson.get("counter").getAsJsonObject());
			JsonArray bids = requestJson.get("bids").getAsJsonArray();
			if(!bids.isJsonNull()){
				for(JsonElement askElement : bids){
					OrderPrices book = new OrderPrices();
					JsonObject askObject = askElement.getAsJsonObject(); 
					book.setFromPrice(askObject.get("amount").getAsString());
					book.setToPrice(askObject.get("price").getAsString());
					orderBook.getBuyPrices().add(book);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
		    String sellerUrl = orderBookUrlBuilder(buyAsset.getType(),sellAsset.getType(),  sellingAssetCode,buyingAssetCode, buyingAssetIssuer, sellingAssetIssuer);
		    response = new URL(sellerUrl).openStream();
			String returnString = IOUtils.toString(response);
			JsonElement element = gson.fromJson (returnString, JsonElement.class);
			JsonObject requestJson = element.getAsJsonObject();
			JsonArray asks = requestJson.get("bids").getAsJsonArray();
			if(null == orderBook){
				orderBook = new OrderBook(requestJson.get("base").getAsJsonObject(),requestJson.get("counter").getAsJsonObject());
			}
			if(!asks.isJsonNull()){
				for(JsonElement askElement : asks){
					OrderPrices book = new OrderPrices();
					JsonObject askObject = askElement.getAsJsonObject(); 
					book.setFromPrice(askObject.get("price").getAsString());
					book.setToPrice(askObject.get("amount").getAsString());
					orderBook.getSellPrices().add(book);
				}
			}
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderBook;
	}
	
	private String orderBookUrlBuilder (String sellingAssetType, String buyingAssetType, String buyingAssetCode,
			String sellingAssetCode, String buyingAssetIssuer, String sellingAssetIssuer){
		StringBuilder stellarUrl = new StringBuilder(network);
		stellarUrl.append("/order_book?");
		stellarUrl.append("selling_asset_type=");
		stellarUrl.append(sellingAssetType);
		if (!sellingAssetType.equalsIgnoreCase("native")) {			
			stellarUrl.append("&selling_asset_code=");
			stellarUrl.append(sellingAssetCode);
			stellarUrl.append("&selling_asset_issuer=");
			stellarUrl.append(sellingAssetIssuer);
		}
		stellarUrl.append("&buying_asset_type=");
		stellarUrl.append(buyingAssetType);
		if (!buyingAssetType.equalsIgnoreCase("native")) {
			stellarUrl.append("&buying_asset_code=");
			stellarUrl.append(buyingAssetCode);
			stellarUrl.append("&buying_asset_issuer=");
			stellarUrl.append(buyingAssetIssuer);
		}
		return stellarUrl.toString();
	}
}
