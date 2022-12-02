package com.floreantpos.model.mybatis;

public class HCReturnListDetailM {
	
	private String productName;
	private Double transferQty;
	private Double returnQty;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Double getTransferQty() {
		return transferQty;
	}
	public void setTransferQty(Double transferQty) {
		this.transferQty = transferQty;
	}
	public Double getReturnQty() {
		return returnQty;
	}
	public void setReturnQty(Double returnQty) {
		this.returnQty = returnQty;
	}
	
	
}
