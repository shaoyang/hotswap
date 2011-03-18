package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * ����ĳ������
 */
public class GtValidator implements TPLValidator {

	public boolean check(Map<String, String> attributes, String value) {
		
		if(null == value || null == attributes || null == attributes.get("value")){
			return false;
		}
		else{
			try{
				int num = Integer.parseInt(attributes.get("value"));				
				
				int valInt = Integer.parseInt(value);
				
				if(valInt > num){
					return true;
				}
				else{
					return false;
				}
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
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
