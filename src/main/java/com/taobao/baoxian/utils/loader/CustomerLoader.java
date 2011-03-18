package com.taobao.baoxian.utils.loader;

import com.taobao.baoxian.osgi.OsgiService;
import com.taobao.baoxian.pojo.OsgiItem;

public interface CustomerLoader {
	public OsgiService load(OsgiItem item);
}
