package com.taobao.test.osgi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import com.taobao.baoxian.osgi.OsgiService;
import com.taobao.baoxian.osgi.check.CheckItem;
import com.taobao.baoxian.osgi.check.CheckParser;
import com.taobao.baoxian.osgi.check.exception.CheckResourceNotValidException;
import com.taobao.baoxian.osgi.loader.URLOsgiLoader;
import com.taobao.baoxian.pojo.OsgiItem;
import com.taobao.baoxian.tfs.TfsManager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring-dao.xml","classpath:WEB-INF/spring-service.xml",
		"classpath:WEB-INF/spring-ibatis.xml","classpath:WEB-INF/spring-conf.xml"})
public class TestURLOsgiLoader{
	
	@Resource
	private TfsManager tfsManager;	

	URLOsgiLoader osgiLoader = new URLOsgiLoader();
	
	//@Test
	public void testLoad(){
	
		URLOsgiLoader osgiLoader = new URLOsgiLoader();
		OsgiItem osgiItem = null;
		Class<?> clazz = null;
		try {
			int itemId = 100;
			
			osgiItem = (OsgiItem)tfsManager.getOsgiItemById(itemId);
			
			assert(osgiItem != null);
			//加在OsgiService
			if(osgiItem != null){
				clazz = osgiLoader.load(osgiItem);
			}
			assert(clazz != null);
			//使用反射加载方法
			String methodName = "testLoad";
			
			/*
 			Method[] methods = clazz.getMethods();
			for(Method m:methods)
				System.out.println(m.toString());
			
			Object instance = clazz.newInstance();
			Method testLoad = clazz.getMethod(methodName,new Class[]{});
			//Class<?> retClz = testLoad.getReturnType();
			
			testLoad.getReturnType();
			testLoad.invoke(instance,new Object[]{});
			//System.out.println(clazz);
			//osgiService.testLoad();
			 * 			*/
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}	
	//@Test
	
	public void testGetCheckResource(){
		CheckParser checkParser = new CheckParser();
		OsgiItem osgiItem = null;
		OsgiService osgiService = null;
		Class<?> clz = null;
		String str = null;
		try {
			int itemId = 100;
			
			osgiItem = (OsgiItem)tfsManager.getOsgiItemById(itemId);
			
			assert(osgiItem != null);
			//加载OsgiService
			if(osgiItem != null){
				clz  = osgiLoader.load(osgiItem);
				osgiService = (OsgiService)clz.newInstance();
				str = osgiService.getRawString("check.xml");
				List<CheckItem> checkList = checkParser.parseCheckResource(str);
				String jsonVar = checkParser.validResourceToJs(checkList);
				assert(clz != null);
				assert(osgiService != null );
				assert(checkList != null);
				assert(!jsonVar.equals(""));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CheckResourceNotValidException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}		
	}
	
	@Test
	public void testGetRender(){
		CheckParser checkParser = new CheckParser();
		OsgiItem osgiItem = null;
		OsgiService osgiService = null;
		Class<?> clz = null;
		String str = null;
		try {
			int itemId = 100;
			
			osgiItem = (OsgiItem)tfsManager.getOsgiItemById(itemId);
			
			assert(osgiItem != null);
			//加载OsgiService
			if(osgiItem != null){
				clz  = osgiLoader.load(osgiItem);
				osgiService = (OsgiService)clz.newInstance();
				Map<Object,Object> renderData = null;//new HashMap<Object,Object>();
				
				str = osgiService.renderOsgi("confirm.vm", renderData);
				
				assert(!str.equals(""));
				assert(clz != null);
				assert(osgiService != null );
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
