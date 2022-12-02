package com.floreantpos.bo.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.ui.model.HCReturnListDialog;

public class HCReturnListAction extends AbstractAction {

	public HCReturnListAction() {
		super("HC Daily Return List");
	}

	public HCReturnListAction(String name) {
		super(name);
	}

	public HCReturnListAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		String tab_name = "HC Daily Return List";
		BackOfficeWindow backOfficeWindow = com.floreantpos.util.POSUtil.getBackOfficeWindow();
		
		HCReturnListDialog trans = null;
		JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
		int index = tabbedPane.indexOfTab(tab_name);
		if (index == -1) {
			trans = new HCReturnListDialog();
			tabbedPane.addTab(tab_name, trans);
		}
		else {
			trans = (HCReturnListDialog) tabbedPane.getComponentAt(index);
		}
		tabbedPane.setSelectedComponent(trans);
		/*
		try {
			StockControlDialog dialog = new StockControlDialog();
			dialog.setSize(1024, 700);
			dialog.open();
		} catch (Exception x) {
			BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
		}
		*/
	}

}
