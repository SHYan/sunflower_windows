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
/*
 * BackOfficeWindow.java
 *
 * Created on August 16, 2006, 12:43 PM
 */

package com.floreantpos.bo.ui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.floreantpos.Messages;
import com.floreantpos.actions.AboutAction;
import com.floreantpos.bo.actions.ActionHistoryAction;
import com.floreantpos.bo.actions.AttendanceHistoryAction;
import com.floreantpos.bo.actions.BackupRestoreAction;
import com.floreantpos.bo.actions.CategoryExplorerAction;
import com.floreantpos.bo.actions.ConfigureRestaurantAction;
import com.floreantpos.bo.actions.CookingInstructionExplorerAction;
import com.floreantpos.bo.actions.CouponExplorerAction;
import com.floreantpos.bo.actions.OrderPaymentReportAction;
import com.floreantpos.bo.actions.GroupExplorerAction;
import com.floreantpos.bo.actions.HCReturnAction;
import com.floreantpos.bo.actions.HCReturnListAction;
import com.floreantpos.bo.actions.InventoryTransactonAction;
import com.floreantpos.bo.actions.InventoryTransactonListAction;
import com.floreantpos.bo.actions.InventoryVendorAction;
import com.floreantpos.bo.actions.InventoryWarehouseAction;
import com.floreantpos.bo.actions.ItemExplorerAction;
import com.floreantpos.bo.actions.LanguageSelectionAction;
import com.floreantpos.bo.actions.MenuUsageReportAction;
import com.floreantpos.bo.actions.ModifierExplorerAction;
import com.floreantpos.bo.actions.ModifierGroupExplorerAction;
import com.floreantpos.bo.actions.OrdersTypeExplorerAction;
import com.floreantpos.bo.actions.PaymentSummaryReportAction;
import com.floreantpos.bo.actions.ShiftExplorerAction;
import com.floreantpos.bo.actions.TaxExplorerAction;
import com.floreantpos.bo.actions.UserExplorerAction;
import com.floreantpos.bo.actions.UserTypeExplorerAction;
import com.floreantpos.bo.actions.Report.DailySalesReportAction;
import com.floreantpos.bo.actions.Report.HCDailyStockReportAction;
import com.floreantpos.bo.actions.Report.HourlySalesReportAction;
import com.floreantpos.bo.actions.Report.MemberSalesDetailReportAction;
import com.floreantpos.bo.actions.Report.MemberSalesReportAction;
import com.floreantpos.bo.actions.Report.ModifierSalesReportAction;
import com.floreantpos.bo.actions.Report.OrderDetailReportAction;
import com.floreantpos.bo.actions.Report.OrderStatusReportAction;
import com.floreantpos.bo.actions.Report.PayoutReportAction;
import com.floreantpos.bo.actions.Report.ProductReturnReportAction;
import com.floreantpos.bo.actions.Report.ProductSalesReportAction;
import com.floreantpos.bo.actions.Report.ProfitLossReportAction;
import com.floreantpos.bo.actions.Report.StockAdjustReportAction;
import com.floreantpos.bo.actions.Report.StockReportAction;
import com.floreantpos.bo.actions.Report.TruncateTransactionAction;
import com.floreantpos.bo.actions.Report.YearlyReportAction;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.customPayment.CustomPaymentBrowserAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.table.ShowTableBrowserAction;
import com.jidesoft.swing.JideTabbedPane;

/**
 *
 * @author  MShahriar
 */
public class BackOfficeWindow extends javax.swing.JFrame {

	private static final String POSY = "bwy";//$NON-NLS-1$
	private static final String POSX = "bwx";//$NON-NLS-1$
	private static final String WINDOW_HEIGHT = "bwheight";//$NON-NLS-1$
	private static final String WINDOW_WIDTH = "bwwidth";//$NON-NLS-1$
	private JMenu floorPlanMenu;
	private static BackOfficeWindow instance;
	private JMenuBar menuBar;
	private static User user;
	Set<UserPermission> permissions = null;

	/** Creates new form BackOfficeWindow */
	public BackOfficeWindow() {
		setIconImage(Application.getApplicationIcon().getImage());

		initComponents();

		createMenus();
		positionWindow();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		setTitle(Application.getTitle() + "- " + com.floreantpos.POSConstants.BACK_OFFICE); //$NON-NLS-1$
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

		ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
				"User Login", "User Login to BackOffice");
	}

	private void positionWindow() {
		int width = AppConfig.getInt(WINDOW_WIDTH, 900); //$NON-NLS-1$
		int height = AppConfig.getInt(WINDOW_HEIGHT, 700); //$NON-NLS-1$
		setSize(width, height);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - width) >> 1;
		int y = (screenSize.height - height) >> 1;

		x = AppConfig.getInt(POSX, x); //$NON-NLS-1$
		y = AppConfig.getInt(POSY, y); //$NON-NLS-1$

		setLocation(x, y);
	}

	private void createMenus() {
		user = Application.getCurrentUser();
		UserType newUserType = user.getType();

		if (newUserType != null) {
			permissions = newUserType.getPermissions();
		}

		menuBar = new JMenuBar();

		if (newUserType == null) {
			/*
			createAdminMenu(menuBar);
			createExplorerMenu(menuBar);
			createInventoryMenu(menuBar);
			createReportMenu(menuBar);
			createFloorMenu(menuBar);
			*/
		} else {
			createAdminMenu(menuBar);
			createExplorerMenu(menuBar);
			createInventoryMenu(menuBar);
			createReportMenu(menuBar);
			createFloorMenu(menuBar);
		}
		

		/*
		for (FloreantPlugin plugin : ExtensionManager.getPlugins()) {
			plugin.initBackoffice();
		}*/

		JMenu helpMenu = new JMenu(Messages.getString("BackOfficeWindow.0")); //$NON-NLS-1$
		//helpMenu.add(new UpdateAction()).setPreferredSize(new Dimension(250, 30));
		helpMenu.add(new AboutAction()).setPreferredSize(new Dimension(250, 30));

		helpMenu.add(new LanguageSelectionAction()).setPreferredSize(new Dimension(250, 30));
		helpMenu.setPreferredSize(new Dimension(70, 35));

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private void createAdminMenu(JMenuBar menuBar) {
		JMenu adminMenu = new JMenu(com.floreantpos.POSConstants.ADMIN);
		if(permissions != null) {
			if(permissions.contains(UserPermission.RESTAURANT_MAINTENANCE))
				adminMenu.add(new ConfigureRestaurantAction()).setPreferredSize(new Dimension(250, 30));
			//adminMenu.add(new CurrencyExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.USER_MAINTENANCE)) {
				adminMenu.add(new UserExplorerAction()).setPreferredSize(new Dimension(250, 30));
				adminMenu.add(new UserTypeExplorerAction()).setPreferredSize(new Dimension(250, 30));
			}
			//adminMenu.add(new ViewGratuitiesAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.BACKUP_RESTORE))
				adminMenu.add(new BackupRestoreAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.ATTENDANCE_HISTORY))
				adminMenu.add(new AttendanceHistoryAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.ACTION_HISTORY))
				adminMenu.add(new ActionHistoryAction()).setPreferredSize(new Dimension(250, 30));
			//reportMenu.add(new EmployeeAttendanceAction()).setPreferredSize(new Dimension(250, 30));
			//adminMenu.add(new JournalReportAction()).setPreferredSize(new Dimension(250, 30));
			if(user!=null && user.getId()==998)
				adminMenu.add(new TruncateTransactionAction()).setPreferredSize(new Dimension(250, 30));	
		}
		adminMenu.setPreferredSize(new Dimension(60, 35));
		menuBar.add(adminMenu);
	}

	private void createInventoryMenu(JMenuBar menuBar){
		JMenu inventoryMenu = new JMenu(com.floreantpos.POSConstants.INVENTORY);
		if(permissions != null) {
			if(permissions.contains(UserPermission.INVENTORY_TRANSACTION))
				inventoryMenu.add(new InventoryTransactonAction()).setPreferredSize(new Dimension(250, 30));	
			inventoryMenu.add(new HCReturnAction()).setPreferredSize(new Dimension(250, 30));
			inventoryMenu.add(new HCReturnListAction()).setPreferredSize(new Dimension(250, 30));
			inventoryMenu.add(new HCDailyStockReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.STOCK_ADJUST_REPORT))
				inventoryMenu.add(new StockAdjustReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.STOCK_BALANCE_REPORT))
				inventoryMenu.add(new StockReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.STOCK_ADJUST_REPORT))
				inventoryMenu.add(new InventoryTransactonListAction()).setPreferredSize(new Dimension(250, 30));
			inventoryMenu.add(new InventoryVendorAction()).setPreferredSize(new Dimension(250, 30));
			inventoryMenu.add(new InventoryWarehouseAction()).setPreferredSize(new Dimension(250, 30));
		}
		
		inventoryMenu.setPreferredSize(new Dimension(70, 35));
		menuBar.add(inventoryMenu);
		
	}

	private void createReportMenu(JMenuBar menuBar) {
		JMenu reportMenu = new JMenu(com.floreantpos.POSConstants.REPORTS);
		if(permissions != null) {
			if(permissions.contains(UserPermission.REPORT_YEAR_REPORT))
				reportMenu.add(new YearlyReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_DAILY_SALES_REPORT))
				reportMenu.add(new DailySalesReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_HOURLY_SALES_REPORT))
				reportMenu.add(new HourlySalesReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_ORDER_STATUS_REPORT))
				reportMenu.add(new OrderStatusReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_ORDER_DETAIL_REPORT))
				reportMenu.add(new OrderDetailReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.MENU_USAGE_REPORT))
				reportMenu.add(new MenuUsageReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_Product_SALES_REPORT))
				reportMenu.add(new ProductSalesReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_Product_RETURN_REPORT))
				reportMenu.add(new ProductReturnReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_MODIFIER_SALES_REPORT))
				reportMenu.add(new ModifierSalesReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_MEMBER_SALES_REPORT))
				reportMenu.add(new MemberSalesReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_MEMBER_SALES_DETAIL_REPORT))
				reportMenu.add(new MemberSalesDetailReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.CREDIT_CARD_REPORT))
				reportMenu.add(new PaymentSummaryReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.CUSTOM_PAYMENT_REPORT))
				reportMenu.add(new OrderPaymentReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_PAYOUT_REPORT))
				reportMenu.add(new PayoutReportAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.REPORT_PROFIT_LOSS_REPORT))
				reportMenu.add(new ProfitLossReportAction()).setPreferredSize(new Dimension(250, 30));
			
		}
				
		//reportMenu.add(new SalesReportAction());
		//reportMenu.add(new OpenTicketSummaryReportAction());
		/*
		reportMenu.add(new DrawerPullReportExplorerAction()).setPreferredSize(new Dimension(250, 30));
		reportMenu.add(new KeyStatisticsSalesReportAction());
		//reportMenu.add(new SalesAnalysisReportAction());
		//reportMenu.add(new ServerProductivityReportAction());
		
		reportMenu.add(new SalesBalanceReportAction());
		reportMenu.add(new SalesExceptionReportAction());
		//reportMenu.add(new SalesDetailReportAction());
		*/
		//reportMenu.add(new PayrollReportAction()).setPreferredSize(new Dimension(250, 30));

		//reportMenu.add(new HourlyLaborReportAction()).setPreferredSize(new Dimension(250, 30));
		//reportMenu.add(new SaleSummaryAction());
		//reportMenu.add(new TicketExplorerAction());
		
		reportMenu.setPreferredSize(new Dimension(70, 35));

		menuBar.add(reportMenu);
	}

	private void createExplorerMenu(JMenuBar menuBar) {
		JMenu explorerMenu = new JMenu(com.floreantpos.POSConstants.EXPLORERS);
		explorerMenu.setPreferredSize(new Dimension(70, 35));

		menuBar.add(explorerMenu);
		//JMenu subMenuPizza = new JMenu(Messages.getString("BackOfficeWindow.1")); //$NON-NLS-1$

		if(permissions != null) {
			if (TerminalConfig.isMultipleOrderSupported() && permissions.contains(UserPermission.ORDER_TYPE_M)) {
				explorerMenu.add(new OrdersTypeExplorerAction()).setPreferredSize(new Dimension(250, 30));
			}
			if(permissions.contains(UserPermission.MENU_CATEGORIES_M))
				explorerMenu.add(new CategoryExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.MENU_GROUPS_M))
				explorerMenu.add(new GroupExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.MENU_ITEMS_M))
				explorerMenu.add(new ItemExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.MENU_MODIFIER_GROUPS_M))
				explorerMenu.add(new ModifierGroupExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.MENU_MODIFIERS_M))
				explorerMenu.add(new ModifierExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.SHIFTS_M))
				explorerMenu.add(new ShiftExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.COUPONS_AND_DISCOUNTS_M))
				explorerMenu.add(new CouponExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.TAX_M))
				explorerMenu.add(new TaxExplorerAction()).setPreferredSize(new Dimension(250, 30));
			if(permissions.contains(UserPermission.CUSTOM_PAYMENT_M))
				explorerMenu.add(new CustomPaymentBrowserAction()).setPreferredSize(new Dimension(250, 30));
			explorerMenu.add(new CookingInstructionExplorerAction()).setPreferredSize(new Dimension(250, 30));	
		}
		//
		//explorerMenu.add(new PizzaExplorerAction()).setPreferredSize(new Dimension(250, 30));
		//explorerMenu.add(subMenuPizza);

		//subMenuPizza.add(new MenuItemSizeExplorerAction());
		//subMenuPizza.add(new PizzaCrustExplorerAction());
		//subMenuPizza.add(new PizzaItemExplorerAction());
		//subMenuPizza.add(new PizzaModifierExplorerAction());
		//explorerMenu.add(new MultiplierExplorerAction());

		/*
		OrderServiceExtension plugin = (OrderServiceExtension) ExtensionManager.getPlugin(OrderServiceExtension.class);
		if (plugin == null) {
			return;
		}

		plugin.createCustomerMenu(explorerMenu);
		*/
	}


	private void createFloorMenu(JMenuBar menuBar) {

		floorPlanMenu = new JMenu(Messages.getString("BackOfficeWindow.2")); //$NON-NLS-1$
		floorPlanMenu.add(new ShowTableBrowserAction()).setPreferredSize(new Dimension(250, 30));
		floorPlanMenu.setPreferredSize(new Dimension(70, 35));

		menuBar.add(floorPlanMenu);

	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		tabbedPane = new JideTabbedPane();
		tabbedPane.setTabShape(JideTabbedPane.SHAPE_WINDOWS);
		tabbedPane.setShowCloseButtonOnTab(true);
		tabbedPane.setTabInsets(new Insets(5, 5, 5, 5));
		Font font = new Font(tabbedPane.getFont().getName(), Font.PLAIN, PosUIManager.getDefaultFontSize());
		tabbedPane.setFont(font);

		getContentPane().setLayout(new java.awt.BorderLayout(5, 0));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		jPanel1.setLayout(new java.awt.BorderLayout(5, 0));

		jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jPanel1.add(tabbedPane, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new BackOfficeWindow().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel jPanel1;
	private JideTabbedPane tabbedPane;

	// End of variables declaration//GEN-END:variables

	public javax.swing.JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	private void saveSizeAndLocation() {
		AppConfig.putInt(WINDOW_WIDTH, BackOfficeWindow.this.getWidth());
		AppConfig.putInt(WINDOW_HEIGHT, BackOfficeWindow.this.getHeight()); //$NON-NLS-1$
		AppConfig.putInt(POSX, BackOfficeWindow.this.getX()); //$NON-NLS-1$
		AppConfig.putInt(POSY, BackOfficeWindow.this.getY()); //$NON-NLS-1$
	}

	public void close() {
		saveSizeAndLocation();
		dispose();
	}

	public static User getUser() {
		return user;
	}
	public static BackOfficeWindow getInstance() {
		System.out.println("getInstance");
		if (instance == null) {
			instance = new BackOfficeWindow();
		}
		return instance;
	}

	public JMenuBar getBackOfficeMenuBar() {
		return menuBar;
	}

	/**
	 * @return the floorPlanMenu
	 */
	public JMenu getFloorPlanMenu() {
		return floorPlanMenu;
	}

	/**
	 * @param floorPlanMenu the floorPlanMenu to set
	 */
	public void setFloorPlanMenu(JMenu floorPlanMenu) {
		this.floorPlanMenu = floorPlanMenu;
	}

	//	public static BackOfficeWindow getInstance() {
	//		if (instance == null) {
	//			instance = new BackOfficeWindow();
	//			Application.getInstance().setBackOfficeWindow(instance);
	//		}
	//
	//		return instance;
	//	}

}
