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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXDatePicker;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;

import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.mybatis.DailySalesReportM;
import com.floreantpos.model.mybatis.ProductSalesM;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;

public class ProductSalesReportView extends JPanel {
	Log logger = LogFactory.getLog(ProductSalesReportView.class);
	
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a"); //$NON-NLS-1$
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	
	private JSpinner dpStartTime;//
	private JSpinner dpEndTime;//
	
	private JLabel lblFromDate;
	private JLabel lblToDate;
	
	private JButton btnGo = new JButton(com.floreantpos.POSConstants.GO);
	private JPanel reportContainer;
	private JComboBox cbPrintType, categoryListBox, cbSortBy;
	
	private POSTextField tfDisplay = new POSTextField();
	
	public ProductSalesReportView() {
		super(new BorderLayout());
		
		JPanel topPanel = new JPanel(new MigLayout());
		
		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();
		

		lblToDate = new JLabel(com.floreantpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();
		
		dpStartTime = UiUtil.getTimeSpinner("start");//
		dpEndTime = UiUtil.getTimeSpinner("end");//
		
		
		dpStartDate.setPreferredSize(new Dimension(115, 30));
		dpEndDate.setPreferredSize(new Dimension(115, 30));
		dpStartTime.setPreferredSize(new Dimension(50, 30));
		dpEndTime.setPreferredSize(new Dimension(50, 30));
		btnGo.setPreferredSize(new Dimension(60, 30));
		
		topPanel.add(lblFromDate);
		topPanel.add(dpStartDate);
		topPanel.add(dpStartTime);//
		topPanel.add(lblToDate);
		topPanel.add(dpEndDate);
		topPanel.add(dpEndTime);//
		
		topPanel.add(new JLabel("Category :"));
		List<String> cateList = IBatisFactory.selectList("Report.getCategory_List", null);
		cateList.add(0, "All");
		
		categoryListBox = new JComboBox();
		categoryListBox.setPreferredSize(new Dimension(115, 30));
		categoryListBox.setModel(new ListComboBoxModel(cateList));
		topPanel.add(categoryListBox, "wrap");
		
		
		topPanel.add(new JLabel("Sort By :"));
		cbSortBy = new JComboBox();
		cbSortBy.setPreferredSize(new Dimension(115, 30));
		cbSortBy.setModel(new ListComboBoxModel(Arrays.asList("Category", "Sales", "Qty")));
		topPanel.add(cbSortBy);
		
		topPanel.add(new JLabel("Report Template :"));
		cbPrintType = new JComboBox();
		cbPrintType.setPreferredSize(new Dimension(80, 30));
		cbPrintType.setModel(new ListComboBoxModel(Arrays.asList("A4", "80mm")));
		topPanel.add(cbPrintType);
		
		topPanel.add(new JLabel("Display Rows :"));
		tfDisplay.setPreferredSize(new Dimension(120, 30));
		tfDisplay.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
		    	char c = e.getKeyChar();
		    	if (!((c >= '0') && (c <= '9') ||
		        	(c == KeyEvent.VK_BACK_SPACE) ||
		        	(c == KeyEvent.VK_DELETE))) {
		    			getToolkit().beep();
		    			e.consume();
		    		}
		    	}
		  	});
		topPanel.add(tfDisplay);
		
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
							"Report Query", "Product Sales Report");
				} catch (Exception e1) {
					POSMessageDialog.showError(ProductSalesReportView.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}
			
		});
	}
	
	private void viewReport() throws Exception {
		
		Object svalue = dpStartTime.getValue();
		Object evalue = dpEndTime.getValue();
        Date date = (Date) svalue;
        
        SimpleDateFormat hourF = new SimpleDateFormat("HH");
        SimpleDateFormat minuteF = new SimpleDateFormat("mm");
        
        int sh = Integer.parseInt(hourF.format(date));
        int sm = Integer.parseInt(minuteF.format(date));
        
        date = (Date) evalue;
        int eh = Integer.parseInt(hourF.format(date));
        int em = Integer.parseInt(minuteF.format(date));
        
        Date fromDate = dpStartDate.getDate();
		Date toDate = dpEndDate.getDate();
		fromDate = DateUtil.startOfDay(fromDate, sh, sm );
		toDate = DateUtil.endOfDay(toDate, eh, em);
		
		
		HashMap map = new HashMap();
		ReportUtil.populateRestaurantProperties(map);
		map.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$
		
		if(TerminalConfig.isOnlyPay())
			map.put("NeedPaid", TerminalConfig.isOnlyPay());

		map.put("startDate", fromDate); //$NON-NLS-1$
		map.put("endDate", toDate); //$NON-NLS-1$
		
		map.put("periodString", timeFormatter.format(fromDate)+"~"+timeFormatter.format(toDate));
		if(tfDisplay.getText()!=null && !tfDisplay.getText().equals(""))
			map.put("display", Integer.parseInt(tfDisplay.getText()));
		/*
		DailySalesReportM sales = (DailySalesReportM) IBatisFactory.selectOneSQL("Report.getSales", map);
		if(sales != null) {
			map.put("serviceCharge", sales.getService_charge_subtotal());
			map.put("totalTax", sales.getTax_subtotal());
			map.put("totalDiscount", sales.getDiscount_subtotal());
			map.put("totalSales", sales.getSale());
		}
		*/
		
		if(categoryListBox.getSelectedItem().toString().equals("All"))
			map.remove("categoryName");
		else map.put("categoryName", categoryListBox.getSelectedItem().toString());
		
		if(cbSortBy.getSelectedItem().toString().equals("Sales")) {
			map.put("orderBySales", "Sales");
		}else if(cbSortBy.getSelectedItem().toString().equals("Qty")) {
			map.put("orderByQty", "Qty");
		}else {
			map.put("orderByCategory", "Category");
		}

		List<ProductSalesM> dataSource = IBatisFactory.selectList("Report.getProduct_Sales", map);
		String reportTemplate = "product_sales_report";
		if(cbPrintType.getSelectedItem().toString().equals("80mm"))
			reportTemplate = "product_sales_report_80mm";
		
		JasperReport masterReport = ReportUtil.getReport(reportTemplate);
		JasperPrint print = JasperFillManager.fillReport(masterReport, map, new JRBeanCollectionDataSource(dataSource));
		JRViewer viewer = new JRViewer(print);
		viewer = new JRViewer(print);
		
		reportContainer.removeAll();
		reportContainer.add(viewer);
		reportContainer.revalidate();
		
	}
}
