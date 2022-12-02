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

import org.jdesktop.swingx.JXDatePicker;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.mybatis.DailySalesReportM;
import com.floreantpos.model.mybatis.HourlySalesReportM;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;

public class HourlySalesReportView extends JPanel {
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a"); //$NON-NLS-1$
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	private JSpinner dpStartTime;
	private JSpinner dpEndTime;
	private JLabel lblFromDate;
	private JLabel lblToDate;
	
	private JButton btnGo = new JButton(com.floreantpos.POSConstants.GO);
	private JPanel reportContainer;
	private JComboBox cbPrintType;
	private int startHr=9, endHr=21;
	
	public HourlySalesReportView() {
		super(new BorderLayout());
		
		JPanel topPanel = new JPanel(new MigLayout("", "[]10[]10[]20[]10[]10[]20[]10[]20[]10[]20[]", "[]"));
		
		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();
		dpStartDate.setPreferredSize(new Dimension(115, 30));

		lblToDate = new JLabel(com.floreantpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();
		dpEndDate.setPreferredSize(new Dimension(115, 30));

		dpStartTime = UiUtil.getTimeSpinner(startHr, 0);
		dpEndTime = UiUtil.getTimeSpinner(endHr, 59);
		
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
		
		topPanel.add(new JLabel(com.floreantpos.POSConstants.REPORT_TEMPLATE + ":"));
		cbPrintType = new JComboBox();
		cbPrintType.setPreferredSize(new Dimension(60, 30));
		cbPrintType.setModel(new ListComboBoxModel(Arrays.asList("A4", "80mm")));
		topPanel.add(cbPrintType);
		topPanel.add(btnGo, "wrap"); //$NON-NLS-1$
		add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new EmptyBorder(0, 10,10,10));
		centerPanel.add(new JSeparator(), BorderLayout.NORTH);
		
		reportContainer = new JPanel(new BorderLayout());
		centerPanel.add(reportContainer);
		
		add(centerPanel);
		btnGo.setPreferredSize(new Dimension(60, 30));

		btnGo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					viewReport();
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"Report Query", "View Hourly Sales Report");
				} catch (Exception e1) {
					POSMessageDialog.showError(HourlySalesReportView.this, POSConstants.ERROR_MESSAGE, e1);
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
        
        Date date2 = (Date) evalue;
        int eh = Integer.parseInt(hourF.format(date2));
        int em = Integer.parseInt(minuteF.format(date2));
        
        System.out.println(sh+"-"+sm+";"+eh+"-"+em);
        Date fromDate = dpStartDate.getDate();
		Date toDate = dpEndDate.getDate();
		fromDate = DateUtil.startOfDay(fromDate, sh, sm );
		toDate = DateUtil.endOfDay(toDate, eh, em);
		
		System.out.println(fromDate.toString()+";"+toDate.toString());
		HashMap map = new HashMap();
		ReportUtil.populateRestaurantProperties(map);

		map.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$
		map.put("startDate", fromDate); //$NON-NLS-1$
		map.put("endDate", toDate); //$NON-NLS-1$
		
		map.put("periodString", timeFormatter.format(fromDate)+"~"+timeFormatter.format(toDate));
		
		List<HourlySalesReportM> dataSource = IBatisFactory.selectList("Report.getHourly_Sales", map);
		List<HourlySalesReportM> dataSourceBp = new ArrayList<HourlySalesReportM> ();
		HourlySalesReportM model ;
		int dataHr, i=0;
		double totalNetSales=0;
		boolean newFlag = true, type80 = false;
		String hr;
		if(cbPrintType.getSelectedItem().toString().equals("80mm"))
			type80 = true;
		
		if(dataSource!=null) {
			for(int si = 0; si <= 23; si++) {
				newFlag = true;
				
				if(i<dataSource.size()) {
					dataHr = Integer.parseInt(dataSource.get(i).getFactdate());

					if(si == dataHr) {
						newFlag = false;
						totalNetSales += dataSource.get(i).getSale();
						if(type80)
							hr = dataHr+" ~ "+(dataHr+1);
						else
							hr = dataHr+":00 ~ "+dataHr+":59";
						dataSource.get(i).setFactdate(hr);
						dataSourceBp.add(dataSource.get(i));
						i++;
					}					
				}
				if(newFlag && (si>=sh && si<=eh)) {
					model = new HourlySalesReportM();
					if(type80)
						hr = si+" ~ "+(si+1);
					else hr = si+":00 ~ "+si+":59";
					model.setFactdate(hr);
					dataSourceBp.add(model);
				}
			}
		}

		map.put("totalNetSales", totalNetSales);
		
		String reportTemplate = "hourly_sales_report";
		if(cbPrintType.getSelectedItem().toString().equals("80mm"))
			reportTemplate = "hourly_sales_report_80mm";
		JasperReport masterReport = ReportUtil.getReport(reportTemplate);
		JasperPrint print = JasperFillManager.fillReport(masterReport, map, new JRBeanCollectionDataSource(dataSourceBp));
		JRViewer viewer = new JRViewer(print);
		viewer = new JRViewer(print);
		
		reportContainer.removeAll();
		reportContainer.add(viewer);
		reportContainer.revalidate();
		
	}
}
