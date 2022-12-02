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
package com.floreantpos.report;

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
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.mybatis.HCDailyStockM;
import com.floreantpos.model.mybatis.MenuItemM;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;

public class HCDailyStockReportView extends JPanel {
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a"); //$NON-NLS-1$
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	
	private JXDatePicker dpStartDate;
	
	private JLabel lblFromDate;
	
	private JButton btnGo = new JButton(com.floreantpos.POSConstants.GO);
	private JPanel reportContainer;
	private JCheckBox disableWarehouse;
	 
	public HCDailyStockReportView() {
		super(new BorderLayout());
		
		JPanel topPanel = new JPanel(new MigLayout());
		
		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getDeafultDate();

		dpStartDate.setPreferredSize(new Dimension(115, 30));
		btnGo.setPreferredSize(new Dimension(60, 30));
		
		topPanel.add(lblFromDate);
		topPanel.add(dpStartDate);

		disableWarehouse = new JCheckBox("Disable SGH/Super1");
		btnGo.setPreferredSize(new Dimension(60, 30));
		topPanel.add(disableWarehouse);
		
		
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
							"Report Query", "Daily Stock Report");
				} catch (Exception e1) {
					POSMessageDialog.showError(HCDailyStockReportView.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}
			
		});
	}
	
	private void viewReport() throws Exception {
		List<HCDailyStockM> dataSource ;
		String criteria = "";

		String reportTemplate = "hc_daily_stock_report";
		
		Date fromDate = dpStartDate.getDate();
		Date toDate = dpStartDate.getDate();
		fromDate = DateUtil.startOfDay(fromDate, 0, 0);
		toDate = DateUtil.endOfDay(toDate, 23, 59);
		
		HashMap map = new HashMap();
		ReportUtil.populateRestaurantProperties(map);
		map.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$

		map.put("startDate", fromDate); //$NON-NLS-1$
		map.put("endDate", toDate); 
		map.put("periodString", timeFormatter.format(fromDate));
		
		map.put("branchName", criteria);
		if(disableWarehouse.isSelected()) {
			dataSource = IBatisFactory.selectList("inventory_transaction.get_HC_daily_stock", map);
			
		}else {
			dataSource = IBatisFactory.selectList("inventory_transaction.get_HC_daily_stock_warehouse", map);
			reportTemplate = "hc_daily_stock_report_warehouse";
		}
		if(dataSource!=null) {
			double lastQty = -1;
			String lastName = "-1";
			for(HCDailyStockM ds : dataSource) {
				if(ds.getAltName().equals(lastName)) {
					ds.setRemark("["+(lastQty + ds.getBalanceQty())+"]");
					lastQty = -1;
					lastName="-1";
				}else {
					lastQty = ds.getBalanceQty();
					lastName = ds.getAltName();
				}
			}
		}

		/*
		if(cbPrintType.getSelectedItem().toString().equals("80mm"))
			reportTemplate = "product_stock_adjust_report_80mm";
		*/
		JasperReport masterReport = ReportUtil.getReport(reportTemplate);
		JasperPrint print = JasperFillManager.fillReport(masterReport, map, new JRBeanCollectionDataSource(dataSource));
		JRViewer viewer = new JRViewer(print);
		viewer = new JRViewer(print);
		
		reportContainer.removeAll();
		reportContainer.add(viewer);
		reportContainer.revalidate();
		
	}
}
