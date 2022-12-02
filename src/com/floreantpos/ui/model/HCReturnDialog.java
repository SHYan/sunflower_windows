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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.mybatis.MenuItemM;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;

public class HCReturnDialog  extends TransparentPanel implements TableModelListener {//<InventoryTransaction> implements ActionListener, ChangeListener {
	private JPanel panel;
	private StockControlTableModel tableModel;
	private javax.swing.JComboBox cbWarehouse;
	private JComboBox categoryListBox, cbGroup;
	private JLabel lblWarehouse;
	JButton totalButton;
	private JCheckBox onlyTransfer;
	
	private JXTable table = new JXTable();
	private JXDatePicker dpStartDate;

	public HCReturnDialog() {
		showTable();
		add(createButtonPanel(), BorderLayout.SOUTH);
		add(buildSearchForm(), BorderLayout.NORTH);
	}
	
	private void showTable(){
		tableModel = new StockControlTableModel();
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new CustomCellRenderer());
		table.setRowHeight(30);
		
		DefaultCellEditor singleclick = new DefaultCellEditor(new JTextField());
		singleclick.setClickCountToStart(2);
		table.setDefaultEditor(table.getColumnClass(4), singleclick); 
		
		
		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table),BorderLayout.CENTER);
		resizeColumnWidth(table);
	}
	 
	private JPanel buildSearchForm() {
		panel = new JPanel();
		panel.setLayout(new MigLayout("", "[]10[]20[]20[]10[]20[]10[]20[]", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		JLabel lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getDeafultDate();
		dpStartDate.setPreferredSize(new Dimension(115, 30));
		panel.add(lblFromDate, "align label"); //$NON-NLS-1$
		panel.add(dpStartDate);
		
		onlyTransfer = new JCheckBox("Only Transfer");
		onlyTransfer.setSelected(true);
		panel.add(onlyTransfer);
		
		lblWarehouse = new JLabel("Transfer to / Return from");
		cbWarehouse = new javax.swing.JComboBox();
		cbWarehouse.setPreferredSize(new Dimension(198, 30));
		InventoryWarehouseDAO warehouseDAO = new InventoryWarehouseDAO();
		List<InventoryWarehouse> warehouse = warehouseDAO.findAll();
		warehouse.add(0, new InventoryWarehouse(999, "ALL"));
		cbWarehouse.setModel(new ComboBoxModel(warehouse));
		try {
			
			
			cbWarehouse.setPreferredSize(new Dimension(120, 30));
			
			panel.add(lblWarehouse);
			panel.add(cbWarehouse);
			
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
			for (MenuGroup s : menuGroupList) {
				cbGroup.addItem(s);
			}
			cbGroup.setPreferredSize(new Dimension(120, 30));
			panel.add(lblGroup);
			panel.add(cbGroup);
			
			JButton searchBttn = new JButton(Messages.getString("MenuItemExplorer.3")); //$NON-NLS-1$
			searchBttn.setPreferredSize(new Dimension(60, 30));
			
			
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
	
			dpStartDate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					searchItem();
				}
			});
			cbWarehouse.addActionListener(new ActionListener() {
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
		Date selectDate = dpStartDate.getDate();
		InventoryWarehouse warehouse = (InventoryWarehouse) cbWarehouse.getSelectedItem();
		tableModel = new StockControlTableModel(selectDate, warehouse, categoryListBox.getSelectedItem().toString(), 
				cbGroup.getSelectedItem().toString(), onlyTransfer.isSelected());
		table.setModel(tableModel);
		table.tableChanged(new TableModelEvent(table.getModel())) ;
		
		ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
				"Report Query", "Stock Return Report");

	}
	
	private TransparentPanel createButtonPanel() {
	
		JButton saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(90, 30));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveStock();
					tableModel = new StockControlTableModel();
					//tableModel = new StockControlTableModel(txName, groupId);
					table.setModel(tableModel);
					table.tableChanged(new TableModelEvent(table.getModel())) ;
				} catch (NumberFormatException e1) {
					POSMessageDialog.showError(HCReturnDialog.this, Messages.getString("MenuItemExplorer.11")); //$NON-NLS-1$
					return;
				} catch (Exception e2) {
					BOMessageDialog.showError(HCReturnDialog.this, POSConstants.ERROR_MESSAGE, e2);
					return;
				}
			}
		});
		
		totalButton = new JButton("");
		totalButton.setPreferredSize(new Dimension(120, 30));
		
		TransparentPanel panel = new TransparentPanel();
		panel.setLayout(new MigLayout("", "300[]300[]100", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		panel.add(totalButton);
		panel.add(saveButton);
		
		return panel;
	}
	
	private void saveStock() {// TODO Auto-generated method stub
		
		try{
			InventoryWarehouse warehouse = (InventoryWarehouse) cbWarehouse.getSelectedItem();
			
			if(warehouse.getName().equals("ALL")) {
				POSMessageDialog.showError(Application.getPosWindow(), "Please select Warehouse!");
				return;
			}
			int warehouse_id = warehouse.getId();
			for(int i = 0;i<tableModel.getRowCount();i++){
				double retQty = 0;
				try{
					retQty = Double.parseDouble(tableModel.getValueAt(i,3).toString());
				}catch(Exception e) {}
				if(tableModel.getValueAt(i, 7).toString() == "true" && retQty>0){
					HashMap map = new HashMap();
					map.put("item_id", Integer.parseInt(tableModel.getValueAt(i, 0).toString()));
					map.put("tran_type", "Return");
					map.put("old_qty", Double.parseDouble(tableModel.getValueAt(i, 2).toString()));
					map.put("qty", retQty);
					if(warehouse_id != 0) map.put("warehouse_id", warehouse_id);
					
					Date d = new Date();
					Timestamp ts = new Timestamp(d.getTime());
					map.put("transaction_date", ts);
					
					IBatisFactory.insertSQL("inventory_transaction.insert_inventory_return_transaction", map);
					IBatisFactory.updateSQL("inventory_transaction.update_inventory_transaction", map);

				}
			}
			POSMessageDialog.showMessage("Save Successful");
			//searchItem();
		}catch (Exception e1) {
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e1);
		}
		
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
		columnWidth.add(70);
		columnWidth.add(70);
		columnWidth.add(70);
		columnWidth.add(70);
		columnWidth.add(80);
		columnWidth.add(50);
		return columnWidth;
	}
	
	class StockControlTableModel extends AbstractTableModel {
		String[] columnNames = { "ID","NAME","TAKE QTY","RETURN QTY","SALE QTY","UNIT PRICE","SUBTOTAL","" };
		Object[][] data = {};
		
		public StockControlTableModel(){
			//generateTableData(null, null);
		}
		
		public  StockControlTableModel (Date fromDate, InventoryWarehouse warehouse, String category, String group, boolean onlyTransfer){
			if(fromDate == null) return;
			generateTableData(fromDate, warehouse, category, group, onlyTransfer);
		}
	
		public void generateTableData(Date selectDate, InventoryWarehouse warehouse, String category, String group, boolean onlyTransfer){
			
			Date fromDate = DateUtil.startOfDay(selectDate, 0, 0 );
			Date toDate = DateUtil.endOfDay(selectDate, 23, 59);
			HashMap map = new HashMap();
			map.put("startDate", fromDate);
			map.put("endDate", toDate);
			
			if(!warehouse.getName().equals("ALL")) map.put("warehouseName", warehouse.getName());
			if(!category.equals("ALL")) map.put("categoryName", category);
			if(!group.equals("ALL")) map.put("groupName", group);
			
			String transferQuery = "inventory_transaction.getProduct_Stock_Return";
			List<MenuItemM> transferAll = IBatisFactory.selectList(transferQuery, map);
			
			if(!onlyTransfer) {
				String menuQuery = "inventory_transaction.getProduct_Return";
				List<MenuItemM> menuAll = IBatisFactory.selectList(menuQuery, map);
				boolean flag = false;
				for(MenuItemM m : menuAll) {
					flag = false;
					for(MenuItemM t : transferAll) {
						if(t.getProductNo()==m.getProductNo()) {
							flag = true;
							break;
						}
					}
					if(!flag) {
						transferAll.add(m);
					}
				}
			}
			
			
			
			
			
			if(transferAll==null || transferAll.size()==0) {
				data = null;
				fireTableDataChanged();
			}
				
			int rows = transferAll.size();
			data = new Object[rows][8];
			int i =0 ;
			for(MenuItemM menu : transferAll){
				
				data[i][0] = menu.getProductNo();
				data[i][1] = menu.getProductName();
				data[i][2] = menu.getChangeQty();
				if(menu.getChangeQty()>0) data[i][3] = 0;
				//take - return
				data[i][5] = menu.getPrice();
				data[i][6] = 0;
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
			if( columnIndex == 3 || columnIndex == 7)
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
                    return Double.class;
                case 3:
                    return Double.class;
                case 4:
                    return Double.class;
                case 5:
                    return Double.class;
                case 6:
                    return Double.class;    
                default:
                    return Boolean.class;
            }
        }
	
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (data == null)
				return ""; 
			return data[rowIndex][columnIndex];
		}
		
		public void updateTotal() {
			double total = 0.0;
			for(int i=0; i<data.length; i++) {
				try {
					total += (double)data[i][6];
				}catch(Exception e) {}
			}
			totalButton.setText(total +"");	
		}
		
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Double val = 0.0;
			
			if(columnIndex == 7){
				data[rowIndex][columnIndex] = !(Boolean)data[rowIndex][columnIndex];
				if(!(Boolean)data[rowIndex][columnIndex]) {
					data[rowIndex][4] = data[rowIndex][3] = data[rowIndex][6] = null;
				}
				updateTotal();
			}else if(columnIndex == 3 ) {
				try {
					if(value==null || String.valueOf(value).equals("")) return;
					val = Double.parseDouble((String)value);
				}catch(Exception e) {
					POSMessageDialog.showError(Application.getPosWindow(), "Please input number value!");
					return;
				}
				
				data[rowIndex][columnIndex] = val;
				if(data[rowIndex][2]!=null) data[rowIndex][4] = ((-1)*(Double) data[rowIndex][2]) - val;
				data[rowIndex][6] =  (Double) data[rowIndex][4] * (Double) data[rowIndex][5];
				data[rowIndex][7] = true;
				updateTotal();
			}
			else{
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
