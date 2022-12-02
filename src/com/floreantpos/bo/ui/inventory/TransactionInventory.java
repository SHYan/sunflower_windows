package com.floreantpos.bo.ui.inventory;

import com.floreantpos.swing.TransparentPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.explorer.ExplorerButtonPanel;
import com.floreantpos.bo.ui.explorer.TicketExplorer;
import com.floreantpos.main.Application;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ComboItemSelectionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.MenuGroupForm;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;

public class TransactionInventory extends TransparentPanel {

	private JXTable table;
	private BeanTableModel<InventoryTransaction> tableModel;
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	private JLabel lblFromDate;
	private JLabel lblToDate;
	
	private JTextField tfName;
	private JComboBox cbType;
	
	public TransactionInventory(){
		
		HashMap map = new HashMap();
		List<InventoryTransaction> dataSource = IBatisFactory.selectList("inventory_transaction.get_inventory_transaction", map);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		
		tableModel = new BeanTableModel<InventoryTransaction>(InventoryTransaction.class);
		tableModel.addColumn("Transaction Date", "transDate"); 
		tableModel.addColumn("Item Name", "ItemName"); 
		tableModel.addColumn("Type", "tranType"); 
		tableModel.addColumn("Old Qty", "oldQuantity"); 
		tableModel.addColumn("Trans: Qty", "Quantity"); 
		tableModel.addColumn("Price", "unitPrice"); 
		
		tableModel.addColumn("Remark", "remark"); 
		tableModel.addColumn("Vendor Name", "VendorName");
		tableModel.addColumn("Warehouse", "WarehouseName");
		tableModel.addRows(dataSource);
		
		table = new JXTable(tableModel);
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(7).setCellRenderer( centerRenderer );
		table.getColumnModel().getColumn(8).setCellRenderer( centerRenderer );

		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		//add(createButtonPanel(), BorderLayout.SOUTH);
		add(buildSearchForm(), BorderLayout.NORTH);
		
		resizeColumnWidth(table);

	}

	private JPanel buildSearchForm() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[]10[]20[]10[]20[]10[]20[]10[]20[]", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		lblFromDate = new JLabel(com.floreantpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();

		lblToDate = new JLabel(com.floreantpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();
		
		dpStartDate.setPreferredSize(new Dimension(115, 30));
		dpEndDate.setPreferredSize(new Dimension(115, 30));
		
		JLabel lblType = new JLabel("Type"); //$NON-NLS-1$
		cbType = new javax.swing.JComboBox();
		cbType.addItem("ALL"); 
		cbType.addItem("Procurement"); 
		cbType.addItem("Demage"); 
		cbType.addItem("Inventory");
		cbType.addItem("Transfer");
		cbType.addItem("Return");
		cbType.addItem("Convert");
		cbType.addItem("Lose");
		cbType.addItem("Extra");
		cbType.addItem("Other"); 
		cbType.setPreferredSize(new Dimension(198, 30));

		JLabel lblName = new JLabel(Messages.getString("MenuItemExplorer.0")); //$NON-NLS-1$
		tfName = new JTextField(15);
		tfName.setPreferredSize(new Dimension(120, 30));
		try {
			JButton searchBttn = new JButton(Messages.getString("MenuItemExplorer.3")); //$NON-NLS-1$
			searchBttn.setPreferredSize(new Dimension(60, 30));
			//JButton addBttn = new JButton("Inventory Control"); //$NON-NLS-1$
			panel.add(lblFromDate);
			panel.add(dpStartDate);
			
			panel.add(lblToDate);
			panel.add(dpEndDate);
			panel.add(lblName, "align label"); //$NON-NLS-1$
			panel.add(tfName);
			panel.add(lblType); 
			panel.add(cbType); 
			panel.add(searchBttn);
			//panel.add(addBttn);

			Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Search"); //$NON-NLS-1$
			title.setTitleJustification(TitledBorder.LEFT);
			panel.setBorder(title);

			searchBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					searchItem();
				}
			});
			/*
			addBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						StockControlDialog dialog = new StockControlDialog();
						dialog.setSize(1024, 700);
						dialog.open();
					} catch (Exception x) {
						BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
					}
				}
			});
			*/
		} catch (Throwable x) {
			BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
		}

		return panel;
	}

	private void searchItem() {
		Date fromDate = dpStartDate.getDate();
		Date toDate = dpEndDate.getDate();
		fromDate = DateUtil.startOfDay(fromDate, 0, 0 );
		toDate = DateUtil.endOfDay(toDate, 23, 59);
		
		Object selectedType = cbType.getSelectedItem();
		HashMap map = new HashMap();
		map.put("startDate", fromDate); //$NON-NLS-1$
		map.put("endDate", toDate);
		if(selectedType == "ALL" )  
			map.put("selectedType", null);
		else
			map.put("selectedType", selectedType.toString());
		map.put("keyword", tfName.getText().toString());
		List<InventoryTransaction> dataSource = IBatisFactory.selectList("inventory_transaction.get_inventory_transaction", map);
		tableModel.removeAll();
		tableModel.addRows(dataSource);
		
		ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
				"Report Query", "Stock Transaction History");
	}

	private TransparentPanel createButtonPanel() {
		JButton saveButton = new JButton("DELETE");
		saveButton.setPreferredSize(new Dimension(60, 30));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0) {
						POSMessageDialog.showMessage(POSUtil.getBackOfficeWindow(), "NEED SELECT TRANSACTION");
						return;
					}
					index = table.convertRowIndexToModel(index);
					InventoryTransaction selectedTran = tableModel.getRows().get(index);
					if (POSMessageDialog.showYesNoQuestionDialog(TransactionInventory.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}
					POSMessageDialog.showYesNoQuestionDialog(TransactionInventory.this, selectedTran.getItemName(), POSConstants.DELETE);
					tableModel.removeRow(index);
					table.repaint();
					
				} catch (Exception e2) {
					BOMessageDialog.showError(TransactionInventory.this, POSConstants.ERROR_MESSAGE, e2);
					return;
				}
			}
		});
		
		TransparentPanel panel = new TransparentPanel();
		panel.setLayout(new MigLayout("", "[]10[]20", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		
		panel.add(saveButton);
		return panel;
	}

	protected MenuGroup getSelectedMenuGroup(MenuGroup defaultValue) {
		List<MenuGroup> menuGroups = MenuGroupDAO.getInstance().findAll();
		ComboItemSelectionDialog dialog = new ComboItemSelectionDialog(Messages.getString("SELECT_MENU_GROUP"), Messages.getString("PosMessage.272"), menuGroups, false);
		dialog.setSelectedItem(defaultValue);
		dialog.setVisibleNewButton(true);
		dialog.pack();
		dialog.open();

		if (dialog.isCanceled())
			return null;

		if (dialog.isNewItem()) {
			MenuGroup foodGroup = new MenuGroup();
			MenuGroupForm editor = new MenuGroupForm(foodGroup);
			BeanEditorDialog editorDialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
			editorDialog.open();
			if (editorDialog.isCanceled())
				return null;
			return getSelectedMenuGroup(foodGroup);
		}
		return (MenuGroup) dialog.getSelectedItem();
	}

	public void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			columnModel.getColumn(column).setPreferredWidth((Integer) getColumnWidth().get(column));
		}
	}

	private List getColumnWidth() {
		List<Integer> columnWidth = new ArrayList();
		//columnWidth.add(20);
		columnWidth.add(100);
		columnWidth.add(200);
		columnWidth.add(50);
		columnWidth.add(50);
		columnWidth.add(50);
		columnWidth.add(50);
		columnWidth.add(100);
		columnWidth.add(100);
		columnWidth.add(100);

		return columnWidth;
	}
}