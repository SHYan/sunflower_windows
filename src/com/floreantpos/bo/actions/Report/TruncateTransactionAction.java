package com.floreantpos.bo.actions.Report;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.floreantpos.main.Application;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;

public class TruncateTransactionAction extends AbstractAction {

	public TruncateTransactionAction() {
		super("Truncate Transaction");
	}

	public TruncateTransactionAction(String name) {
		super(name);
	}

	public TruncateTransactionAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		if (ConfirmDeleteDialog.showMessage(POSUtil.getBackOfficeWindow(),
				"Are you sure to delete all transactions?",
				com.floreantpos.POSConstants.DELETE) == ConfirmDeleteDialog.YES) {

			String[] truncate_list = { "action_history", "attendence_history", "cooking_instruction",
					"custom_payment", "delivery_address", "delivery_charge", "delivery_configuration",
					"delivery_instruction", "drawer_assigned_history", "employee_in_out_history",
					"guest_check_print", "kit_ticket_table_num",
					"kitchen_ticket_item", "kitchen_ticket",
					"inventory_transaction", "table_booking_info",
					"table_booking_mapping", "table_ticket_num", "shop_table_status",
					"table_type_relation", "ticket_item_addon_relation",
					"ticket_item_cooking_instruction", "ticket_item_discount",
					"ticket_item_modifier_relation", "ticket_item_modifier",
					"ticket_item", "ticket_discount", "ticket_table_num", "transaction_properties",
					"transactions" };

			try {
				for (int i = 0; i < truncate_list.length; i++) {
					// System.out.print(truncate_list[i]);
					HashMap map = new HashMap();
					map.put("table_name", truncate_list[i]);
					IBatisFactory.deleteSQL(
							"truncate_transaction.truncate_data", map);
				}
				IBatisFactory.deleteSQL("truncate_transaction.delete_ticket",
						null);
				IBatisFactory.deleteSQL("truncate_transaction.delete_gratuity",
						null);
				IBatisFactory.updateSQL(
						"truncate_transaction.update_menu_item", null);
				ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
						"BackOffice Setting", "***CLEAR ALL TRANSACTIONS***");
			} catch (Exception ex) {
				POSMessageDialog.showError(ex.getMessage());
			}

		}
	}
}
