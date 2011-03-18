package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * �ж������Ƿ�Ϊ��
 * @author chenghao@taobao.com at 2010-4-8
 *
 */
public class RequiredValidator implements TPLValidator {

	/**
	 * У�鵥��ֵ
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
