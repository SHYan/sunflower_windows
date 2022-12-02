package com.floreantpos.model.mybatis;

import java.util.List;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class HCReturnListM {
	
	private String warehouse;
	private int warehouseId;
	private JRBeanCollectionDataSource itemList;
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public JRBeanCollectionDataSource getItemList() {
		return itemList;
	}
	public void setItemList(JRBeanCollectionDataSource itemList) {
		this.itemList = itemList;
	}
	
	
	
	
	
}
