/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.ui.model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.main.Application;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ComboItemSelectionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;

public class StockControlDialog  extends TransparentPanel implements TableModelListener {//<InventoryTransaction> implements ActionListener, ChangeListener {
	private JPanel panel;
	private StockControlTableModel tableModel;
	private JComboBox cbGroup, categoryListBox;
	private JTextField tfName;
	private javax.swing.JComboBox cbVendor, cbWarehouse;
	private javax.swing.JComboBox cbType;
	private JLabel lblWarehouse, lblVendor;
	private JXDatePicker dpStartDate;
	
	private JXTable table = new JXTable();

	public StockControlDialog() {
		//setTitle("Inventory Control"); 
	//	tableModel = new StockControlTableModel();
		showTable();
		add(createButtonPanel(), BorderLayout.SOUTH);
		add(buildSearchForm(), BorderLayout.NORTH);
	}
	
	private void showTable(){
		tableModel = new StockControlTableModel();
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new CustomCellRenderer());
		table.setRowHeight(30);
		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table),BorderLayout.CENTER);
		resizeColumnWidth(table);
		/*
		TableRowSorter<StockControlTableModel> sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();*/
		
	}
	 
	private JPanel buildSearchForm() {
		panel = new JPanel();
		panel.setLayout(new MigLayout("", "[]10[]20[]10[]20[]10[]20[]10[]20[]", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		JLabel lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getDeafultDate();
		dpStartDate.setPreferredSize(new Dimension(115, 30));
		panel.add(lblFromDate, "align label"); //$NON-NLS-1$
		panel.add(dpStartDate);
		
		JLabel lblName = new JLabel(Messages.getString("MenuItemExplorer.0")); //$NON-NLS-1$
		panel.add(lblName, "align label"); //$NON-NLS-1$
		
		tfName = new JTextField(15);
		tfName.setPreferredSize(new Dimension(120, 30));
		panel.add(tfName);
		
		try {
			panel.add(new JLabel("Category :"));
			List<String> cateList = IBatisFactory.selectList("Report.getCategory_List", null);
			cateList.add(0, "ALL");
			categoryListBox = new JComboBox();
			categoryListBox.setPreferredSize(new Dimension(115, 30));
			categoryListBox.setModel(new ListComboBoxModel(cateList));
			panel.add(categoryListBox);
			
			JLabel lblGroup = new JLabel(Messages.getString("MenuItemExplorer.1")); //$NON-NLS-1$
			List<MenuGroup> menuGroupList = MenuGroupDAO.getInstance().findAll();
			cbGroup = new JComboBox();
			cbGroup.addItem("ALL"); //$NON-NLS-1$
			//cbGroup.addItem("<None>");
			for (MenuGroup s : menuGroupList) {
				cbGroup.addItem(s);
			}
	
			/*List<MenuGroup> foodGroups = MenuGroupDAO.getInstance().findAll();
			cbGroup = new JComboBox();
			cbGroup.setModel(new ComboBoxModel(foodGroups));*/

			JButton searchBttn = new JButton(Messages.getString("MenuItemExplorer.3")); //$NON-NLS-1$
			
			
			cbGroup.setPreferredSize(new Dimension(120, 30));
			searchBttn.setPreferredSize(new Dimension(60, 30));
			
			panel.add(lblGroup);
			panel.add(cbGroup);
			panel.add(searchBttn);
			
			Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Search"); //$NON-NLS-1$
			title.setTitleJustification(TitledBorder.LEFT);
			panel.setBorder(title);	
			searchBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				searchItem();
				}
			});
	
			tfName.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					searchItem();
				}
			});
			
		} catch (Throwable x) {
			BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
		}
		return panel;
	}

	private void searchItem() {
		String txName = tfName.getText();
		tableModel = new StockControlTableModel(txName, categoryListBox.getSelectedItem().toString(), cbGroup.getSelectedItem().toString());
		table.setModel(tableModel);
		table.tableChanged(new TableModelEvent(table.getModel())) ;

	}
	
	private TransparentPanel createButtonPanel() {
	
		lblVendor = new JLabel("Vendor");
		cbVendor = new javax.swing.JComboBox();
		cbVendor.setPreferredSize(new Dimension(198, 30));
		InventoryVendorDAO vendorDAO = new InventoryVendorDAO();
		List<InventoryVendor> vendor = vendorDAO.findAll();
		cbVendor.setModel(new ComboBoxModel(vendor));
		
		JLabel lblType = new JLabel("Type"); //$NON-NLS-1$
		cbType = new javax.swing.JComboBox();
		cbType.addItem("Procurement"); 
		cbType.addItem("Demage"); 
		cbType.addItem("Inventory");
		cbType.addItem("Transfer");
		cbType.addItem("Return");
		cbType.addItem("Convert");
		cbType.addItem("Lose");
		cbType.addItem("Extra");
		cbType.addItem("Other"); 
		cbType.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        if(cbType.getSelectedItem().toString().equals("Transfer") || 
		        		cbType.getSelectedItem().toString().equals("Return") ||
		        		cbType.getSelectedItem().toString().equals("Other")) {
		        	cbWarehouse.setEnabled(true);
		        }else {
		        	cbWarehouse.setEnabled(false);
		        }
		        if(cbType.getSelectedItem().toString().equals("Procurement")) {
		        	cbVendor.setEnabled(true);
		        }else {
		        	cbVendor.setEnabled(false);
		        }
		        	
		    }
		});
		cbType.setPreferredSize(new Dimension(198, 30));
		
		lblWarehouse = new JLabel("Transfer to / Return from");
		cbWarehouse = new javax.swing.JComboBox();
		cbWarehouse.setPreferredSize(new Dimension(198, 30));
		InventoryWarehouseDAO warehouseDAO = new InventoryWarehouseDAO();
		List<InventoryWarehouse> warehouse = warehouseDAO.findAll();
		cbWarehouse.setModel(new ComboBoxModel(warehouse));
		
		JButton saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(60, 30));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveStock();
					tableModel = new StockControlTableModel();
					//tableModel = new StockControlTableModel(txName, groupId);
					table.setModel(tableModel);
					table.tableChanged(new TableModelEvent(table.getModel())) ;
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"BackOffice Setting", "Stock Transaction");
					
				} catch (NumberFormatException e1) {
					POSMessageDialog.showError(StockControlDialog.this, Messages.getString("MenuItemExplorer.11")); //$NON-NLS-1$
					return;
				} catch (Exception e2) {
					BOMessageDialog.showError(StockControlDialog.this, POSConstants.ERROR_MESSAGE, e2);
					return;
				}
			}
		});
		
		TransparentPanel panel = new TransparentPanel();
		panel.setLayout(new MigLayout("", "[]10[]20[]10[]20[]", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		panel.add(lblType); 
		panel.add(cbType); 
		panel.add(lblWarehouse);
		panel.add(cbWarehouse);
		panel.add(lblVendor); 
		panel.add(cbVendor); 
		cbWarehouse.setEnabled(false);
    	//cbVendor.setEnabled(false);
		panel.add(saveButton);
		return panel;
	}
	
	private void saveStock() {// TODO Auto-generated method stub
		try{
			
			Date selectDate = dpStartDate.getDate();
			Timestamp ts = new Timestamp(selectDate.getTime());
			Calendar cal = Calendar.getInstance();
			int hr = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			cal.clear();
		    cal.setTimeInMillis(ts.getTime());
		    cal.set(Calendar.HOUR_OF_DAY, hr);
		    cal.set(Calendar.MINUTE, min);
		    cal.set(Calendar.SECOND, sec);
		    cal.set(Calendar.MILLISECOND, 0);
			
			
			
			InventoryVendor vendor = (InventoryVendor) cbVendor.getSelectedItem();
			InventoryWarehouse warehouse = (InventoryWarehouse) cbWarehouse.getSelectedItem();
			int vendor_id  = 0, warehouse_id = 0;
			if(cbType.getSelectedItem().toString() == "Procurement" &&  vendor!=null) {
				vendor_id = vendor.getId();
			}
			if((cbType.getSelectedItem().toString().equals("Transfer") || 
					cbType.getSelectedItem().toString().equals("Return") ||
					cbType.getSelectedItem().toString().equals("Other")) &&  warehouse!=null) {
				warehouse_id = warehouse.getId();
			}
			for(int i = 0;i<tableModel.getRowCount();i++){
				if(tableModel.getValueAt(i, 7).toString() == "true"){
					HashMap map = new HashMap();
					map.put("item_id", Integer.parseInt(tableModel.getValueAt(i, 0).toString()));
					map.put("tran_type", cbType.getSelectedItem().toString());
					map.put("old_qty", Double.parseDouble(tableModel.getValueAt(i, 3).toString()));
					if(vendor_id != 0) map.put("vendor_id", vendor_id);
					if(warehouse_id != 0) map.put("warehouse_id", warehouse_id);
					
					double qty = 0.0;
					try {
						qty = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
						qty = ((cbType.getSelectedItem().toString().equals("Demage") || cbType.getSelectedItem().toString().equals("Transfer")
								|| cbType.getSelectedItem().toString().equals("Convert")
								|| cbType.getSelectedItem().toString().equals("Lose")) ? (-1)*qty : qty);
						map.put("qty", qty);
						map.put("price", Double.parseDouble(tableModel.getValueAt(i, 5).toString()));
					}catch(Exception e) {
						
					}
					
					if(!cbType.getSelectedItem().toString().equals("Inventory") && qty==0) continue;
					
					if(tableModel.getValueAt(i, 6) != null)
						map.put("remark", tableModel.getValueAt(i, 6).toString());
					
					map.put("transaction_date", new Timestamp(cal.getTimeInMillis()));
					
					IBatisFactory.insertSQL("inventory_transaction.insert_inventory_transaction", map);
					IBatisFactory.updateSQL("inventory_transaction.update_inventory_transaction", map);
					
				}
			}
			POSMessageDialog.showMessage("Save Successful");
			//searchItem();
		}catch (Exception e1) {
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e1);
		}
	}
	
	protected MenuGroup getSelectedMenuGroup(MenuGroup defaultValue) {
		List<MenuGroup> menuGroups = MenuGroupDAO.getInstance().findAll();
		ComboItemSelectionDialog dialog = new ComboItemSelectionDialog("SELECT GROUP", "Menu Group", menuGroups, false);
		dialog.setSelectedItem(defaultValue);
		dialog.setVisibleNewButton(true);
		dialog.pack();
		dialog.open();
		
		if (dialog.isCanceled())
			return null;
		
		if (dialog.isNewItem()) {
			MenuGroup foodGroup = new MenuGroup();
			MenuGroupForm editor = new MenuGroupForm(foodGroup);
			BeanEditorDialog editorDialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
			editorDialog.open();
			if (editorDialog.isCanceled())
				return null;
			return getSelectedMenuGroup(foodGroup);
		}
		return (MenuGroup) dialog.getSelectedItem();
	}
	
	public void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			columnModel.getColumn(column).setPreferredWidth((Integer) getColumnWidth().get(column));
		}
	}
	
	private List getColumnWidth() {
		List<Integer> columnWidth = new ArrayList();
		columnWidth.add(30);
		columnWidth.add(200);
		columnWidth.add(200);
		columnWidth.add(70);
		columnWidth.add(70);
		columnWidth.add(70);
		columnWidth.add(150);
		columnWidth.add(50);
		return columnWidth;
	}
	
	class StockControlTableModel extends AbstractTableModel {
		String[] columnNames = { "ID","NAME","TRANSLATED NAME","STOCK AMOUNT","QUANTITY","PRICE","REMARK","" };
		Object[][] data = {};
		
		public StockControlTableModel(){
			//generateTableData("", "", 0);
		}
		
		public  StockControlTableModel (String txName, String categoryName, String groupName){
			generateTableData(txName, categoryName, groupName);
		}
	
		public void generateTableData(String txName, String categoryName, String groupName){
			HashMap map = new HashMap();
			map.put("item_name", txName);
			if(!categoryName.equals("ALL")) map.put("categoryName", categoryName); 
			if(!groupName.equals("ALL")) map.put("groupName", groupName); 
			List<MenuItem> findAll = IBatisFactory.selectList("inventory_transaction.get_menu_item", map);
			
			int rows = findAll.size();
			data = new Object[rows][8];
			int i =0 ;
			for(MenuItem menu : findAll){
				
				data[i][0] = menu.getId();
				data[i][1] = menu.getName();
				data[i][2] = menu.getTranslatedName();
				data[i][3] = menu.getStockAmount();
				//data[i][4] = 0.0;
				//data[i][5] = 0.0;
				//data[i][6] = "";
				data[i][7] = Boolean.FALSE;
				i++;
			}
			
			fireTableDataChanged();
		}
		
		public int getRowCount() {
			if (data == null) {
				return 0;
			}
			return data.length;
		}
		
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if( columnIndex == 4 || columnIndex == 5 || columnIndex == 6 || columnIndex == 7)
				return true;
			return false;
		}
		
		public Class getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Integer.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return Double.class;
                case 4:
                    return Double.class;
                case 5:
                    return Double.class;
                case 6:
                    return String.class;
                default:
                    return Boolean.class;
            }
        }
	
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (data == null)
				return ""; 
			return data[rowIndex][columnIndex];
		}
		
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if(columnIndex == 7){
				data[rowIndex][columnIndex] = !(Boolean)data[rowIndex][columnIndex];
			}
			else{
				if(value==null || String.valueOf(value).trim().equals("")) return;
				data[rowIndex][columnIndex] = value;
				fireTableCellUpdated(rowIndex, columnIndex); 
				data[rowIndex][7] = true;
				fireTableCellUpdated(rowIndex, 7); 
				
			}
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		//showTable();
		
	}
}
