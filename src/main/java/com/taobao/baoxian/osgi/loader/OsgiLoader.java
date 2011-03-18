package com.taobao.baoxian.osgi.loader;

import java.net.MalformedURLException;

import com.taobao.baoxian.osgi.OsgiService;
import com.taobao.baoxian.osgi.TestInterface;
import com.taobao.baoxian.pojo.OsgiItem;

public interface OsgiLoader {
	public Class<?> load(OsgiItem item) throws MalformedURLException;
}
