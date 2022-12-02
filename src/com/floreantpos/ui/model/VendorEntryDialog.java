package com.floreantpos.ui.model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import net.miginfocom.swing.MigLayout;

import com.floreantpos.Messages;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.util.ZipCodeUtil;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;

public class VendorEntryDialog extends BeanEditor{

	private JPanel contentPane;
	private JTextField tfVendor;
	
	//private FixedLengthTextField tfName;
	private JTextArea tfAddress;
	private FixedLengthTextField tfCity;
	private JTextField tfState;
	private FixedLengthTextField tfZip;
	private JTextField tfCountry;
	private JTextField tfEmail;
	private JTextField tfPhone;
	private JTextField tfFax;
	private QwertyKeyPad qwertyKeyPad;
	public boolean isKeypad;
	
	private InventoryVendor ivendor;
	
	public VendorEntryDialog() throws Exception {
		this(new InventoryVendor());
	}
	
	public VendorEntryDialog(InventoryVendor vendor) throws Exception {
		createVendorForm();

		setBean(vendor);
	}

	
	/*public VendorEntryDialog(boolean enable) {
		isKeypad = enable;
		createVendorForm();
	}*/
	
	private void createVendorForm() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setOpaque(true);
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new MigLayout("insets 10 10 10 10", "[][][][]", "[][][][][]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		inputPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ENTER_VENDOR_INFO")));

		JLabel lblAddress = new JLabel(Messages.getString("CustomerForm.18")); //$NON-NLS-1$
		tfAddress = new JTextArea(new FixedLengthDocument(220));
		JScrollPane scrlDescription = new JScrollPane(tfAddress);
		scrlDescription.setPreferredSize(PosUIManager.getSize(338, 52));

		JLabel lblZip = new JLabel(Messages.getString("CustomerForm.12")+" 2"); //$NON-NLS-1$
		tfZip = new FixedLengthTextField(30);
		

		JLabel lblCitytown = new JLabel(Messages.getString("VENDOR_TYPE")); //$NON-NLS-1$
		tfCity = new FixedLengthTextField();

		JLabel lblState = new JLabel(Messages.getString("OUTSTANDING")); //$NON-NLS-1$
		tfState = new JTextField(30);
		
		JLabel lblCountry = new JLabel(Messages.getString("LAST_UPDATE")); //$NON-NLS-1$
		tfCountry = new FixedLengthTextField(30);

		JLabel lblName = new JLabel(Messages.getString("PosMessage.179")); //$NON-NLS-1$
		inputPanel.add(lblName); //$NON-NLS-1$
		tfVendor = new FixedLengthTextField();
		tfVendor.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfVendor); //$NON-NLS-1$
		
		inputPanel.add(lblCitytown); //$NON-NLS-1$
		tfCity.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfCity, "wrap"); //$NON-NLS-1$
		
		
		
		JLabel lblCellPhone = new JLabel(Messages.getString("CustomerForm.32")); //$NON-NLS-1$
		inputPanel.add(lblCellPhone); //$NON-NLS-1$
		tfPhone = new JTextField(30);
		tfPhone.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfPhone); 
		
		inputPanel.add(lblState); //$NON-NLS-1$
		tfState.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfState, "wrap"); //$NON-NLS-1$
		
		
		
		inputPanel.add(lblZip); //$NON-NLS-1$
		tfZip.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfZip); //$NON-NLS-1$
		
		inputPanel.add(lblCountry); //$NON-NLS-1$
		tfCountry.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfCountry, "wrap"); //$NON-NLS-1$
		
		
		JLabel lblFax = new JLabel(Messages.getString("VIBER")); //$NON-NLS-1$
		tfFax = new FixedLengthTextField(30);
		inputPanel.add(lblFax); //$NON-NLS-1$
		tfFax.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfFax); //$NON-NLS-1$

		inputPanel.add(lblAddress); //$NON-NLS-1$
		inputPanel.add(scrlDescription, "wrap"); //$NON-NLS-1$
		
		JLabel lblEmail = new JLabel(Messages.getString("CustomerForm.15")); //$NON-NLS-1$
		tfEmail = new FixedLengthTextField(30);
		inputPanel.add(lblEmail); //$NON-NLS-1$
		tfEmail.setPreferredSize(new Dimension(120, 30));
		inputPanel.add(tfEmail, "wrap"); //$NON-NLS-1$
		
		

		
		

		

		
		

		qwertyKeyPad = new QwertyKeyPad();

		add(inputPanel, BorderLayout.CENTER);

		if (isKeypad) {
			add(qwertyKeyPad, BorderLayout.SOUTH); //$NON-NLS-1$
		}

		tfZip.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getStateAndCityByZipCode();
			}
		});

		tfZip.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				getStateAndCityByZipCode();
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		//enableVendorFields(true);
	}
	

	public void enableVendorFields(boolean enable) {
		tfVendor.setEnabled(enable);
		tfAddress.setEnabled(enable);
		tfCity.setEnabled(enable);
		tfState.setEditable(enable);
		tfZip.setEnabled(enable);
		tfCountry.setEditable(enable);
		tfEmail.setEditable(enable);
		tfPhone.setEnabled(enable);
		tfFax.setEditable(enable);
	}

	public void updateView() {
		InventoryVendor vendor = (InventoryVendor) getBean();

		if (vendor == null) {
			tfVendor.setText(""); //$NON-NLS-1$
			tfAddress.setText("");
			tfCity.setText("Beverage Supplier");
			tfState.setText("0");
			tfZip.setText("");
			tfCountry.setText("");
			tfEmail.setText("");
			tfPhone.setText("");
			tfFax.setText("");
			return;
		}
		tfVendor.setText(vendor.getName());
		tfAddress.setText(vendor.getAddress());
		tfCity.setText(vendor.getCity());
		tfState.setText(vendor.getState());
		tfZip.setText(vendor.getZip());
		tfCountry.setText(vendor.getCountry());
		tfEmail.setText(vendor.getEmail());
		tfPhone.setText(vendor.getPhone());
		tfFax.setText(vendor.getFax());
	}

	public boolean updateModel() {
		
		InventoryVendor vendor = (InventoryVendor) getBean();
		if (vendor == null) {
			return false;
		}

		String vendorName = tfVendor.getText();
		if (POSUtil.isBlankOrNull(vendorName)) {
			MessageDialog.showError("Please Fill Vendor Name"); //$NON-NLS-1$
			return false;
		}

		vendor.setName(vendorName);
		vendor.setAddress(tfAddress.getText());
		vendor.setCity(tfCity.getText());
		vendor.setState(tfState.getText());
		vendor.setZip(tfZip.getText());
		vendor.setCountry(tfCountry.getText());
		vendor.setEmail(tfEmail.getText());
		vendor.setPhone(tfPhone.getText());
		vendor.setFax(tfFax.getText());
		return true;
	}

	public InventoryVendor getInventoryVendor() {
		return ivendor;
	}

	public void setInventoryVendor(InventoryVendor vendor) {
		this.ivendor = vendor;

		updateView();
	}

	private void getStateAndCityByZipCode() {

		String zipCode = tfZip.getText();

		if (zipCode == null || zipCode.isEmpty()) {
			tfState.setText(""); //$NON-NLS-1$
			tfCity.setText(""); //$NON-NLS-1$
			return;
		}

		String city = ZipCodeUtil.getCity(zipCode);
		String state = ZipCodeUtil.getState(zipCode);

		tfState.setText(state);
		tfCity.setText(city);
	}

	
	@Override
	public String getDisplayText() {
		InventoryVendor vendor = (InventoryVendor) getBean();
		if (vendor.getId() == null) {
			return Messages.getString("NEW_VENDOR"); 
		}
		return Messages.getString("EDIT_VENDOR"); 
	}

	@Override
	public boolean save() {
		try {

			if (!updateModel())
				return false;

			InventoryVendor vendor = (InventoryVendor) getBean();
			InventoryVendorDAO.getInstance().saveOrUpdate(vendor);

			return true;

		} catch (Exception x) {
			MessageDialog.showError(x);
			return false;
		}
		
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return contentPane;
	}
}
