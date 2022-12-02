package com.floreantpos.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JOptionPane;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.floreantpos.PosLog;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.Shenfen;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class PosWebService {

	//private static final String SERVICE_URL = TerminalConfig.getWebServiceUrl();
	private final String SHENFEN_URL = "http://localhost/reg_check.php";
	
	private static final String yonghu = "sunflowerpos";
	private static final String mima = "sunflower123456";
	
	MultivaluedMap multimap;
	public PosWebService() {
	}
	
	public String postData(String postUrl, Map<String, Object> map) {
		HttpURLConnection conn = null;
		String result = "bad connection";
		try {
			String upJson = new JSONObject(map).toString();
			String auth = yonghu + ":" + mima;
	        //System.out.println(upJson);
	        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
	        String authHeaderValue = "Basic " + new String(encodedAuth);
	        
	        URL url = new URL(postUrl);
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(5000);
	        conn.setConnectTimeout(5000);
	        //conn.setRequestProperty("Acceptcharset", "en-us");
	        //conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	        //conn.setRequestProperty("charset", "EN-US");
	        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setRequestProperty("Authorization", authHeaderValue);
	        conn.setRequestMethod("POST");
	        
	        OutputStream os = conn.getOutputStream();
	        os.write(upJson.getBytes("UTF-8"));
	        os.close();
	        
	        int status = conn.getResponseCode();
	        if (status == 408) {
	        	result = "NETWORK TIMEOUT 408";
			}else if (status == 404) {
				result = "PAGE ACCESS ERROR 404";
			}else if (status != 200) {
				result = "NETWORK ERROR "+status;
			}else {
	            InputStream in = new BufferedInputStream(conn.getInputStream());
		        result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
	            in.close();
			}
	        conn.disconnect();
	        return result;
			
		} catch (Exception e) {
			PosLog.error(getClass(), e);
			return "License Server Connection Error!";
		}
	}
	
	public String shenFenYanZheng(Map<String, Object> map) {
		return postData(SHENFEN_URL, map);
	}
	
	public String updateCheck(Map<String, Object> map) {
		return postData(SHENFEN_URL, map);
	}

	public String getAvailableNewVersions(String terminalKey, String currentPosVersion) {
		try {
			Client client = Client.create();
			client.getProperties();
			client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter("sunflower", "FuXun12345"));
			MultivaluedMap map = new MultivaluedMapImpl();
			map.add("terminal_key", terminalKey);
			map.add("pos_version", currentPosVersion);
			//WebResource webResource = client.resource(SERVICE_URL + "/public/posuser/update");
			WebResource webResource = client.resource(SHENFEN_URL);
			ClientResponse response = webResource.accept("application/json").post(ClientResponse.class, map);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			String versionInfo = response.getEntity(String.class);
			
			return versionInfo;

		} catch (Exception e) {
			//PosLog.error(getClass(), e);
		}
		return null;
	}
	

	public MultivaluedMap<String, Object> convertObject2Map(Object obj){
		
		MultivaluedMap map = new MultivaluedMapImpl();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try { 
            	map.add(field.getName(), field.get(obj));
            } catch (Exception e) { 
            	
            }
        }
        return map;

	}

}
