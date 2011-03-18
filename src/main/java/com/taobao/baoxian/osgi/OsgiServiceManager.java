package com.taobao.baoxian.osgi;

import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import com.taobao.baoxian.pojo.OsgiItem;

public interface OsgiServiceManager {

	//加载OSGI服务类
	public Class<?> getOsgiClass(OsgiItem item) throws MalformedURLException;
	
	public Method getOsgiMethod(Class<?> cls, Object instance, String methodName) throws SecurityException, NoSuchMethodException;

	public Object getOsgiObject(Class<?> cls) throws InstantiationException,IllegalAccessException;
}
