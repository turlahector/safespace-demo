package com.stellar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.stellar.sdk.KeyPair;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class StellarUtil {
	
	public void createValuePair() {
		KeyPair pair = KeyPair.random();
		//
		System.out.println(pair);
		
		// SAV76USXIJOBMEQXPANUOQM6F5LIOTLPDIDVRJBFFE2MDJXG24TAPUU7
		//System.out.println(pair.getAccountId());
		//GCFXHS4GXL6BVUCXBWXGTITROWLVYXQKQLF4YH5O5JT3YZXCYPAFBJZB
	}
	
	public String callMethod(String method, String data, String endpoint) throws IOException {
        URL url = new URL(endpoint + "?method=" + method);
        URLConnection connection = url.openConnection();
        //connection.addRequestProperty("X-WSSE", getHeader(username,password));

        connection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();

        InputStream in = connection.getInputStream();
        BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        StringBuffer sBuffer = new StringBuffer();
        String inputLine;
        while ((inputLine = res.readLine()) != null)
            sBuffer.append(inputLine);

        res.close();

        return sBuffer.toString();
    }
	
	 private static String getHeader(String username, String password) throws UnsupportedEncodingException {
	        byte[] nonceB = generateNonce();
	        String nonce = base64Encode(nonceB);
	        String created = generateTimestamp();
	        String password64 = getBase64Digest(nonceB, created.getBytes("UTF-8"), password.getBytes("UTF-8"));
	        StringBuffer header = new StringBuffer("UsernameToken Username=\"");
	        header.append(username);
	        header.append("\", ");
	        header.append("PasswordDigest=\"");
	        header.append(password64.trim());
	        header.append("\", ");
	        header.append("Nonce=\"");
	        header.append(nonce.trim());
	        header.append("\", ");
	        header.append("Created=\"");
	        header.append(created);
	        header.append("\"");
	        return header.toString();
	 }
	 
	 private static byte[] generateNonce() {
	        String nonce = Long.toString(new Date().getTime());
	        return nonce.getBytes();
	    }

	    private static String generateTimestamp() {
	        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        return dateFormatter.format(new Date());
	    }

	    private static synchronized String getBase64Digest(byte[] nonce, byte[] created, byte[] password) {
	      try {
	        MessageDigest messageDigester = MessageDigest.getInstance("SHA-1");
	        // SHA-1 ( nonce + created + password )
	        messageDigester.reset();
	        messageDigester.update(nonce);
	        messageDigester.update(created);
	        messageDigester.update(password);
	        return base64Encode(messageDigester.digest());
	      } catch (java.security.NoSuchAlgorithmException e) {
	        throw new RuntimeException(e);
	      }
	    }

	    private static String base64Encode(byte[] bytes) {
	      return Base64Coder.encodeLines(bytes);
	    }

}
