package com.floreantpos.model.mybatis;

public class HCDailyStockM {
	
	private String productName;
	private String altName;
	private String productId;
	private Double oldQty;
	private Double procurementQty;
	private Double demageQty;
	private Double loseQty;
	private Double extraQty;
	private Double returnQty;
	private Double convertQty;
	private Double transferQty;
	private Double otherQty;
	private Double sghQty = 0.0;
	private Double super1Qty= 0.0;
	private Double balanceQty;
	private String remark;
	
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the oldQty
	 */
	public Double getOldQty() {
		return oldQty;
	}
	/**
	 * @param oldQty the oldQty to set
	 */
	public void setOldQty(Double oldQty) {
		this.oldQty = oldQty;
	}
	/**
	 * @return the procurementQty
	 */
	public Double getProcurementQty() {
		return procurementQty;
	}
	/**
	 * @param procurementQty the procurementQty to set
	 */
	public void setProcurementQty(Double procurementQty) {
		this.procurementQty = procurementQty;
	}
	/**
	 * @return the demageQty
	 */
	public Double getDemageQty() {
		return demageQty;
	}
	/**
	 * @param demageQty the demageQty to set
	 */
	public void setDemageQty(Double demageQty) {
		this.demageQty = demageQty;
	}
	/**
	 * @return the loseQty
	 */
	public Double getLoseQty() {
		return loseQty;
	}
	/**
	 * @param loseQty the loseQty to set
	 */
	public void setLoseQty(Double loseQty) {
		this.loseQty = loseQty;
	}
	/**
	 * @return the extraQty
	 */
	public Double getExtraQty() {
		return extraQty;
	}
	/**
	 * @param extraQty the extraQty to set
	 */
	public void setExtraQty(Double extraQty) {
		this.extraQty = extraQty;
	}
	/**
	 * @return the returnQty
	 */
	public Double getReturnQty() {
		return returnQty;
	}
	/**
	 * @param returnQty the returnQty to set
	 */
	public void setReturnQty(Double returnQty) {
		this.returnQty = returnQty;
	}
	/**
	 * @return the convertQty
	 */
	public Double getConvertQty() {
		return convertQty;
	}
	/**
	 * @param convertQty the convertQty to set
	 */
	public void setConvertQty(Double convertQty) {
		this.convertQty = convertQty;
	}
	/**
	 * Not include SGH1(Warehouse id 1) and AND Super1(Warehouse id 6) 
	 * @return the transferQty
	 */
	public Double getTransferQty() {
		return transferQty;
	}
	/**
	 * @param transferQty the transferQty to set
	 */
	public void setTransferQty(Double transferQty) {
		this.transferQty = transferQty;
	}
	/**
	 * @return the otherQty
	 */
	public Double getOtherQty() {
		return otherQty;
	}
	/**
	 * @param otherQty the otherQty to set
	 */
	public void setOtherQty(Double otherQty) {
		this.otherQty = otherQty;
	}
	/**
	 * @return the sghQty
	 */
	public Double getSghQty() {
		return sghQty;
	}
	/**
	 * @param sghQty the sghQty to set
	 */
	public void setSghQty(Double sghQty) {
		this.sghQty = sghQty;
	}
	/**
	 * @return the super1Qty
	 */
	public Double getSuper1Qty() {
		return super1Qty;
	}
	/**
	 * @param super1Qty the super1Qty to set
	 */
	public void setSuper1Qty(Double super1Qty) {
		this.super1Qty = super1Qty;
	}
	/**
	 * @return the balanceQty
	 */
	public Double getBalanceQty() {
		return (oldQty + procurementQty + returnQty + extraQty + transferQty + demageQty + convertQty + loseQty + otherQty + sghQty+ super1Qty);
	}
	/**
	 * @param balanceQty the balanceQty to set
	 */
	public void setBalanceQty(Double balanceQty) {
		this.balanceQty = balanceQty;
	}
	public String getAltName() {
		return altName;
	}
	public void setAltName(String altName) {
		this.altName = altName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	
}
