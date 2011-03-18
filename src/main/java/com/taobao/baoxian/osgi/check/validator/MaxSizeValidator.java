package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * ����ַ�����
 */
public class MaxSizeValidator implements TPLValidator {

	/**
	 * У�鵥��ֵ
	 */
	public boolean check(Map<String, String> attributes, String value) {
		
		if(null == value || null == attributes || null == attributes.get("num")){
			return false;
		}
		else{
			int num = Integer.parseInt(attributes.get("num"));
			if(value.length() > num){
				return false;
			}
			else{
				return true;
			}
		}
	}

	/**
	 * У��һ��ֵ������һ��Ϊ�ռ����� false
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
