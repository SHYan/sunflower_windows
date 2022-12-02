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
package com.floreantpos.model.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.model.InventoryWarehouse;

public class InventoryWarehouseDAO extends BaseInventoryWarehouseDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public InventoryWarehouseDAO () {}
	
	public boolean exists(String warehouseName) throws PosException {
		Session session = null;
		
		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(InventoryWarehouse.PROP_NAME, warehouseName));
			List list = criteria.list();
			if (list != null && list.size() > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new PosException(Messages.getString("WAREHOUSE_DUPLICATE_CHECK_ERROR"), e); //$NON-NLS-1$
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public void refresh(InventoryWarehouse warehouse) throws PosException {
		Session session = null;
		
		try {
			session = createNewSession();
			session.refresh(warehouse);
		} catch (Exception e) {
			throw new PosException(Messages.getString("WAREHOUSE_REFRESH_ERROR"), e); //$NON-NLS-1$
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
		}
	}


}