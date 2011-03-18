package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * 最小字符个数
 */
public class MinSizeValidator implements TPLValidator {

	/**
	 * 校验单个值
	 */
	public boolean check(Map<String, String> attributes, String value) {
		
		if(null == value || null == attributes || null == attributes.get("num")){
			return false;
		}
		else{
			int num = Integer.parseInt(attributes.get("num"));
			if(value.length() < num){
				return false;
			}
			else{
				return true;
			}
		}
	}

	/**
	 * 校验一组值，其中一个为空即返回 false
	 */
	public boolean check(Map<String, String> attributes, String[] values) {
		if(null == values){
			return false;
		}
		
		for(String aValue: values){
			if(false == this.check(attributes, aValue)){
				return false;
			}
		}
		return true;
	}
}
