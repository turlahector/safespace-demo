package com.stellar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.stellar.sdk.KeyPair;

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
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        //connection.addRequestProperty("X-WSSE", getHeader(username,password));
        
        connection.setDoOutput(true);


        InputStream in = connection.getInputStream();
        BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        StringBuffer sBuffer = new StringBuffer();
        String inputLine;
        while ((inputLine = res.readLine()) != null)
            sBuffer.append(inputLine);

        res.close();

        return sBuffer.toString();
    }

}
