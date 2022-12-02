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
package com.floreantpos.mobi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.ShopTableStatus;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.MenuModifierDAO;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.ui.views.order.OrderController;

public class MobiSchedule {
	static Log logger = LogFactory.getLog(MobiSchedule.class);
	
	/*============mobi=================*/
	public static int refreshDuration = 5000;
	public static String dirPath = "C:\\mobi_tmp\\", LASTFILE = "";
	public static int orderType = 1, userId = 999;
	public static boolean runningFlag = false;
	public static Timer clockTimer;
	
	public static void parseJson(File jsonFile) {
		if(jsonFile==null) return;
		Application application = Application.getInstance();
    	String modifierStr;
    	
		InputStream is;
		try {
			is = new FileInputStream(jsonFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
        try {
            String jsonStr = IOUtils.toString(is, "UTF-8");
            JSONObject item, jsonObj = new JSONObject(jsonStr);//(JSONObject) parser.parse(new FileReader(jsonFile));
        	jsonObj = jsonObj.getJSONObject("value");

        	Ticket ticket = null;
        	Integer oldTicket = null;
        	boolean oldTicketFlag = false;
        	if(jsonObj.has("ticketId") && jsonObj.get("ticketId")!=null)
        		oldTicket = Integer.valueOf(jsonObj.get("ticketId").toString());
        	
        	ShopTable selectedTables = null;
        	
        	if(oldTicket!=null && oldTicket!=0) {
        		ticket = TicketDAO.getInstance().get(oldTicket);
        		if(ticket.isClosed()) 
        			ticket = null;
        		else oldTicketFlag = true;
        	} 
        	if(!oldTicketFlag) {
        		
        		ticket = new Ticket();
        		ticket.setId(ticket.generateNewTicketId(null));
            	ticket.setPriceIncludesTax(application.isPriceIncludesTax());
        		OrderTypeDAO od = new OrderTypeDAO();
        		ticket.setOrderType(od.get(orderType));
        		ticket.setTicketType("DINE IN");
        		Calendar currentTime = Calendar.getInstance();
        		ticket.setCreateDate(currentTime.getTime());
        		ticket.setCreationHour(currentTime.get(Calendar.HOUR_OF_DAY));
        		ticket.setTerminal(application.getTerminal());
        		ticket.setShift(application.getCurrentShift());
        		if(Application.getCurrentUser()!=null) {
	        		ticket.setOwner(Application.getCurrentUser());
	        		ticket.setOwnerName(Application.getCurrentUser().getFullName());
        		}else {
        			User user = UserDAO.getInstance().findUser(userId);
        			ticket.setOwner(user);
	        		ticket.setOwnerName(user.getFullName());
        		}
        		ticket.setNumberOfGuests((Integer)jsonObj.get("no_of_customers"));
        		
        		selectedTables = ShopTableDAO.getInstance().getByNumber(Integer.parseInt(jsonObj.get("table_no").toString()));
        		selectedTables.setServing(true);
        		ticket.addTable(selectedTables.getTableNumber());
        		ticket.addTableName(selectedTables.getDescOrNum());
        		
        			
        	}
    		
            JSONArray itemArray = jsonObj.getJSONArray("Items");
            MenuItem menuItem;
            MenuModifier menuModifier;
            
            TicketItem ticketItem;
            List<TicketItem> ticketItemList = new ArrayList<TicketItem>();
            if(oldTicketFlag) {
            	ticketItemList.addAll(ticket.getTicketItems());
            }
    		
            if(itemArray==null) return;
            for(int i=0; i<itemArray.length(); i++) {
            	item = itemArray.getJSONObject(i);
            	
            	menuItem = MenuItemDAO.getInstance().get(Integer.parseInt(item.get("Product_No").toString()));
        		MenuItemDAO dao = new MenuItemDAO();
        		menuItem = dao.initialize(menuItem);
            	//logger.debug(item.get("Qty").toString()+" is Qty");
        		double itemQty = Double.parseDouble(item.get("Qty").toString());
        		ticketItem = menuItem.convertToTicketItem(ticket.getOrderType(), itemQty);
        		//logger.debug("Ticket item qty is : "+ticketItem.getItemQuantity());
        		//ticketItem.setItemQuantity(Double.parseDouble(item.get("Qty").toString()));
        		modifierStr = item.getString("Condiment");
            	if(modifierStr!=null && !modifierStr.equals("")) {
            		String[] modifierObjList = modifierStr.split(",", -1);
            		for(int j=0; j<modifierObjList.length; j++) {
            			String[] modifierObj = modifierObjList[j].split("\\|", -1);
            			menuModifier = MenuModifierDAO.getInstance().get(Integer.parseInt(modifierObj[0]));
                		//ticketItem.addTicketItemModifier(menuModifier, TicketItemModifier.NORMAL_MODIFIER, ticket.getOrderType(), null, (int) itemQty * (int) Double.parseDouble(modifierObj[3]));
            			ticketItem.addTicketItemModifier(menuModifier, TicketItemModifier.NORMAL_MODIFIER, ticket.getOrderType(), null);
                		ticketItem.setHasModifiers(true);
            		}
            	}
            	ticketItem.setTicket(ticket);
        		ticketItem.calculatePrice();
            	
        		ticketItemList.add(ticketItem);
        		
            }
            
            String waiter = jsonObj.getString("waiter");
            ticket.setExtraDeliveryInfo(waiter);
            
            ticket.setTicketItems(ticketItemList);
            ticket.calculatePrice();
    		
    		TicketDAO.getInstance().saveOrUpdate(ticket);
    		
    		if(!oldTicketFlag) {
	    		ShopTableStatus shopTableStatus = selectedTables.getShopTableStatus();
	    		shopTableStatus.setTableTicket(ticket.getId(), ticket.getOwner().getId(), ticket.getOwnerName());
	    		selectedTables.setShopTableStatus(shopTableStatus);
	    		ShopTableDAO.getInstance().occupyTables(ticket);
	    		
	    		ActionHistoryDAO actionHistoryDAO = ActionHistoryDAO.getInstance();
	    		User user = Application.getCurrentUser();
	    		actionHistoryDAO.saveHistory(user, ActionHistory.NEW_CHECK, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL  + ticket.getId()); //$NON-NLS-1$
	    		
    		}

    		
    		
    		if (ticket.getOrderType().isShouldPrintToKitchen()) {
    			if (ticket.needsKitchenPrint()) {
    				ReceiptPrintService.printToKitchen(ticket);
    				TicketDAO.getInstance().refresh(ticket);
    			}
    		}
    		
    		ticket.setExtraDeliveryInfo(null);
    		OrderController.saveOrder(ticket);
    		
        }catch(IOException e) {
        	logger.debug(e.getMessage());
        }finally {
        	try {
				is.close();
			} catch (IOException e) {
				logger.debug(e.getMessage());
			}	
        }
       
	}
	
	public static void checkMobiUpload() {
		if(runningFlag) return; 
		runningFlag = true;
		
		try {
			File dir = new File(dirPath);
			File[] files = dir.listFiles(new FilenameFilter(){
			    public boolean accept(File dir, String name){
			        return name.toLowerCase().endsWith(".json");
			    }
			});
			if(files==null) return;
			String newFileName;
			for(File file : files){
			    if(file.isFile()){
			    	if(LASTFILE.equals(file.getName())) continue;
			    	LASTFILE = file.getName();
			    	newFileName = file.getAbsoluteFile()+".tmp";
			    	if(file.renameTo(new File(newFileName))) {
			    		file = new File(newFileName);
				        parseJson(file);
				        if(!file.delete()) 
			        		file.renameTo(new File(file.getAbsoluteFile()+".bp"));
				        if(file.exists()) 
				        	file.renameTo(new File(file.getAbsoluteFile()+".bp"));
			    	}
			    }
			}
		}catch(Exception e) {
			runningFlag = false;
			logger.debug(e.getMessage());
		}
		runningFlag = false;
	}

	public static void stopMobiTimer() {
		if(clockTimer!=null && clockTimer.isRunning()) clockTimer.stop();
	}
	
	public static void startMobiTimer() {
		
		if(clockTimer==null) 
			clockTimer = new Timer(refreshDuration, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					checkMobiUpload();
				}
			});
		if(!clockTimer.isRunning())
			clockTimer.start();

	}

}
