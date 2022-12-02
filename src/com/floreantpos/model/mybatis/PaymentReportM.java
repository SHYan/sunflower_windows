package com.floreantpos.model.mybatis;

public class PaymentReportM {
	//private String id;
	private String factdate;
	private Integer id;
	private Double item_subtotal;
	private Double service_charge_subtotal;
	private Double tax_subtotal;
	private Double discount_subtotal;
	private Double sale;
	private Double due_subtotal;
	private String paymentType;
	private String paymentName;
	private String paymentRef;
	private Long qty_subtotal;
	/**
	 * @return the factdate
	 */
	public String getFactdate() {
		return factdate;
	}
	/**
	 * @param factdate the factdate to set
	 */
	public void setFactdate(String factdate) {
		this.factdate = factdate;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the item_subtotal
	 */
	public Double getItem_subtotal() {
		return item_subtotal;
	}
	/**
	 * @param item_subtotal the item_subtotal to set
	 */
	public void setItem_subtotal(Double item_subtotal) {
		this.item_subtotal = item_subtotal;
	}
	/**
	 * @return the service_charge_subtotal
	 */
	public Double getService_charge_subtotal() {
		return service_charge_subtotal;
	}
	/**
	 * @param service_charge_subtotal the service_charge_subtotal to set
	 */
	public void setService_charge_subtotal(Double service_charge_subtotal) {
		this.service_charge_subtotal = service_charge_subtotal;
	}
	/**
	 * @return the tax_subtotal
	 */
	public Double getTax_subtotal() {
		return tax_subtotal;
	}
	/**
	 * @param tax_subtotal the tax_subtotal to set
	 */
	public void setTax_subtotal(Double tax_subtotal) {
		this.tax_subtotal = tax_subtotal;
	}
	/**
	 * @return the discount_subtotal
	 */
	public Double getDiscount_subtotal() {
		return discount_subtotal;
	}
	/**
	 * @param discount_subtotal the discount_subtotal to set
	 */
	public void setDiscount_subtotal(Double discount_subtotal) {
		this.discount_subtotal = discount_subtotal;
	}
	/**
	 * @return the sale
	 */
	public Double getSale() {
		return sale;
	}
	/**
	 * @param sale the sale to set
	 */
	public void setSale(Double sale) {
		this.sale = sale;
	}
	/**
	 * @return the due_subtotal
	 */
	public Double getDue_subtotal() {
		return due_subtotal;
	}
	/**
	 * @param due_subtotal the due_subtotal to set
	 */
	public void setDue_subtotal(Double due_subtotal) {
		this.due_subtotal = due_subtotal;
	}
	/**
	 * @return the paymentType
	 */
	public String getPaymentType() {
		return paymentType;
	}
	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	/**
	 * @return the paymentName
	 */
	public String getPaymentName() {
		return paymentName;
	}
	/**
	 * @param paymentName the paymentName to set
	 */
	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}
	/**
	 * @return the paymentRef
	 */
	public String getPaymentRef() {
		return paymentRef;
	}
	/**
	 * @param paymentRef the paymentRef to set
	 */
	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}
	/**
	 * @return the qty_subtotal
	 */
	public Long getQty_subtotal() {
		return qty_subtotal;
	}
	/**
	 * @param qty_subtotal the qty_subtotal to set
	 */
	public void setQty_subtotal(Long qty_subtotal) {
		this.qty_subtotal = qty_subtotal;
	}
	
	
}
