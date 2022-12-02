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
package com.floreantpos.bo.actions.Report;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.report.MemberSalesDetailReportView;
import com.floreantpos.report.MemberSalesReportView;
import com.floreantpos.report.OrderStatusReportView;

public class MemberSalesDetailReportAction extends AbstractAction {

	public MemberSalesDetailReportAction() {
		super(com.floreantpos.POSConstants.REPORT_MEMBER_SALES_DETAIL_REPORT);
	}

	public MemberSalesDetailReportAction(String name) {
		super(name);
	}

	public MemberSalesDetailReportAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = com.floreantpos.util.POSUtil.getBackOfficeWindow();
		JTabbedPane tabbedPane = window.getTabbedPane();
		
		MemberSalesDetailReportView viewer = null;
		int index = tabbedPane.indexOfTab(com.floreantpos.POSConstants.REPORT_MEMBER_SALES_DETAIL_REPORT);
		if (index == -1) {
			viewer = new MemberSalesDetailReportView();
			tabbedPane.addTab(com.floreantpos.POSConstants.REPORT_MEMBER_SALES_DETAIL_REPORT, viewer);
		}
		else {
			viewer = (MemberSalesDetailReportView) tabbedPane.getComponentAt(index);
		}
		tabbedPane.setSelectedComponent(viewer);
	}

}
