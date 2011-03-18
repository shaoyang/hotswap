package com.taobao.baoxian.osgi.impl;

import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import com.taobao.baoxian.osgi.OsgiServiceManager;
import com.taobao.baoxian.osgi.loader.URLOsgiLoader;
import com.taobao.baoxian.pojo.OsgiItem;

public class OsgiServiceManagerImpl implements OsgiServiceManager{

	private static Logger logger = Logger.getLogger("com.taobao.baoxian.osgi");

	
	/**
	 * 从OsgiItem服务项目直接得到要使用的Object
	 */
	@Override
	public Object getOsgiObject(Class<?> cls) throws InstantiationException, IllegalAccessException{
		logger.debug("返回Osgi实例");
		return cls.newInstance();
	}
	
	@Override
	public Class<?> getOsgiClass(OsgiItem item) throws MalformedURLException{
		Class<?> cls = null;
		//每次使用新的类加载器去加载类
		URLOsgiLoader osgiLoader = new URLOsgiLoader();
		logger.debug("加载Osgi类");
		cls = osgiLoader.load(item);
		return cls;
	}
	
	@Override
	public Method getOsgiMethod(Class<?> cls,Object instance,String methodName) throws SecurityException, NoSuchMethodException{
		//使用反射加载方法
		Method method = cls.getMethod(methodName,new Class[]{});
		logger.debug("返回Osgi方法");
		return method;
	}

	/*
	public List<CheckItem> getCheckService(OsgiInfo osgiInfo) {
		try{
			OsgiService osgiService = this.getOsgiService(osgiInfo.getOsgiItem());
			return osgiService.getCheckResource(osgiInfo.getRenderData());
		}catch(Exception ex){
			
		}
		return null;
	}
	*/
	
}
