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

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;

import org.jdesktop.swingx.JXDatePicker;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.mybatis.PayoutM;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;

public class PayoutReportView extends JPanel {
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a"); //$NON-NLS-1$
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	
	private JLabel lblFromDate;
	private JLabel lblToDate;
	
	private JButton btnGo = new JButton(com.floreantpos.POSConstants.GO);
	private JPanel reportContainer;
	
	private JCheckBox checkS = new JCheckBox("Reason");
	private JCheckBox checkC = new JCheckBox("Recepient");
	private JCheckBox checkD = new JCheckBox("Date");
	private JComboBox cbPrintType;
	
	public PayoutReportView() {
		super(new BorderLayout());
		JPanel topPanel = new JPanel(new MigLayout());
		
		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();

		lblToDate = new JLabel(com.floreantpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();
		
		dpStartDate.setPreferredSize(new Dimension(115, 30));
		dpEndDate.setPreferredSize(new Dimension(115, 30));
		btnGo.setPreferredSize(new Dimension(60, 30));
		checkC.setPreferredSize(new Dimension(80, 30));
		checkD.setPreferredSize(new Dimension(80, 30));
		checkS.setPreferredSize(new Dimension(80, 30));
		
		topPanel.add(lblFromDate);
		topPanel.add(dpStartDate);
		topPanel.add(lblToDate);
		topPanel.add(dpEndDate);
		topPanel.add(new JLabel("Summary By : "));
		
		topPanel.add(checkD);
		topPanel.add(checkS);
		topPanel.add(checkC);
		
		topPanel.add(new JLabel(com.floreantpos.POSConstants.REPORT_TEMPLATE + ":"));
		cbPrintType = new JComboBox();
		cbPrintType.setPreferredSize(new Dimension(115, 30));
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
		
		btnGo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					viewReport();
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"Report Query", "Payout Report");
				} catch (Exception e1) {
					POSMessageDialog.showError(PayoutReportView.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}
			
		});
	}
	
	private void viewReport() throws Exception {
		
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
		
		int count=0;
		if(checkD.isSelected()) count+=1;
		if(checkS.isSelected()) count+=2;
		if(checkC.isSelected()) count+=4;
		
		String report = "Report.get_payout";
		if(count==1) report = "Report.get_payout_summary_d";
		if(count==2) report = "Report.get_payout_summary_s";
		if(count==4) report = "Report.get_payout_summary_c";
		if(count==3) report = "Report.get_payout_summary_ds";
		if(count==5) report = "Report.get_payout_summary_dc";
		if(count==6) report = "Report.get_payout_summary_sc";
		if(count==7) report = "Report.get_payout_summary_dsc";
		
		List<PayoutM> dataSource = IBatisFactory.selectList(report, map);
		
		String reportTemplate = "payout_report";
		if(cbPrintType.getSelectedItem().toString().equals("80mm"))
			reportTemplate = "payout_report_80mm";
		
		JasperReport masterReport = ReportUtil.getReport(reportTemplate);
		JasperPrint print = JasperFillManager.fillReport(masterReport, map, new JRBeanCollectionDataSource(dataSource));
		JRViewer viewer = new JRViewer(print);
		viewer = new JRViewer(print);
		
		reportContainer.removeAll();
		reportContainer.add(viewer);
		reportContainer.revalidate();
	}
}
