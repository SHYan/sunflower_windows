package com.floreantpos.main;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Shenfen;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.VoidReason;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao.VoidReasonDAO;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.services.PosWebService;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.NotesDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class Shenfenyanzheng {
	Terminal terminal;
	String os, osversion, brand, model;
	
	public Shenfenyanzheng() {
		getOSinfo();
	}

	public Map<String, Object> convertObject2Map(Object obj){
		Map map = new HashMap();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try { 
            	map.put(field.getName(), field.get(obj));
            } catch (Exception e) { 
            	
            }
        }
        return map;
	}
	
	private String showTerminalLocationDialog() {
		try {
			NotesDialog dialog = new NotesDialog();
			dialog.setTitle(Messages.getString("TerminalConfigurationView.24"));
			dialog.pack();
			dialog.open();

			if (!dialog.isCanceled()) {
				return dialog.getNote();
				
			}
		} catch (Throwable e) {
			POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
		}
		return "";
	}
	
	public int sfyz(Terminal terminal) {
		this.terminal = terminal;
		boolean newFlag = false, syncFailFlag = true;
		
		long currentTimestamp = System.currentTimeMillis()/1000;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("zuiHouGenXin", currentTimestamp);
		map.put("uid", this.terminal.getTerminalKey());
		
		Restaurant restaurant = RestaurantDAO.getRestaurant();
		map.put("branchName", restaurant.getName());
		/*
		if(restaurant == null || restaurant.getName().equals("Sunflower POS")) 
			return 0;
		*/
		PosWebService service = new PosWebService();
		Shenfen sf = (Shenfen) IBatisFactory.selectOneSQL("Shenfen.get_shenfen", map);
		if(sf==null) {
			newFlag = true;
			sf = new Shenfen();
			sf.setStoreName(restaurant.getName());
			map.put("chuangJianShiJian", currentTimestamp);
			map.put("qiXian", currentTimestamp+604800);//7*24*60*60  7days
		}else {
			map.put("id", sf.getId());
			map.put("clientCreated", sf.getChuangJianShiJian());
			map.put("key", sf.getYanZhengMa());
			map.put("qiXian", sf.getQiXian());
		}
		if(this.terminal.getLocation() == null) {
			String loc = showTerminalLocationDialog();
			if(loc!=null && !loc.equals("") && StringUtils.countMatches(loc, ":")==2) {
				this.terminal.setLocation(loc.trim());
				map.put("tempId", this.terminal.getId());
				map.put("tempLocation", loc);
				IBatisFactory.updateSQL("Shenfen.update_terminal_location", map);
			}
		}
		
		if(this.terminal.getLocation() != null) {
			String [] temp = this.terminal.getLocation().split(":");
			if(temp!=null && temp.length==3) {
				map.put("cid", temp[0]);
				map.put("bid", temp[1]);
				map.put("tid", temp[2]);
				
			}
		}

		map.put("os", os);
		map.put("osVersion", osversion);
		map.put("brand", brand);
		map.put("model", model);
		map.put("sheBeiZhongLei", (os == "Android" || os == "iOS") ? "mobile" : "pc");
		
		map.put("request_type", "quick_status_check");
		JSONObject jsonObj = null;
		String retJsonStr = service.shenFenYanZheng(map);
		//System.out.println(retJsonStr);
		if(retJsonStr==null || retJsonStr.equals("") || retJsonStr.length()<40) {
			map.put("genXinZhuangTai", retJsonStr);
		} else {
			map.put("zuiHouGenXinChengGong", currentTimestamp);
			map.put("genXinZhuangTai", "Success");
			try {
				jsonObj = new JSONObject(retJsonStr);
				String status = (String) jsonObj.get("response_status");
				if(status.equals("success") && jsonObj.get("response_data")!=null){
					JSONObject responseData = new JSONObject(jsonObj.get("response_data").toString());
					Map<String, Object> sfMap = responseData.toMap();

					map.put("yanZhengMa", sfMap.get("lickey"));
					map.put("qiXian", Long.parseLong(sfMap.get("expire_time").toString()));
					map.put("zhuCeZhongLei", sfMap.get("lictype").toString());
					sfMap.clear();
					syncFailFlag = false;
				}else {
					map.put("qiXian", currentTimestamp+604800);//7*24*60*60  7days change to temp
					JOptionPane.showMessageDialog(null, jsonObj.get("error_code")+":"+jsonObj.get("response_msg"));
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(newFlag)
			IBatisFactory.insertSQL("Shenfen.insert_shenfen", map);
		else {
			if(syncFailFlag) {
				IBatisFactory.updateSQL("Shenfen.update_shenfen_sync_fail", map);
			}
			else
				IBatisFactory.updateSQL("Shenfen.update_shenfen", map);
		}
		if(map.get("qiXian")!=null) {
			long zuihouriqi = (long) map.get("qiXian");
			TerminalConfig.setExpTime(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (zuihouriqi*1000)));
			return (int) (((zuihouriqi - currentTimestamp) / 86400)+1);
		}
		map.clear();
		return 0;
	}
	
	public void getOSinfo() {
		if(os != null && os!="") return;
		String cmd = "systeminfo";
		Runtime runtime = Runtime.getRuntime();
		try {
		    Process process = runtime.exec(cmd);
		    
		    BufferedReader systemInformationReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = systemInformationReader.readLine()) != null)
		    {
		    	if(line.startsWith("OS Name:"))
		    		os = line.replace("OS Name:", "").trim();
		    	else if(line.startsWith("OS Version:"))
		    		osversion = line.replace("OS Version:", "").trim();
		    	else if(line.startsWith("System Manufacturer:"))
		    		brand = line.replace("System Manufacturer:", "").trim();
		    	else if(line.startsWith("System Model:"))
		    		model = line.replace("System Model:", "").trim();
		    }
		    
		}catch(Exception e) {
			
		}
		
	}
/*
	public boolean licenseValidation(){
		String addr = TerminalUtil.getSystemUID();
	    String mac_addr = getMacAddress();
	    if ((mac_addr != "") && 
	      (checkValid(mac_addr))) {
	      return true;
	    }
	    if ((addr != "") && 
	      (checkValid(addr))) {
	      return true;
	    }
	    if(mac_addr== "" &&  RestaurantDAO.getInstance()!=null) {
			String restName =  RestaurantDAO.getInstance().getRestaurant().getName();
			if(restName!=null && restName!="" && checkValid(restName)) return true;
		}else return checkValid("0226981446");
		return false;
	}
	
	public  String getMacAddress(){
		try{
			InetAddress address = InetAddress.getLocalHost();
	
		    NetworkInterface ni = NetworkInterface.getByInetAddress(address);
		    byte[] mac = ni.getHardwareAddress();
		    String macAddress = "";
		    StringBuilder sb = new StringBuilder();
		    for (int i = 0; i < mac.length; i++) {
		    	macAddress = sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "")).toString();
		    }
		    return macAddress;
		}catch(Exception e){
			 return "";
		}
	}
	
	public String getPre(String addr){
		String [] k = addr.split("-");
		String regKey = "";
		for(int i=0; i<k.length;i++){
			if(k[i].length() <= 4){
				if(regKey == "")
					regKey = k[i].substring(0, 2);
				else
					regKey = regKey +k[i].substring(0, 2);
			}
			else{
				if(regKey == "")
					regKey = k[i].substring(0, 3);
				else
					regKey = regKey +k[i].substring(0, 3);
			}
		}
		return regKey;
	}
	
	public  boolean checkValid(String addr){
		try{
			
			List<License> l_key;
			HashMap map = new HashMap();
			l_key = IBatisFactory.selectList("license.get_license", map);
			if(l_key.size() == 0){
				return false;
			}
			else{
				String regKey = getPre(addr);
				boolean is_client = false;
				for(License lic : l_key){
					if(lic.getLkey().equals(encrypt_key(regKey, server_key).substring(0,10)) && lic.getMaddress().equals(addr)){
						return true;
					}
					if(lic.getLkey().equals(encrypt_key(regKey, client_key).substring(0,10)) && lic.getMaddress().equals(addr)){
						is_client = true;
						break;
					}
				}
				if(is_client){
					for(License lic : l_key){
						if(lic.getKey_type()){
							String regKey1 = getPre(lic.getMaddress());
							if(lic.getLkey().equals(encrypt_key(regKey1, server_key).substring(0,10))) { // && lic.getMaddress().equals(lic.getMaddress())){
								return true;
							}
						}
					}
				}
				return false;
			}
		}
		catch(Exception ex)
		{	DatabaseConfigurationDialog dialog = new DatabaseConfigurationDialog();
			dialog.pack();
			dialog.open();
		}
		return false;
	}
	
	public String encrypt_key(String value, String use_key) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(use_key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			//String encStr = Base64.encodeBase64String(encrypted);
			String encStr =  Base64.getEncoder().encodeToString(encrypted);
			
			return encStr.toUpperCase();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public boolean registerKey(String name, String key, Boolean key_type){
		
		Calendar currentTime = Calendar.getInstance();
		
		HashMap map = new HashMap();
		map.put("maddress", name);
		map.put("lkey", key); //enc key
		map.put("key_type", key_type); 
		map.put("create_date", currentTime.getTime()); 
		IBatisFactory.insertSQL("license.delete_license", map); //delete old one
		IBatisFactory.insertSQL("license.insert_license", map);
		return true;
	}
	
	*/
}
