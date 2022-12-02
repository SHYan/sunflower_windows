package com.floreantpos.model.mybatis;


import java.sql.Timestamp;

public class PayoutM {
    private Timestamp  transactionDate;
    private Double amount;
    private String note;
    private String recp_name;
    private String reason;
    private int reason_id;
    private int recp_id;
    private Double amountIn;
    private Double amountOut;
    
	public Timestamp getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getRecp_name() {
		return recp_name;
	}
	public void setRecp_name(String recp_name) {
		this.recp_name = recp_name;
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getReason_id() {
		return reason_id;
	}
	public void setReason_id(int reason_id) {
		this.reason_id = reason_id;
	}
	public int getRecp_id() {
		return recp_id;
	}
	public void setRecp_id(int recp_id) {
		this.recp_id = recp_id;
	}
	public Double getAmountIn() {
		return amountIn;
	}
	public void setAmountIn(Double amountIn) {
		this.amountIn = amountIn;
	}
	public Double getAmountOut() {
		return amountOut;
	}
	public void setAmountOut(Double amountOut) {
		this.amountOut = amountOut;
	}
}
