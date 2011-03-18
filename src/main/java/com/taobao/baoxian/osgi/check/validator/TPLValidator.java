package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * 校验方法公共接口
 */
public interface TPLValidator {
	
	/**
	 * 校验单一值
	 * @param attributes
	 * @param value
	 * @return
	 */
	public boolean check(Map<String,String> attributes, String value);
	
	/**
	 * 校验一组值
	 * @param attributes
	 * @param values
	 * @return
	 */
	public boolean check(Map<String,String> attributes, String[] values);
}
