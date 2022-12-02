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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXDatePicker;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.mybatis.HCReturnListDetailM;
import com.floreantpos.model.mybatis.HCReturnListM;
import com.floreantpos.model.mybatis.MenuItemM;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;

public class HCReturnListDialog extends JPanel {
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a"); //$NON-NLS-1$
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	
	private JLabel lblFromDate;
	private JLabel lblToDate;
	
	private JButton btnGo = new JButton(com.floreantpos.POSConstants.GO);
	private JPanel reportContainer;
	private JComboBox categoryListBox, cbWarehouse, cbGroup;
	 
	public HCReturnListDialog() {
		super(new BorderLayout());
		
		JPanel topPanel = new JPanel(new MigLayout());
		
		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();

		lblToDate = new JLabel(com.floreantpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();
		
		dpStartDate.setPreferredSize(new Dimension(115, 30));
		dpEndDate.setPreferredSize(new Dimension(115, 30));
		btnGo.setPreferredSize(new Dimension(60, 30));
		
		topPanel.add(lblFromDate);
		topPanel.add(dpStartDate);
		topPanel.add(lblToDate);
		topPanel.add(dpEndDate);
		
		topPanel.add(new JLabel("Category :"));
		List<String> cateList = IBatisFactory.selectList("Report.getCategory_List", null);
		cateList.add(0, "ALL");
		
		categoryListBox = new JComboBox();
		categoryListBox.setPreferredSize(new Dimension(115, 30));
		categoryListBox.setModel(new ListComboBoxModel(cateList));
		topPanel.add(categoryListBox);
		
		JLabel lblGroup = new JLabel(Messages.getString("MenuItemExplorer.1")); //$NON-NLS-1$
		List<MenuGroup> menuGroupList = MenuGroupDAO.getInstance().findAll();
		cbGroup = new JComboBox();
		cbGroup.setPreferredSize(new Dimension(120, 30));
		cbGroup.addItem("ALL"); //$NON-NLS-1$
		for (MenuGroup s : menuGroupList) {
			cbGroup.addItem(s);
		}
		topPanel.add(lblGroup);
		topPanel.add(cbGroup);
		
		cbWarehouse = new javax.swing.JComboBox();
		cbWarehouse.setPreferredSize(new Dimension(198, 30));
		InventoryWarehouseDAO warehouseDAO = new InventoryWarehouseDAO();
		List<InventoryWarehouse> warehouse = warehouseDAO.findAll();
		warehouse.add(0, new InventoryWarehouse(999, "ALL"));
		cbWarehouse.setModel(new ComboBoxModel(warehouse));
		cbWarehouse.setPreferredSize(new Dimension(120, 30));

		topPanel.add(new JLabel("Transfer to / Return from"));
		topPanel.add(cbWarehouse);
		
		
		
		topPanel.add(btnGo, "wrap"); //$NON-NLS-1$
		add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new EmptyBorder(0, 10,10,10));
		centerPanel.add(new JSeparator(), BorderLayout.NORTH);
		
		reportContainer = new JPanel(new BorderLayout());
		centerPanel.add(reportContainer);
		
		add(centerPanel);
		
		btnGo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					viewReport();
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"Report Query", "Daily Return List Report");
				} catch (Exception e1) {
					POSMessageDialog.showError(HCReturnListDialog.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}
			
		});
	}
	
	private void viewReport() throws Exception {
		List<HCReturnListM> dataSource;
		String criteria = "";

		String reportTemplate = "hc_return_list_report";
		
		Date fromDate = dpStartDate.getDate();
		Date toDate = dpEndDate.getDate();
		fromDate = DateUtil.startOfDay(fromDate);
		toDate = DateUtil.endOfDay(toDate);
		
		HashMap map = new HashMap();
		ReportUtil.populateRestaurantProperties(map);
		map.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$

		map.put("startDate", fromDate); //$NON-NLS-1$
		map.put("endDate", toDate); //$NON-NLS-1$
		
		map.put("periodString", timeFormatter.format(fromDate)+"~"+timeFormatter.format(toDate));
		
		if(categoryListBox.getSelectedItem().toString().equals("ALL"))
			map.remove("categoryName");
		else {
			map.put("categoryName", categoryListBox.getSelectedItem().toString());
			criteria += "Category:"+categoryListBox.getSelectedItem().toString()+"; ";
		}
		
		if(cbGroup.getSelectedItem().toString().equals("ALL"))
			map.remove("groupName");
		else {
			map.put("groupName", cbGroup.getSelectedItem().toString());
			criteria += "Group:"+cbGroup.getSelectedItem().toString()+"; ";
		}
		
		InventoryWarehouse warehouse = (InventoryWarehouse) cbWarehouse.getSelectedItem();
		if(!warehouse.getName().equals("ALL")) {
			map.put("warehouseId", warehouse.getId());
			criteria += "Warehouse:"+warehouse.getName()+"; ";
		}
		
		dataSource = IBatisFactory.selectList("inventory_transaction.getHC_return_warehouse", map);
		if(dataSource!=null) {
			for(int i=0; i<dataSource.size(); i++) {
				map.put("warehouseId", dataSource.get(i).getWarehouseId());
				List<HCReturnListDetailM> lp = IBatisFactory.selectList("inventory_transaction.getHC_return_list", map);
				if(lp!=null)
					dataSource.get(i).setItemList(new JRBeanCollectionDataSource(lp));
			}
		}
		map.put("branchName", criteria);
		map.put("itemListReport", ReportUtil.getReport("hc_return_list_report_subreport"));
		JasperReport masterReport = ReportUtil.getReport(reportTemplate);
		JasperPrint print = JasperFillManager.fillReport(masterReport, map, new JRBeanCollectionDataSource(dataSource));
		JRViewer viewer = new JRViewer(print);
		viewer = new JRViewer(print);
		
		reportContainer.removeAll();
		reportContainer.add(viewer);
		reportContainer.revalidate();
		
	}
}
