package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * У�鷽�������ӿ�
 */
public interface TPLValidator {
	
	/**
	 * У�鵥һֵ
	 * @param attributes
	 * @param value
	 * @return
	 */
	public boolean check(Map<String,String> attributes, String value);
	
	/**
	 * У��һ��ֵ
	 * @param attributes
	 * @param values
	 * @return
	 */
	public boolean check(Map<String,String> attributes, String[] values);
}
