package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * 判断数据是否为空
 * @author chenghao@taobao.com at 2010-4-8
 *
 */
public class RequiredValidator implements TPLValidator {

	/**
	 * 校验单个值
	 */
	public boolean check(Map<String, String> attributes, String value) {
		
		if(null == value || 0 == value.trim().length()){
			return false;
		}
		else{
			return true;
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
