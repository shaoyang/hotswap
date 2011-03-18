package com.taobao.baoxian.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.taobao.baoxian.osgi.OsgiService;
import com.taobao.baoxian.osgi.OsgiServiceManager;
import com.taobao.baoxian.pojo.OsgiItem;
import com.taobao.baoxian.tfs.TfsManager;
import com.taobao.baoxian.utils.GET;

public class ItemController {
	
	private OsgiServiceManager osgiServiceManager;
	
	private TfsManager tfsManager;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {  
	 
		GET requestData = new GET(request);
		
		long itemId = requestData.getLong("item_id");
		
		OsgiItem osgiItem = tfsManager.getOsgiItemById((int)itemId);
		//获得Osgi实例
		OsgiService osgiService = (OsgiService) osgiServiceManager.getOsgiClass(osgiItem).newInstance();
		String str = osgiService.getRawString("estimate.vm");
		return null;
	}
}