package com.taobao.baoxian.osgi;

import java.util.List;
import java.util.Map;

import com.taobao.baoxian.osgi.check.CheckItem;
import com.taobao.baoxian.osgi.check.validator.TPLValidator;

public interface OsgiCheck {
	
	/**
	 * 获得校验定义数据结构
	 * @return
	 */
	//List<CheckItem> getCheckResource(Map<Object,Object> userData);
	
	/**
	 * 获得自定义校验方法
	 * @return
	 */
	Map<String, TPLValidator> getCustomValidators();
}
