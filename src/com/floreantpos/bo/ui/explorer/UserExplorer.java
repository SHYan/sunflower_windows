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
package com.floreantpos.bo.ui.explorer;

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

import org.hibernate.exception.ConstraintViolationException;

import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.forms.UserForm;
import com.floreantpos.util.POSUtil;

public class UserExplorer extends TransparentPanel {

	private JTable table;
	private UserTableModel tableModel;

	public void doEditSelected() {
		try {
			int index = table.getSelectedRow();
			if (index < 0)
				return;

			User user = (User) tableModel.getRowData(index);
			UserForm editor = new UserForm();
			editor.setEditMode(true);
			editor.setBean(user);
			BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
			dialog.open();
			if (dialog.isCanceled())
				return;
			tableModel.updateItem(index);
			ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
					"BackOffice Setting", "Edit user "+user.getFullName());
		} catch (Throwable x) {
			BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
		}
	}
	public UserExplorer() {
		List<User> users = UserDAO.getInstance().findAll();
		tableModel = new UserTableModel(users);
		table = new JTable(tableModel);
		table.setRowHeight(POSConstants.TABLE_ROW_HEIGHT);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());
		
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
					Integer userWithMaxId = UserDAO.getInstance().findUserWithMaxId();

					UserForm editor = new UserForm();
					if (userWithMaxId != null) {
						editor.setId(new Integer(userWithMaxId.intValue() + 1));
					}
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;
					User user = (User) editor.getBean();
					if(user==null) return;
					tableModel.addItem(user);
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"BackOffice Setting", "Add new user "+user.getFullName());
				} catch (Exception x) {
					PosLog.error(getClass(), x);
					BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});
		JButton copyButton = new JButton(com.floreantpos.POSConstants.COPY);
		copyButton.setPreferredSize(new Dimension(60, 40));
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					User user = (User) tableModel.getRowData(index);

					User user2 = new User();
					user2.setUserId(user.getUserId());
					user2.setType(user.getType());
					user2.setFirstName(user.getFirstName());
					user2.setLastName(user.getLastName());
					user2.setPassword(user.getPassword());
					user2.setSsn(user.getSsn());

					UserForm editor = new UserForm();
					editor.setEditMode(false);
					editor.setBean(user2);
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;

					User newUser = (User) editor.getBean();
					tableModel.addItem(newUser);
					ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
							"BackOffice Setting", "Add new user "+user.getFullName());
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
				int index = table.getSelectedRow();
				if (index < 0)
					return;

				User user = (User) tableModel.getRowData(index);
				if (user == null) {
					return;
				}

				try {
					if (ConfirmDeleteDialog.showMessage(POSUtil.getBackOfficeWindow(), com.floreantpos.POSConstants.CONFIRM_DELETE,
							com.floreantpos.POSConstants.DELETE) == ConfirmDeleteDialog.YES) {
						UserDAO.getInstance().delete(user);
						tableModel.deleteItem(index);
						ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
								"BackOffice Setting", "Delete user "+user.getFullName());
					}
				} catch (ConstraintViolationException x) {
					String message = com.floreantpos.POSConstants.USER
							+ " " + user.getFirstName() + " " + user.getLastName() + " (" + user.getType() + ") " + com.floreantpos.POSConstants.ERROR_MESSAGE; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					BOMessageDialog.showError(message, x);
				} catch (Exception x) {
					BOMessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		TransparentPanel panel = new TransparentPanel();
		panel.add(addButton);
		panel.add(copyButton);
		panel.add(editButton);
		panel.add(deleteButton);
		add(panel, BorderLayout.SOUTH);
	}

	class UserTableModel extends ListTableModel {

		UserTableModel(List list) {
			super(new String[] { com.floreantpos.POSConstants.ID, com.floreantpos.POSConstants.FIRST_NAME, com.floreantpos.POSConstants.LAST_NAME,
					com.floreantpos.POSConstants.TYPE }, list);
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			User user = (User) rows.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return String.valueOf(user.getUserId());

				case 1:
					return user.getFirstName();

				case 2:
					return user.getLastName();

				case 3:
					return user.getType();
			}
			return null;
		}
	}
}
