package com.floreantpos.util;

import java.io.*;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

public class ServerRequest {

	private final String url;
	private static String account;
	private static String password;
	UsernamePasswordCredentials credentials;
	BasicScheme scheme = new BasicScheme();
	
	public ServerRequest(String httpUrl, String syncAccount, String syncPassword) {
		url = httpUrl;
		account = syncAccount;
		password = syncPassword;
		credentials = new UsernamePasswordCredentials(account, password);
	}
	
	public String postRequest(List<? extends NameValuePair> postData) {
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			Header authorizationHeader = scheme.authenticate(credentials, httpPost);
	        httpPost.setHeader(authorizationHeader);
	        
	        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(postData);
	        httpPost.setEntity(urlEncodedFormEntity);
	        
	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        
	        if (httpResponse.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream = httpResponse.getEntity()
						.getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				return stringBuilder.toString();
			}else {
				
			}

		} catch (Exception e) {
		}
		return "";
	}
	
	public String readJson() {
	
	
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			Header authorizationHeader = scheme.authenticate(credentials, httpGet);
	        httpGet.setHeader(authorizationHeader);
	        
			HttpResponse httpResponse = client.execute(httpGet);			
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				return stringBuilder.toString();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
		return "";
	}


}
