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
package com.floreantpos.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.json.JSONObject;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.DatabaseConfigurationDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Shenfen;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.services.PosWebService;
import com.floreantpos.util.POSUtil;
import com.orocube.common.util.TerminalUtil;

public class AboutDialog extends POSDialog {

	public AboutDialog() {
		super(POSUtil.getBackOfficeWindow(), Messages.getString("AboutDialog.0")); //$NON-NLS-1$
		setIconImage(Application.getApplicationIcon().getImage());
	}
	
	private void openBrowser(String link) throws Exception {
		URI uri = new URI(link);
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(uri);
		}
	}
	
	private void openUpdateDialog() {
		Map<String, Object> map = new HashMap<String, Object>();
		Terminal terminal = TerminalDAO.getInstance().get(new Integer(TerminalConfig.getTerminalId()));
		map.put("uid", terminal.getTerminalKey());
		map.put("app_name", Application.APPNAME);
		map.put("app_version", Application.VERSION);
		Shenfen sf = (Shenfen) IBatisFactory.selectOneSQL("Shenfen.get_shenfen", map);
		if(sf!=null) {
			map.put("os", sf.getOs());
			map.put("os_version", sf.getOsVersion());
		}
		if(terminal.getLocation() != null) {
			String [] temp = terminal.getLocation().split(":");
			if(temp!=null && temp.length==3) {
				map.put("cid", temp[0]);
				map.put("bid", temp[1]);
				map.put("tid", temp[2]);
			}
		}
		map.put("request_type", "update_check");
		JSONObject jsonObj = null;
		PosWebService service = new PosWebService();
		String retJsonStr = service.updateCheck(map);
		boolean up_to_date = false;
		if(retJsonStr!=null && !retJsonStr.equals("")) {
			try {
				jsonObj = new JSONObject(retJsonStr);
				String status = (String) jsonObj.get("response_status");
				if(status.equals("success")){
					if(jsonObj.get("response_data") == null || jsonObj.get("response_data").toString().equals("")) {
						JOptionPane.showMessageDialog(null, jsonObj.get("response_msg"));
					}else {
						JSONObject responseData = new JSONObject(jsonObj.get("response_data").toString());
						Map<String, Object> sfMap = responseData.toMap();
						map.put("appVersion", sfMap.get("app_version"));
						map.put("downloadLink", sfMap.get("download_link"));
						sfMap.clear();
						up_to_date = true;	
					}
					
				}else {
					JOptionPane.showMessageDialog(null, jsonObj.get("error_code")+":"+jsonObj.get("response_msg"));
				}
			}catch(Exception e) {
				JOptionPane.showMessageDialog(null, retJsonStr);
				PosLog.error(getClass(), e);
			}
		}
		if(up_to_date) {
			int option = JOptionPane.showConfirmDialog(POSUtil.getBackOfficeWindow(), Messages.getString("UPDATE_AVA_DOWNLOAD")+map.get("appVersion"), "Update Windows", //$NON-NLS-1$
					JOptionPane.YES_NO_OPTION); //$NON-NLS-2$
			if (option == JOptionPane.YES_OPTION) {
				try {
					openBrowser(map.get("downloadLink").toString());
				} catch (Exception e1) {
					PosLog.error(getClass(), e1);
				}
			}
			
		}
		
	}

	@Override
	protected void initUI() {
		JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JLabel logoLabel = new JLabel(IconFactory.getIcon("/icons/", "fp_logo128x128.png")); //$NON-NLS-1$ //$NON-NLS-2$
		contentPanel.add(logoLabel, BorderLayout.WEST);
		String terminalKey = TerminalUtil.getSystemUID();
		Terminal terminal = TerminalDAO.getInstance().get(TerminalConfig.getTerminalId());
		JLabel l = new JLabel("<html><b>"+Application.APPNAME+"</b><br/>(Version " + Application.VERSION + ")<br/>"
				+ "Terminal Key : "+TerminalUtil.getSystemUID()+"<br/>"
				+ "Terminal : "+terminal.getLocation()+"<br/>"
				+ "License Expire : "+TerminalConfig.getExpTime()+"</html>"); 
		contentPanel.add(l);

		JPanel buttonPanel = new JPanel(new MigLayout("fill"));
		JButton updateBtn = new JButton(Messages.getString("UPDATE_CHECK")); //$NON-NLS-1$
		updateBtn.setPreferredSize(new Dimension(180, 40));
		updateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openUpdateDialog();
			}
		});
		
		JButton btnOk = new JButton(Messages.getString("AboutDialog.5")); //$NON-NLS-1$
		btnOk.setPreferredSize(new Dimension(120, 40));
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		/*
		JTextField tfTerminalKey = new JTextField();
		tfTerminalKey.setHorizontalAlignment(JTextField.CENTER);
		tfTerminalKey.setEditable(false);
		tfTerminalKey.setText(TerminalUtil.getSystemUID());
		tfTerminalKey.setBorder(null);
		tfTerminalKey.setFont(tfTerminalKey.getFont().deriveFont(Font.BOLD, 18));
		
		buttonPanel.add(new JSeparator(), "growx");
		//buttonPanel.add(new JLabel("Terminal Key"), "newline, split 2");
		buttonPanel.add(tfTerminalKey, "newline,growx");
		buttonPanel.add(new JSeparator(), "newline, growx");
		 */
		buttonPanel.add(updateBtn);
		buttonPanel.add(btnOk);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(contentPanel);
	}
}
