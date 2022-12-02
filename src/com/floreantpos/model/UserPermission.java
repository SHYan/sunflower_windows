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
package com.floreantpos.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.Messages;
import com.floreantpos.model.base.BaseUserPermission;

@XmlRootElement(name = "user-permission")
public class UserPermission extends BaseUserPermission {
	private static final long serialVersionUID = 1L;
	private String resourceBundlePropertyName;
	private boolean visibleWithoutPermission = true;
	
	/*[CONSTRUCTOR MARKER BEGIN]*/
	public UserPermission() {
		super();
	}

	public UserPermission(java.lang.String name, String resourceBundlePropertyName) {
		super(name);
		this.resourceBundlePropertyName = resourceBundlePropertyName;
	}

	/*[CONSTRUCTOR MARKER END]*/

	public UserPermission(java.lang.String name, String resourceBundlePropertyName, boolean visibleWithoutPermission) {
		super(name);
		this.resourceBundlePropertyName = resourceBundlePropertyName;
		this.visibleWithoutPermission = visibleWithoutPermission;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserPermission)) {
			return false;
		}

		UserPermission p = (UserPermission) obj;

		return this.getName().equalsIgnoreCase(p.getName());
	}

	@Override
	public String toString() {
		if (StringUtils.isEmpty(resourceBundlePropertyName)) {
			return getName();
		}
		return Messages.getString(resourceBundlePropertyName);
	}

	public final static UserPermission ADD_DISCOUNT = new UserPermission("Add Discount", "UserPermission.16"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission ADD_MISC_ITEM = new UserPermission("Add MISC ITEM", "ADD_MISC_ITEM"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission REFUND = new UserPermission("Refund", "UserPermission.17"); //$NON-NLS-1$ //$NON-NLS-2$
	/* J1.1 13*/
	public final static UserPermission RETURN_ITEM = new UserPermission("Return Item", "ReturnItem");
	/* J1.1 48*/
	public final static UserPermission OPEN_PRICE = new UserPermission("Allow open price edit", "OPEN_PRICE");
	public final static UserPermission HOLD_TICKET = new UserPermission("Hold Ticket", "UserPermission.26"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public final static UserPermission VIEW_ALL_OPEN_TICKETS = new UserPermission("View All Open Ticket", "UserPermission.1"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission CREATE_TICKET = new UserPermission("Create New Ticket", "UserPermission.0"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission SPLIT_TICKET = new UserPermission("Split Ticket", "UserPermission.10"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission MERGE_TICKET = new UserPermission("Merge Ticket", "MERGE_TICKET_BUTTON_TEXT"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission VOID_TICKET = new UserPermission("Void Ticket", "UserPermission.3"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission SETTLE_TICKET = new UserPermission("Settle Ticket", "UserPermission.11"); //$NON-NLS-1$ //$NON-NLS-2$
	
	//public final static UserPermission PERFORM_MANAGER_TASK = new UserPermission("Perform Manager Task", "UserPermission.5"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission VIEW_BACK_OFFICE = new UserPermission("View Back Office", "UserPermission.6", false); //$NON-NLS-1$ //$NON-NLS-2$
	
	//public final static UserPermission AUTHORIZE_TICKETS = new UserPermission("Authorize Tickets", "UserPermission.7"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission DRAWER_ASSIGNMENT = new UserPermission("Drawer Assignment", "UserPermission.8"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission DRAWER_PULL = new UserPermission("Drawer Pull", "UserPermission.9"); //$NON-NLS-1$ //$NON-NLS-2$
	
	//public final static UserPermission REOPEN_TICKET = new UserPermission("Reopen Ticket", "UserPermission.12"); //$NON-NLS-1$ //$NON-NLS-2$
	
	//ublic final static UserPermission VIEW_EXPLORERS = new UserPermission("View Explorers", "UserPermission.18"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission VIEW_REPORTS = new UserPermission("View Reports", "UserPermission.19"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission MANAGE_TABLE_LAYOUT = new UserPermission("Manage Table Layout", "UserPermission.20"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission TABLE_BOOKING = new UserPermission("Booking", "UserPermission.22"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission MODIFY_PRINTED_TICKET = new UserPermission("Modify Printed Ticket", "UserPermission.21"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission TRANSFER_TICKET = new UserPermission("Transfer Ticket", "UserPermission.23"); //$NON-NLS-1$ //$NON-NLS-2$
	//public final static UserPermission ALL_FUNCTIONS = new UserPermission("All Functions", "UserPermission.25"); //$NON-NLS-1$ //$NON-NLS-2$
	
	//public final static UserPermission VIEW_ALL_CLOSE_TICKETS = new UserPermission("View All Close Tickets", "UserPermission.27"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission PAY_OUT = new UserPermission("Pay 0-ut", "UserPermission.13"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission SHUT_DOWN = new UserPermission("Shut Down", "UserPermission.15"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission KITCHEN_DISPLAY = new UserPermission("Kitchen Display", "UserPermission.24"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission QUICK_MAINTENANCE = new UserPermission("Quick Maintenance", "Quick_Maintenance"); //$NON-NLS-1$ //$NON-NLS-2$
	public final static UserPermission PERFORM_ADMINISTRATIVE_TASK = new UserPermission("Perform Administrative Task", "UserPermission.4"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public final static UserPermission RESTAURANT_MAINTENANCE = new UserPermission("Restaurant Maintenance", "Restaurant_Maintenance");
	public final static UserPermission USER_MAINTENANCE = new UserPermission("User Maintenance", "User_Maintenance");
	public final static UserPermission BACKUP_RESTORE = new UserPermission("Backup & Restory", "BackupRestoreAction.0");
	public final static UserPermission ATTENDANCE_HISTORY = new UserPermission("Attendance History", "AttendanceHistoryAction.0");
	public final static UserPermission ACTION_HISTORY = new UserPermission("Action History", "ActionHistory");
	public final static UserPermission TRUNCATE_DATA = new UserPermission("Truncate Data", "Truncate_Data");
	
	public final static UserPermission ORDER_TYPE_M  = new UserPermission("Order Type Manage", "ORDER_TYPE_M");
	public final static UserPermission MENU_CATEGORIES_M  = new UserPermission("Menu Categories Manage", "MENU_CATEGORIES_M");
	public final static UserPermission MENU_GROUPS_M = new UserPermission("Menu Groups Manage", "MENU_GROUPS_M");
	public final static UserPermission MENU_ITEMS_M = new UserPermission("Menu Items Manage", "MENU_ITEMS_M");
	public final static UserPermission MENU_MODIFIER_GROUPS_M = new UserPermission("Modifier Groups Manage", "MENU_MODIFIER_GROUPS_M");
	public final static UserPermission MENU_MODIFIERS_M = new UserPermission("Modifiers Manage", "MENU_MODIFIERS_M");
	public final static UserPermission SHIFTS_M = new UserPermission("Shifts Manage", "SHIFTS_M");
	public final static UserPermission COUPONS_AND_DISCOUNTS_M = new UserPermission("Coupons & Discounts Manage", "COUPONS_AND_DISCOUNTS_M");
	public final static UserPermission TAX_M = new UserPermission("Tax Manage", "TAX_M");
	public final static UserPermission CUSTOM_PAYMENT_M = new UserPermission("Custom Payment Manage", "CUSTOM_PAYMENT_M");
	
	public final static UserPermission INVENTORY_TRANSACTION = new UserPermission("Inventory Transaction", "INVENTORY_TRANSACTION");
	public final static UserPermission STOCK_ADJUST_REPORT = new UserPermission("Stock Adjust Report", "REPORT_STOCK_ADJUST_REPORT");
	public final static UserPermission STOCK_BALANCE_REPORT = new UserPermission("Stock Balance Report", "REPORT_STOCK_REPORT");
	
	
	public final static UserPermission REPORT_YEAR_REPORT = new UserPermission("Yearly Sales Report", "REPORT_YEAR_REPORT");
	public final static UserPermission REPORT_DAILY_SALES_REPORT = new UserPermission("Daily Sales Report", "REPORT_DAILY_SALES_REPORT");
	public final static UserPermission REPORT_HOURLY_SALES_REPORT = new UserPermission("Hourly Sales Report", "REPORT_HOURLY_SALES_REPORT");
	public final static UserPermission REPORT_ORDER_STATUS_REPORT = new UserPermission("Order Status Report", "REPORT_ORDER_STATUS_REPORT");
	public final static UserPermission REPORT_ORDER_DETAIL_REPORT = new UserPermission("Order Detail Report", "REPORT_ORDER_DETAIL_REPORT");
	public final static UserPermission MENU_USAGE_REPORT = new UserPermission("Category Sales Report", "PosMessage.115");
	public final static UserPermission REPORT_Product_SALES_REPORT = new UserPermission("Product Sales Report", "REPORT_Product_SALES_REPORT");
	public final static UserPermission REPORT_Product_RETURN_REPORT = new UserPermission("Product Return Report", "REPORT_Product_Return_REPORT");
	public final static UserPermission REPORT_MODIFIER_SALES_REPORT = new UserPermission("Modifier Sales Report", "REPORT_MODIFIER_SALES_REPORT");
	public final static UserPermission REPORT_MEMBER_SALES_REPORT = new UserPermission("Member Transaction Report", "REPORT_MEMBER_SALES_REPORT");
	public final static UserPermission REPORT_MEMBER_SALES_DETAIL_REPORT = new UserPermission("Member Transaction Detail Report", "REPORT_MEMBER_SALES_DETAIL_REPORT");
	public final static UserPermission CREDIT_CARD_REPORT = new UserPermission("Daily Payment Report", "PosMessage.142");
	public final static UserPermission CUSTOM_PAYMENT_REPORT = new UserPermission("Order Payment Report", "CUSTOM_PAYMENT_REPORT");
	public final static UserPermission REPORT_PAYOUT_REPORT = new UserPermission("Pay Out Report", "REPORT_PAYOUT_REPORT");
	public final static UserPermission REPORT_PROFIT_LOSS_REPORT = new UserPermission("Profit & Loss Report", "REPORT_PROFIT_LOSS_REPORT");
	
	public final static UserPermission[] permissions = new UserPermission[] { RESTAURANT_MAINTENANCE, USER_MAINTENANCE, BACKUP_RESTORE, ATTENDANCE_HISTORY, ACTION_HISTORY,
			ORDER_TYPE_M, MENU_CATEGORIES_M, MENU_GROUPS_M, MENU_ITEMS_M, MENU_MODIFIER_GROUPS_M, MENU_MODIFIERS_M, SHIFTS_M,
			COUPONS_AND_DISCOUNTS_M, TAX_M, CUSTOM_PAYMENT_M,
			INVENTORY_TRANSACTION, STOCK_ADJUST_REPORT, STOCK_BALANCE_REPORT, REPORT_ORDER_STATUS_REPORT, 
			REPORT_YEAR_REPORT, REPORT_DAILY_SALES_REPORT, REPORT_HOURLY_SALES_REPORT, REPORT_ORDER_DETAIL_REPORT,
			MENU_USAGE_REPORT, REPORT_Product_SALES_REPORT, REPORT_Product_RETURN_REPORT, REPORT_MODIFIER_SALES_REPORT,
			REPORT_MEMBER_SALES_REPORT, REPORT_MEMBER_SALES_DETAIL_REPORT, CREDIT_CARD_REPORT, REPORT_PAYOUT_REPORT,CUSTOM_PAYMENT_REPORT, REPORT_PROFIT_LOSS_REPORT,
			
			VIEW_ALL_OPEN_TICKETS, CREATE_TICKET, ADD_DISCOUNT, ADD_MISC_ITEM, REFUND, RETURN_ITEM, OPEN_PRICE,
			SPLIT_TICKET, MERGE_TICKET, HOLD_TICKET,  VOID_TICKET, 
			VIEW_BACK_OFFICE, PAY_OUT, DRAWER_ASSIGNMENT, DRAWER_PULL, 
			PERFORM_ADMINISTRATIVE_TASK, KITCHEN_DISPLAY, QUICK_MAINTENANCE, SHUT_DOWN};

	public boolean isVisibleWithoutPermission() {
		return visibleWithoutPermission;
	}

	public void setVisibleWithoutPermission(boolean visibleWithoutPermission) {
		this.visibleWithoutPermission = visibleWithoutPermission;
	}
}