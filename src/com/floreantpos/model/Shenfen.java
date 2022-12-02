package com.floreantpos.model;

import java.util.HashMap;
import java.util.Map;

public class Shenfen {
	
	private int id;
	private String branchName;
	private String yanZhengMa;
	private String sheBeiHao;
	private String zhuCeZhongLei;
	private String sheBeiZhongLei;
	
	private long chuangJianShiJian;
	private long qiXian;
	private long zuiHouGenXinChengGong;
	private long zuiHouGenXin;
	private String genXinZhuangTai;
	
	private String storeName;
	private String customerId;
	private String branchId;
	private String terminalId;
	
	private String brand;
	private String model;
	private String os;
	private String osVersion;
	
	public Map<String, Object> toMap(){
		Map<String, Object> map = new HashMap<String, Object> ();
		map.put("id", this.id);
		map.put("yanZhengMa", this.yanZhengMa);
		map.put("sheBeiHao", this.sheBeiHao);
		map.put("zhuCeZhongLei", this.zhuCeZhongLei);
		map.put("sheBeiZhongLei", this.sheBeiZhongLei);
		map.put("chuangJianShiJian", this.chuangJianShiJian);
		map.put("qiXian", this.qiXian);
		map.put("zuiHouGenXin", this.zuiHouGenXin);
		map.put("genXinZhuangTai", this.genXinZhuangTai);
		map.put("storeName", this.storeName);
		map.put("customerId", this.customerId);
		map.put("branchId", this.branchId);
		map.put("terminalId", this.terminalId);
		return map;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public long getZuiHouGenXin() {
		return zuiHouGenXin;
	}
	public void setZuiHouGenXin(long zuiHouGenXin) {
		this.zuiHouGenXin = zuiHouGenXin;
	}
	public long getChuangJianShiJian() {
		return chuangJianShiJian;
	}
	public void setChuangJianShiJian(long chuangJianShiJian) {
		this.chuangJianShiJian = chuangJianShiJian;
	}
	
	public String getYanZhengMa() {
		return yanZhengMa;
	}
	public void setYanZhengMa(String yanZhengMa) {
		this.yanZhengMa = yanZhengMa;
	}
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	/**
	 * @return the qiXian
	 */
	public long getQiXian() {
		return qiXian;
	}

	/**
	 * @param qiXian the qiXian to set
	 */
	public void setQiXian(long qiXian) {
		this.qiXian = qiXian;
	}

	/**
	 * @return the genXinZhuangTai
	 */
	public String getGenXinZhuangTai() {
		return genXinZhuangTai;
	}

	/**
	 * @param genXinZhuangTai the genXinZhuangTai to set
	 */
	public void setGenXinZhuangTai(String genXinZhuangTai) {
		this.genXinZhuangTai = genXinZhuangTai;
	}

	/**
	 * @return the sheBeiHao
	 */
	public String getSheBeiHao() {
		return sheBeiHao;
	}

	/**
	 * @param sheBeiHao the sheBeiHao to set
	 */
	public void setSheBeiHao(String sheBeiHao) {
		this.sheBeiHao = sheBeiHao;
	}

	/**
	 * @return the zhuCeZhongLei
	 */
	public String getZhuCeZhongLei() {
		return zhuCeZhongLei;
	}

	/**
	 * @param zhuCeZhongLei the zhuCeZhongLei to set
	 */
	public void setZhuCeZhongLei(String zhuCeZhongLei) {
		this.zhuCeZhongLei = zhuCeZhongLei;
	}

	/**
	 * @return the sheBeiZhongLei
	 */
	public String getSheBeiZhongLei() {
		return sheBeiZhongLei;
	}

	/**
	 * @param sheBeiZhongLei the sheBeiZhongLei to set
	 */
	public void setSheBeiZhongLei(String sheBeiZhongLei) {
		this.sheBeiZhongLei = sheBeiZhongLei;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the zuiHouGenXinChengGong
	 */
	public long getZuiHouGenXinChengGong() {
		return zuiHouGenXinChengGong;
	}

	/**
	 * @param zuiHouGenXinChengGong the zuiHouGenXinChengGong to set
	 */
	public void setZuiHouGenXinChengGong(long zuiHouGenXinChengGong) {
		this.zuiHouGenXinChengGong = zuiHouGenXinChengGong;
	}
	
	
	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @param os the os to set
	 */
	public void setOs(String os) {
		this.os = os;
	}

	/**
	 * @return the osVersion
	 */
	public String getOsVersion() {
		return osVersion;
	}

	/**
	 * @param osVersion the osVersion to set
	 */
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	
}
