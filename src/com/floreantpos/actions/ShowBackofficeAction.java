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
package com.floreantpos.actions;

import javax.swing.Action;
import javax.swing.JOptionPane;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;
import com.floreantpos.ui.dialog.POSMessageDialog;

public class ShowBackofficeAction extends PosAction {

	public ShowBackofficeAction() {
		super(POSConstants.BACK_OFFICE_BUTTON_TEXT); //$NON-NLS-1$
		setRequiredPermission(UserPermission.VIEW_BACK_OFFICE);
	}

	public ShowBackofficeAction(boolean showText, boolean showIcon) {
		if (showText) {
			putValue(Action.NAME, UserPermission.VIEW_BACK_OFFICE); //$NON-NLS-1$
		}
		if (showIcon) {
			putValue(Action.SMALL_ICON, IconFactory.getIcon("backoffice.png")); //$NON-NLS-1$
		}
		
		setRequiredPermission(UserPermission.VIEW_BACK_OFFICE);
	}

	@Override
	public void execute() {
		BackOfficeWindow window = com.floreantpos.util.POSUtil.getBackOfficeWindow();
		
		if (window == null) {
			window = new BackOfficeWindow();
		}else {
			if(BackOfficeWindow.getUser()!=null) {
				if(BackOfficeWindow.getUser().getId()!=Application.getCurrentUser().getId()) {
					int option = POSMessageDialog.showYesNoQuestionDialog(Application.getPosWindow(), Messages.getString("LoginDuplicate"), Messages.getString("Application.44")); //$NON-NLS-1$ //$NON-NLS-2$
					if (option == JOptionPane.YES_OPTION) {
						Application.getInstance().restartWindow();
					}else {
						window.dispose();
						return;
					}
				}
					
			}
		}
		window.setVisible(true);
		window.toFront();
	}

}
