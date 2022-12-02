package com.floreantpos.bo.ui.inventory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXTable;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.main.Application;
import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.WarehouseEntryDialog;

public class WarehouseInventory extends TransparentPanel {
	private JTable table;
	private BeanTableModel<InventoryWarehouse> tableModel;
	
	public void doEditSelected() {
		try {
			int index = table.getSelectedRow();
			if (index < 0)
				return;
			
			index = table.convertRowIndexToModel(index);
			InventoryWarehouse wh = (InventoryWarehouse) tableModel.getRow(index);
			WarehouseEntryDialog dialog = new WarehouseEntryDialog();
			dialog.setInventoryWarehouse(wh);
			dialog.open();
			if (dialog.isCanceled())
				return;

			table.repaint();
			ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
					"Stock", "Edit Warehouse "+wh.getName());
		} catch (Throwable x) {
		BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
		}
	}
	
	public WarehouseInventory() {
		List<InventoryWarehouse> warehouse = new InventoryWarehouseDAO().findAll();
		
		tableModel = new BeanTableModel<InventoryWarehouse>(InventoryWarehouse.class);
		tableModel.addColumn(POSConstants.ID.toUpperCase(), "id"); //$NON-NLS-1$
		tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name"); //$NON-NLS-1$
		tableModel.addRows(warehouse);
		
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new CustomCellRenderer());
		table.setRowHeight(POSConstants.TABLE_ROW_HEIGHT);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					doEditSelected();
				}
			}
		});
		
		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		
		JButton addButton = new JButton(com.floreantpos.POSConstants.ADD);
		addButton.setPreferredSize(new Dimension(60, 40));
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					WarehouseEntryDialog dialog = new WarehouseEntryDialog();
					dialog.open();
					if (dialog.isCanceled())
						return;
					InventoryWarehouse wh = dialog.getInventoryWarehouse();
					tableModel.addRow(wh);
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"Stock", "New Warehouse "+wh.getName());
				} catch (Exception x) {
				BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}
			
		});
		
		JButton editButton = new JButton(com.floreantpos.POSConstants.EDIT);
		editButton.setPreferredSize(new Dimension(60, 40));
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doEditSelected();
			}
			
		});
		
		JButton deleteButton = new JButton(com.floreantpos.POSConstants.DELETE);
		deleteButton.setPreferredSize(new Dimension(60, 40));
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					if (ConfirmDeleteDialog.showMessage(WarehouseInventory.this, com.floreantpos.POSConstants.CONFIRM_DELETE, com.floreantpos.POSConstants.DELETE) == ConfirmDeleteDialog.YES) {
						InventoryWarehouse wh = (InventoryWarehouse) tableModel.getRow(index);
						InventoryWarehouseDAO.getInstance().delete(wh);
						tableModel.removeRow(index);
						ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
								"Stock", "Delete Warehouse "+wh.getName());
					}
				} catch (Exception x) {
				BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}
			
		});

		TransparentPanel panel = new TransparentPanel();
		panel.add(addButton);
		panel.add(editButton);
		panel.add(deleteButton);
		add(panel, BorderLayout.SOUTH);
	}
	
	
}
