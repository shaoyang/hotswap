package com.taobao.baoxian.osgi.check.validator;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 在未来时间天数之内，如果次日零时到未来60日零时可以这样标示 dateRange: min:1, max:60
 */
public class DateRangeValidator implements TPLValidator {

	public boolean check(Map<String, String> attributes, String value) {
		
		if(null == value || null == attributes || null == attributes.get("min") || null == attributes.get("max")){
			return false;
		}
		else{
			try{
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				Date valueDate = dateFormat.parse(value);
				
				String min = attributes.get("min");				
				String max = attributes.get("max");				
				
				Date minDate = dateFormat.parse(min);
				Date maxDate = dateFormat.parse(max);
				
				if( (valueDate.equals(minDate) || valueDate.after(minDate))  && 
						(valueDate.equals(maxDate) || valueDate.before(maxDate))){
					return true;
				}
				else{
					return false;
				}
				
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
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
	
	//测试
	public static void main(String[] args) {
		DateRangeValidator v = new DateRangeValidator();
		
		boolean s = v.check(new HashMap<String,String>(){
			private static final long serialVersionUID = 1L;

				{
					put("min","2010-5-21");
					put("max","2010-5-24");
				}	
			}, "2010-5-21");
		
		System.out.println(s);
	}
}
