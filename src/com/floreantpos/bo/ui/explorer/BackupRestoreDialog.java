package com.floreantpos.bo.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.Messages;
import com.floreantpos.bo.actions.DataExportAction;
import com.floreantpos.bo.actions.DataImportAction;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;

import net.miginfocom.swing.MigLayout;

public class BackupRestoreDialog extends POSDialog {
	
	public BackupRestoreDialog() {
		super(POSUtil.getBackOfficeWindow(), "");
		init();
	}
	public BackupRestoreDialog(JFrame parent) {
		super();

		init();
	}
		
	private void init() {
		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("BackupRestoreAction.0")); //$NON-NLS-1$
		add(titlePanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new MigLayout("fillx,aligny center", "[][][][]", ""));

		JButton backupButton = new JButton(Messages.getString("BackupRestoreAction.1"));
		backupButton.setPreferredSize(new Dimension(120, 60));
		backupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					com.floreantpos.util.JBackupController backup = new com.floreantpos.util.JBackupController();
					if(backup.backupDatabase()) {
						POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("BackupRestoreAction.1")+" "+Messages.getString("SUCCESS")); //$NON-NLS-1$
						ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
								"BackOffice Setting", "Backup Database Success");
					}
					else
						POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("BackupRestoreAction.1")+" "+Messages.getString("FAIL")); //$NON-NLS-1$
				} catch (Exception x) {
					
				}
			}

		});
		
		JButton restoreButton = new JButton(Messages.getString("BackupRestoreAction.2"));
		restoreButton.setPreferredSize(new Dimension(120, 40));
		restoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String restoreDatabase = JOptionPane.showInputDialog(Messages.getString("BackupRestoreAction.6"));
					if (StringUtils.isEmpty(restoreDatabase)) {
						POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("BackupRestoreAction.1")+" "+Messages.getString("FAIL")); //$NON-NLS-1$
						return;
					}
	                com.floreantpos.util.JBackupController controller = new com.floreantpos.util.JBackupController();
					if(controller.restoreDatabase(restoreDatabase)) {
						POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("BackupRestoreAction.1")+" "+Messages.getString("SUCCESS")); //$NON-NLS-1$
						ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), 
								"BackOffice Setting", "Restore Database Success");
					} else
						POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("BackupRestoreAction.1")+" "+Messages.getString("FAIL")); //$NON-NLS-1$
				} catch (Exception x) {
					
				}
			}
		});

		JButton backupButton1 = new JButton(Messages.getString("BackupRestoreAction.3"));
		backupButton1.setPreferredSize(new Dimension(120, 60));
		backupButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DataExportAction();
			}
		});
		JButton restoreButton1 = new JButton(Messages.getString("BackupRestoreAction.4"));
		restoreButton1.setPreferredSize(new Dimension(120, 40));
		restoreButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DataImportAction();
			}
		});
		
		centerPanel.add(backupButton);
		centerPanel.add(restoreButton);
		centerPanel.add(backupButton1);
		centerPanel.add(restoreButton1);
		
		add(centerPanel, BorderLayout.CENTER);

	}
	
	

}
