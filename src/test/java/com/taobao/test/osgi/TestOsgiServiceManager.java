package com.taobao.test.osgi;

import static org.junit.Assert.assertNotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.SQLException;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.taobao.baoxian.osgi.OsgiService;
import com.taobao.baoxian.osgi.OsgiServiceManager;
import com.taobao.baoxian.osgi.check.CheckParser;
import com.taobao.baoxian.pojo.OsgiItem;
import com.taobao.baoxian.tfs.TfsManager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring-dao.xml","classpath:WEB-INF/spring-service.xml",
		"classpath:WEB-INF/spring-ibatis.xml","classpath:WEB-INF/spring-conf.xml"})
public class TestOsgiServiceManager {

	@Resource
	private OsgiServiceManager osgiServiceManager;
	
	@Resource
	private TfsManager tfsManager;
	@Test
	public void testCheck(){
		int itemId = 100;
		OsgiItem item;
			try {
				item = tfsManager.getOsgiItemById(itemId);
				Class<?> clz = osgiServiceManager.getOsgiClass(item);
				OsgiService osgiService = (OsgiService)osgiServiceManager.getOsgiObject(clz);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		
	}
	@Test
	public void testGet(){
		int itemId = 100;
		OsgiItem item;
		try {
			item = tfsManager.getOsgiItemById(itemId);
			//测试类
			Class<?> clz = osgiServiceManager.getOsgiClass(item);
			assertNotNull(clz);
			//测试对象
			Object instance = osgiServiceManager.getOsgiObject(clz);
			assertNotNull(instance);
			//测试方法
			String methodName = "testLoad";
			Method method = osgiServiceManager.getOsgiMethod(clz, instance, methodName);
			assertNotNull(method);
			method.invoke(instance, new Object[]{});
		
			//测试加载校验文件
			CheckParser checkParser = new CheckParser();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
}
