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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;

import org.jdesktop.swingx.JXDatePicker;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.NumberUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

//not used now
/**
 * Created by IntelliJ IDEA.
 * User: mshahriar
 * Date: Feb 28, 2007
 * Time: 12:25:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class HourlyLaborReportView extends JPanel {
	
	private JPanel reportPanel;
	private JPanel contentPane;
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a"); //$NON-NLS-1$
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	
	private JSpinner dpStartTime;
	private JSpinner dpEndTime;
	
	private JLabel lblFromDate;
	private JLabel lblToDate;
	
	private JButton btnGo = new JButton(com.floreantpos.POSConstants.GO);
	
	private JComboBox cbTerminal;
	private JComboBox cbUserType;
	private JPanel reportContainer;

	
	public HourlyLaborReportView() {
		super(new BorderLayout());
		
		JPanel topPanel = new JPanel(new MigLayout("","[]10[]10[]20[]10[]10[]20[]10[]20[]10[]20[]", "[]"));
		
		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();

		lblToDate = new JLabel(com.floreantpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();
		
		dpStartTime = UiUtil.getTimeSpinner("start");
		dpEndTime = UiUtil.getTimeSpinner("end");
		
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
		
		topPanel.add(new JLabel(com.floreantpos.POSConstants.USER_TYPE + ":"));
		
		cbUserType = new JComboBox();
		cbUserType.setPreferredSize(new Dimension(80, 30));
		UserTypeDAO dao = new UserTypeDAO();
		List<UserType> userTypes = dao.findAll();

		Vector list = new Vector();
		list.add(null);
		list.addAll(userTypes);

		cbUserType.setModel(new DefaultComboBoxModel(list));
		topPanel.add(cbUserType);
		
		cbTerminal = new JComboBox();
		cbTerminal.setPreferredSize(new Dimension(80, 30));
		TerminalDAO terminalDAO = new TerminalDAO();
		List terminals = terminalDAO.findAll();
		terminals.add(0, com.floreantpos.POSConstants.ALL);
		cbTerminal.setModel(new ListComboBoxModel(terminals));
		topPanel.add(cbTerminal);
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
				viewReport();
			}
		});
	}

	private void viewReport() {
		

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
		

		if (fromDate.after(toDate)) {
			POSMessageDialog.showError(com.floreantpos.util.POSUtil.getFocusedWindow(), com.floreantpos.POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
			return;
		}

		UserType userType = (UserType) cbUserType.getSelectedItem();

		Terminal terminal = null;
		if (cbTerminal.getSelectedItem() instanceof Terminal) {
			terminal = (Terminal) cbTerminal.getSelectedItem();
		}


		TicketDAO ticketDAO = TicketDAO.getInstance();
		AttendenceHistoryDAO attendenceHistoryDAO = new AttendenceHistoryDAO();
		ArrayList<LaborReportData> rows = new ArrayList<LaborReportData>();

		DecimalFormat formatter = new DecimalFormat("00"); //$NON-NLS-1$

		int grandTotalChecks = 0;
		int grandTotalGuests = 0;
		double grandTotalSales = 0;
		double grandTotalMHr = 0;
		double grandTotalLabor = 0;
		double grandTotalSalesPerMHr = 0;
		double grandTotalGuestsPerMHr = 0;
		double grandTotalCheckPerMHr = 0;
		double grandTotalLaborCost = 0;

		for (int i = 0; i < 24; i++) {
			if(i < fromDate.getHours() || i > toDate.getHours()) continue;
			
			List<Ticket> tickets = ticketDAO.findTicketsForLaborHour(fromDate, toDate, i, userType, terminal);
			List<User> users = attendenceHistoryDAO.findNumberOfClockedInUserAtHour(fromDate, toDate, i, userType, terminal);

			int manHour = users.size();
			int totalChecks = 0;
			int totalGuests = 0;
			double totalSales = 0;
			double labor = 0;
			double salesPerMHr = 0;
			double guestsPerMHr = 0;
			double checksPerMHr = 0;
			//double laborCost = 0;

			for (Ticket ticket : tickets) {
				++totalChecks;
				totalGuests += ticket.getNumberOfGuests();
				totalSales += ticket.getTotalAmount();
			}

			for (User user : users) {
				labor += (user.getCostPerHour() == null ? 0 : user.getCostPerHour());
			}
			if (manHour > 0) {
				labor = labor / manHour;
				salesPerMHr = totalSales / manHour;
				guestsPerMHr = (double) totalGuests / manHour;
				checksPerMHr = totalChecks / manHour;
				//laborCost =
			}

			LaborReportData reportData = new LaborReportData();
			reportData.setPeriod(formatter.format(i) + ":00 - " + formatter.format(i) + ":59"); //$NON-NLS-1$ //$NON-NLS-2$
			reportData.setManHour(manHour);
			reportData.setNoOfChecks(totalChecks);
			reportData.setSales(totalSales);
			reportData.setNoOfGuests(totalGuests);
			reportData.setLabor(labor);
			reportData.setSalesPerMHr(salesPerMHr);
			reportData.setGuestsPerMHr(guestsPerMHr);
			reportData.setCheckPerMHr(checksPerMHr);

			rows.add(reportData);

			grandTotalChecks += totalChecks;
			grandTotalGuests += totalGuests;
			grandTotalSales += totalSales;
			grandTotalMHr += manHour;
			grandTotalLabor += labor;
			grandTotalSalesPerMHr += salesPerMHr;
			grandTotalCheckPerMHr += checksPerMHr;
			grandTotalGuestsPerMHr += guestsPerMHr;
			//grandTotalLaborCost +=

		}

		ArrayList<LaborReportData> shiftReportRows = new ArrayList<LaborReportData>();
		ShiftDAO shiftDAO = new ShiftDAO();
		List<Shift> shifts = shiftDAO.findAll();
		for (Shift shift : shifts) {
			List<Ticket> tickets = ticketDAO.findTicketsForShift(fromDate, toDate, shift, userType, terminal);
			List<User> users = attendenceHistoryDAO.findNumberOfClockedInUserAtShift(fromDate, toDate, shift, userType, terminal);

			int manHour = users.size();
			int totalChecks = 0;
			int totalGuests = 0;
			double totalSales = 0;
			double labor = 0;
			double salesPerMHr = 0;
			double guestsPerMHr = 0;
			double checksPerMHr = 0;
			//double laborCost = 0;

			for (Ticket ticket : tickets) {
				++totalChecks;
				totalGuests += ticket.getNumberOfGuests();
				totalSales += ticket.getTotalAmount();
			}

			for (User user : users) {
				labor += (user.getCostPerHour() == null ? 0 : user.getCostPerHour());
			}
			if (manHour > 0) {
				labor = labor / manHour;
				salesPerMHr = totalSales / manHour;
				guestsPerMHr = (double) totalGuests / manHour;
				checksPerMHr = totalChecks / manHour;
				//laborCost =
			}

			LaborReportData reportData = new LaborReportData();
			reportData.setPeriod(shift.getName());
			reportData.setManHour(manHour);
			reportData.setNoOfChecks(totalChecks);
			reportData.setSales(totalSales);
			reportData.setNoOfGuests(totalGuests);
			reportData.setLabor(labor);
			reportData.setSalesPerMHr(salesPerMHr);
			reportData.setGuestsPerMHr(guestsPerMHr);
			reportData.setCheckPerMHr(checksPerMHr);

			shiftReportRows.add(reportData);
		}

		try {
			JasperReport hourlyReport = ReportUtil.getReport("hourly_labor_subreport"); //$NON-NLS-1$
			JasperReport shiftReport = ReportUtil.getReport("hourly_labor_shift_subreport"); //$NON-NLS-1$

			JasperReport report = ReportUtil.getReport("hourly_labor_report"); //$NON-NLS-1$

			HashMap properties = new HashMap();
			ReportUtil.populateRestaurantProperties(properties);
			properties.put("reportTitle", com.floreantpos.POSConstants.HOURLY_LABOR_REPORT); //$NON-NLS-1$
			properties.put("reportTime", ReportService.formatFullDate(new Date())); //$NON-NLS-1$
			properties.put("fromDay", ReportService.formatShortDate(fromDate)); //$NON-NLS-1$
			properties.put("toDay", ReportService.formatShortDate(toDate)); //$NON-NLS-1$
			properties.put(com.floreantpos.POSConstants.TYPE, com.floreantpos.POSConstants.BY_RANGE_ACTUAL);
			properties.put("dept", userType == null ? com.floreantpos.POSConstants.ALL : userType.getName()); //$NON-NLS-1$
			properties.put("incr", Messages.getString("HourlyLaborReportView.0")); //$NON-NLS-1$ //$NON-NLS-2$
			properties.put("cntr", terminal == null ? com.floreantpos.POSConstants.ALL : terminal.getName()); //$NON-NLS-1$

			properties.put("totalChecks", String.valueOf(grandTotalChecks)); //$NON-NLS-1$
			properties.put("totalGuests", String.valueOf(grandTotalGuests)); //$NON-NLS-1$
			properties.put("totalSales", NumberUtil.formatPrice(grandTotalSales)); //$NON-NLS-1$
			properties.put("totalMHr", NumberUtil.formatPrice(grandTotalMHr)); //$NON-NLS-1$
			properties.put("totalLabor", NumberUtil.formatPrice(grandTotalLabor)); //$NON-NLS-1$
			properties.put("totalSalesPerMhr", NumberUtil.formatPrice(grandTotalSalesPerMHr)); //$NON-NLS-1$
			properties.put("totalGuestsPerMhr", NumberUtil.formatPrice(grandTotalCheckPerMHr)); //$NON-NLS-1$
			properties.put("totalCheckPerMHr", NumberUtil.formatPrice(grandTotalGuestsPerMHr)); //$NON-NLS-1$
			properties.put("totalLaborCost", NumberUtil.formatPrice(grandTotalLaborCost)); //$NON-NLS-1$

			properties.put("hourlyReport", hourlyReport); //$NON-NLS-1$
			properties.put("hourlyReportDatasource", new JRTableModelDataSource(new HourlyLaborReportModel(rows))); //$NON-NLS-1$
			properties.put("shiftReport", shiftReport); //$NON-NLS-1$
			properties.put("shiftReportDatasource", new JRTableModelDataSource(new HourlyLaborReportModel(shiftReportRows))); //$NON-NLS-1$

			JasperPrint print = JasperFillManager.fillReport(report, properties, new JREmptyDataSource());

			JRViewer viewer = new JRViewer(print);
			reportContainer.removeAll();
			reportContainer.add(viewer);
			reportContainer.revalidate();
		} catch (JRException e) {
			PosLog.error(getClass(), e);
		}
	}

	public static class LaborReportData {
		private String period;
		private int noOfChecks;
		private int noOfGuests;
		private double sales;
		private double manHour;
		private double labor;
		private double salesPerMHr;
		private double guestsPerMHr;
		private double checkPerMHr;
		private double laborCost;

		public double getCheckPerMHr() {
			return checkPerMHr;
		}

		public void setCheckPerMHr(double checkPerMHr) {
			this.checkPerMHr = checkPerMHr;
		}

		public double getGuestsPerMHr() {
			return guestsPerMHr;
		}

		public void setGuestsPerMHr(double guestsPerMHr) {
			this.guestsPerMHr = guestsPerMHr;
		}

		public double getLabor() {
			return labor;
		}

		public void setLabor(double labor) {
			this.labor = labor;
		}

		public double getLaborCost() {
			return laborCost;
		}

		public void setLaborCost(double laborCost) {
			this.laborCost = laborCost;
		}

		public double getManHour() {
			return manHour;
		}

		public void setManHour(double manHour) {
			this.manHour = manHour;
		}

		public int getNoOfChecks() {
			return noOfChecks;
		}

		public void setNoOfChecks(int noOfChecks) {
			this.noOfChecks = noOfChecks;
		}

		public int getNoOfGuests() {
			return noOfGuests;
		}

		public void setNoOfGuests(int noOfGuests) {
			this.noOfGuests = noOfGuests;
		}

		public String getPeriod() {
			return period;
		}

		public void setPeriod(String period) {
			this.period = period;
		}

		public double getSales() {
			return sales;
		}

		public void setSales(double sales) {
			this.sales = sales;
		}

		public double getSalesPerMHr() {
			return salesPerMHr;
		}

		public void setSalesPerMHr(double salesPerMHr) {
			this.salesPerMHr = salesPerMHr;
		}

	}
}
