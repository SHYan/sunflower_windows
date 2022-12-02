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
package com.floreantpos.bo.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.ModifierGroup;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Tax;
import com.floreantpos.model.TaxGroup;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.MenuItemModifierGroupDAO;
import com.floreantpos.model.dao.MenuModifierDAO;
import com.floreantpos.model.dao.ModifierGroupDAO;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.model.dao.UserPermissionDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.datamigrate.Elements;

public class DataImportAction {

	public DataImportAction() {
		init(); //$NON-NLS-1$
	}

	public void init() {
		JFileChooser fileChooser = DataExportAction.getFileChooser();
		int option = fileChooser.showOpenDialog(com.floreantpos.util.POSUtil.getBackOfficeWindow());
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fileChooser.getSelectedFile();
		try {

			if(importMenuItemsFromFile(file)) {
				POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("DataImportAction.1")); //$NON-NLS-1$
				ActionHistoryDAO.getInstance().saveHistory(
						Application.getCurrentUser(),
						Messages.getString("DataImportAction.0"),
						"Import Menu Success");
			}
			
			

		} catch (Exception e1) {
			PosLog.error(getClass(), e1);
			POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), e1.getMessage());
			ActionHistoryDAO.getInstance().saveHistory(
					Application.getCurrentUser(),
					Messages.getString("DataImportAction.0"),
					"Import Menu Fail");
		}

	}

	public static boolean importMenuItemsFromFile(File file) throws Exception {
		if (file == null) {
			POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("SelectFileError"));
			return false;
		}

		FileInputStream inputStream = new FileInputStream(file);
		return importMenuItems(inputStream);
	}

	public static boolean importMenuItems(InputStream inputStream) throws Exception {
		Map<String, Object> objectMap = new HashMap<String, Object>();

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Elements.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			//unmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//unmarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			//unmarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			Elements elements = (Elements) unmarshaller.unmarshal(inputStream);

			List<Tax> taxes = elements.getTaxes();
			if (taxes != null) {
				for (Tax tax : taxes) {
					TaxDAO.getInstance().saveOrUpdate(tax);
				}
			}
			
			List<Restaurant> restaurant = elements.getRestaurants();
			if (restaurant != null) {
				for (Restaurant res : restaurant) {
					RestaurantDAO.getInstance().saveOrUpdate(res);
				}
			}
			
			List<User> users = elements.getUsers();
			if (users != null) {
				for (User user : users) {
					UserDAO.getInstance().saveOrUpdate(user);
				}
			}
			
			List<UserType> userTypes = elements.getUserTypes();
			if (userTypes != null) {
				for (UserType userType : userTypes) {
					UserTypeDAO.getInstance().saveOrUpdate(userType);
				}
			}
			
			List<UserPermission> userPs = elements.getUserPermissions();
			if (userPs != null) {
				for (UserPermission us : userPs) {
					UserPermissionDAO.getInstance().saveOrUpdate(us);
				}
			}

			List<MenuCategory> menuCategories = elements.getMenuCategories();
			if (menuCategories != null) {
				for (MenuCategory menuCategory : menuCategories) {

					String uniqueId = menuCategory.getUniqueId();
					objectMap.put(uniqueId, menuCategory);
					menuCategory.setId(null);

					MenuCategoryDAO.getInstance().save(menuCategory);
				}
			}

			List<MenuGroup> menuGroups = elements.getMenuGroups();
			if (menuGroups != null) {
				for (MenuGroup menuGroup : menuGroups) {

					MenuCategory menuCategory = menuGroup.getParent();
					if (menuCategory != null) {
						menuCategory = (MenuCategory) objectMap.get(menuCategory.getUniqueId());
						menuGroup.setParent(menuCategory);
					}

					objectMap.put(menuGroup.getUniqueId(), menuGroup);
					menuGroup.setId(null);

					MenuGroupDAO.getInstance().saveOrUpdate(menuGroup);
				}
			}

			List<ModifierGroup> menuModifierGroups = elements.getModifierGroups();
			if (menuModifierGroups != null) {
				for (ModifierGroup menuModifierGroup : menuModifierGroups) {
					objectMap.put(menuModifierGroup.getUniqueId(), menuModifierGroup);
					menuModifierGroup.setId(null);

					ModifierGroupDAO.getInstance().saveOrUpdate(menuModifierGroup);
				}
			}

			List<MenuModifier> menuModifiers = elements.getMenuModifiers();
			if (menuModifiers != null) {
				for (MenuModifier menuModifier : menuModifiers) {

					objectMap.put(menuModifier.getUniqueId(), menuModifier);
					menuModifier.setId(null);

					ModifierGroup menuModifierGroup = menuModifier.getModifierGroup();
					if (menuModifierGroup != null) {
						menuModifierGroup = (ModifierGroup) objectMap.get(menuModifierGroup.getUniqueId());
						menuModifier.setModifierGroup(menuModifierGroup);
					}

					Tax tax = menuModifier.getTax();
					if (tax != null) {
						tax = (Tax) objectMap.get(tax.getUniqueId());
						menuModifier.setTax(tax);
					}

					MenuModifierDAO.getInstance().saveOrUpdate(menuModifier);
				}
			}

			List<MenuItemModifierGroup> menuItemModifierGroups = elements.getMenuItemModifierGroups();
			if (menuItemModifierGroups != null) {
				for (MenuItemModifierGroup mimg : menuItemModifierGroups) {
					objectMap.put(mimg.getUniqueId(), mimg);
					mimg.setId(null);

					ModifierGroup menuModifierGroup = mimg.getModifierGroup();
					if (menuModifierGroup != null) {
						menuModifierGroup = (ModifierGroup) objectMap.get(menuModifierGroup.getUniqueId());
						mimg.setModifierGroup(menuModifierGroup);
					}

					MenuItemModifierGroupDAO.getInstance().save(mimg);
				}
			}

			List<MenuItem> menuItems = elements.getMenuItems();
			if (menuItems != null) {
				for (MenuItem menuItem : menuItems) {

					objectMap.put(menuItem.getUniqueId(), menuItem);
					menuItem.setId(null);

					MenuGroup menuGroup = menuItem.getParent();
					if (menuGroup != null) {
						menuGroup = (MenuGroup) objectMap.get(menuGroup.getUniqueId());
						menuItem.setParent(menuGroup);
					}

					TaxGroup taxGroup = menuItem.getTaxGroup();
					if (taxGroup != null) {
						taxGroup = (TaxGroup) objectMap.get(taxGroup.getId());
						menuItem.setTaxGroup(taxGroup);
					}

					List<MenuItemModifierGroup> menuItemModiferGroups = menuItem.getMenuItemModiferGroups();
					if (menuItemModiferGroups != null) {
						for (MenuItemModifierGroup menuItemModifierGroup : menuItemModiferGroups) {
							MenuItemModifierGroup menuItemModifierGroup2 = (MenuItemModifierGroup) objectMap.get(menuItemModifierGroup.getUniqueId());
							menuItemModifierGroup.setId(menuItemModifierGroup2.getId());
							menuItemModifierGroup.setModifierGroup(menuItemModifierGroup2.getModifierGroup());
						}
					}

					MenuItemDAO.getInstance().saveOrUpdate(menuItem);
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return true;
	}
}
