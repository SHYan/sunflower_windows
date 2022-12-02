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
package com.floreantpos.config.ui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.Messages;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.util.POSUtil;

public class TaxConfigurationView extends ConfigurationView {
	public static final String CONFIG_TAB_TAX = Messages.getString("TaxConfigurationView.00"); //$NON-NLS-1$
	private Restaurant restaurant;
	private JCheckBox cbItemSalesPriceIncludesTax;

	/* J1.1 22 */
	private JCheckBox pc_no_sc = new JCheckBox("Parcel No SC");
	private JCheckBox pc_no_tax = new JCheckBox("Parcel No Tax");
	private JCheckBox sc_include_dis = new JCheckBox("*SC Include Discount");
	private JCheckBox tax_include_sc = new JCheckBox("*Tax Include SC");
	
	private IntegerTextField tfQuantityLength = new IntegerTextField(10);
	private IntegerTextField tfPriceLength = new IntegerTextField(10);
	
	public TaxConfigurationView() {
		setLayout(new BorderLayout());

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[]", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		cbItemSalesPriceIncludesTax = new JCheckBox(Messages.getString("TaxConfigurationView.4")); //$NON-NLS-1$
		contentPanel.add(cbItemSalesPriceIncludesTax, "cell 0 0"); //$NON-NLS-1$
		
		/* J1.1 22 */
		contentPanel.add(sc_include_dis, "newline"); //$NON-NLS-1$
		contentPanel.add(tax_include_sc, "wrap"); //$NON-NLS-1$
		contentPanel.add(pc_no_sc, "newline"); //$NON-NLS-1$
		contentPanel.add(pc_no_tax, "wrap"); //$NON-NLS-1$
		
		contentPanel.add(new JLabel(Messages.getString("TaxConfigurationView.Qty"))); //$NON-NLS-1$
		
		contentPanel.add(tfQuantityLength, "wrap"); //$NON-NLS-1$
		contentPanel.add(new JLabel(Messages.getString("TaxConfigurationView.Price"))); //$NON-NLS-1$
		contentPanel.add(tfPriceLength, "wrap"); //$NON-NLS-1$

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(null);
		add(scrollPane);
	}

	@Override
	public boolean save() throws Exception {
		if (!isInitialized()) {
			return true;
		}

		restaurant.setItemPriceIncludesTax(cbItemSalesPriceIncludesTax.isSelected());

		RestaurantDAO.getInstance().saveOrUpdate(restaurant);

		Application.getInstance().refreshRestaurant();

		/*J1.1 22*/
		TerminalConfig.setScIncludeDis(sc_include_dis.isSelected());
		TerminalConfig.setTaxIncludeSc(tax_include_sc.isSelected());
		TerminalConfig.setPcNoSc(pc_no_sc.isSelected());
		TerminalConfig.setPcNoTax(pc_no_tax.isSelected());
		
		TerminalConfig.setQtyDecimal(tfQuantityLength.getInteger());
		TerminalConfig.setPriceDecimal(tfPriceLength.getInteger());
		
		return true;
	}

	@Override
	public void initialize() throws Exception {
		restaurant = RestaurantDAO.getInstance().get(Integer.valueOf(1));
		cbItemSalesPriceIncludesTax.setSelected(POSUtil.getBoolean(restaurant.isItemPriceIncludesTax()));

		/* J1.1 22 */
		sc_include_dis.setSelected(TerminalConfig.isScIncludeDis());
		tax_include_sc.setSelected(TerminalConfig.isTaxIncludeSc());
		pc_no_sc.setSelected(TerminalConfig.isPcNoSc());
		pc_no_tax.setSelected(TerminalConfig.isPcNoTax());
		
		tfQuantityLength.setText(String.valueOf(TerminalConfig.getQtyDecimal()));
		tfPriceLength.setText(String.valueOf(TerminalConfig.getPriceDecimal()));
		setInitialized(true);
	}

	@Override
	public String getName() {
		return CONFIG_TAB_TAX;
	}

}
