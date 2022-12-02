package com.floreantpos.bo.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.inventory.TransactionInventory;
import com.floreantpos.ui.model.HCReturnDialog;
import com.floreantpos.ui.model.StockControlDialog;

public class HCReturnAction extends AbstractAction {

	public HCReturnAction() {
		super("HC Daily Return");
	}

	public HCReturnAction(String name) {
		super(name);
	}

	public HCReturnAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		String tab_name = "HC Daily Return";
		BackOfficeWindow backOfficeWindow = com.floreantpos.util.POSUtil.getBackOfficeWindow();
		
		HCReturnDialog trans = null;
		JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
		int index = tabbedPane.indexOfTab(tab_name);
		if (index == -1) {
			trans = new HCReturnDialog();
			tabbedPane.addTab(tab_name, trans);
		}
		else {
			trans = (HCReturnDialog) tabbedPane.getComponentAt(index);
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
