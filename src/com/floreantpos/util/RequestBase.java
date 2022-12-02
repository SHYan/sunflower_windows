package com.floreantpos.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class RequestBase  extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5712174894424918062L;

	private static final Logger logger = LoggerFactory.getLogger(RequestBase.class);
	
	HttpServletRequest req;
	Map<String, Object> outMap = new HashMap<String, Object>();
	
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	logger.debug("MenuAPI : ENTER processRequest()");
    	
    	req.setCharacterEncoding("UTF-8");
    	
    	this.req = req;
    	this.outMap.put("status", "no");
    	this.outMap.put("response_msg", "Some Errors!");
    	if(!paramCheck()) {
			this.outMap.put("response_msg", "Parameters are not complete!");
	    	this.outMap.put("response_data", "");
			outputJSON(resp);
			return;
		}
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		paramMap.put("jsp_schema", req.getParameter("req_schema").toString());
		paramMap.put("branchId", req.getParameter("req_branch").toString());
		paramMap.put("terminalNo", req.getParameter("req_terminal").toString());
		int currentTime = (int)(System.currentTimeMillis()/1000);
		
		if(req.getParameter("req_type").toString().equals("init_product")){
			this.outMap.put("status", "ok");
			this.outMap.put("response_msg", "Product update Finish!");
		}else if(req.getParameter("req_type").toString().equals("init_category")){
			this.outMap.put("status", "ok");
			this.outMap.put("response_msg", "Category update finish!");
		}
		outputJSON(resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	logger.debug("MenuAPI : ENTER doPost()");
    	processRequest(req, resp);
    }
    
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                        throws ServletException, IOException {
		logger.debug("MenuAPI : ENTER doGet()");
		processRequest(req, resp);
    }
	
	protected void outputJSON(HttpServletResponse resp){
		logger.debug("MenuAPI : ENTER outputJSON()");
		try {
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter out = resp.getWriter();
			Gson gson = new Gson();
			out.print(gson.toJson(this.outMap));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void outputAlertMsg(HttpServletResponse resp, String msg, boolean back){
		logger.debug("AddMenuAPI : ENTER outputJSON()");
		
		try {
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter out = resp.getWriter();
			out.println("<script type=\"text/javascript\">");  
			if(back)
				out.println("alert('"+msg+"');location.href = document.referrer;");
			else
				out.println("alert('"+msg+"');");
			out.println("</script>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean paramCheck(){
		logger.debug("AddMenuAPI : ENTER paramCheck()");
		if(req==null || Strings.isNullEmpty(req.getParameter("req_type")) || Strings.isNullEmpty(req.getParameter("req_schema"))){
			return false;
		}
		
		if(Strings.isNullEmpty(req.getParameter("product")) && Strings.isNullEmpty(req.getParameter("category")) && Strings.isNullEmpty(req.getParameter("condiment"))){
			logger.debug("menu lact parameter");
			return false;	
		}
			
		return true;
	}
	
	protected String getIP(){
		String ip = req.getHeader("X-FORWARDED-FOR");
		if(Strings.isNotNullEmpty(ip)) {
			int idx = ip.indexOf(',');
            if (idx > -1) 
                return ip.substring(0, idx);
		}
		return req.getRemoteAddr();
	}
}