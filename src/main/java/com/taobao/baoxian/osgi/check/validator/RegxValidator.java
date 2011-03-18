package com.taobao.baoxian.osgi.check.validator;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式校验
 */
public class RegxValidator implements TPLValidator {

//	private final String emailRegx = "^([a-zA-Z0-9_-]){3,}@([a-zA-Z0-9_-]){3,}(.([a-zA-Z0-9]){2,4}){1,2}$";
	private final String emailRegx = "^[a-z0-9]([a-z0-9]*[-_\\.]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$";
	private final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
	

	public RegxValidator() {
		dateFormater.setLenient(false);
		dateTimeFormater.setLenient(false);
		timeFormater.setLenient(false);
	}
	
	/**
	 * 校验单个值
	 */
	public boolean check(Map<String, String> attributes, String value) {
		
		
		if(null == value || null == attributes || ( null == attributes.get("rule") && null == attributes.get("expression"))){
			return false;
		}
		else{
			String rule = attributes.get("rule");
			String expression = attributes.get("expression");			
			
			if(null != expression){
				expression = expression.replace("\\\\", "\\");
				return value.trim().matches(expression);
			}
			else if(null != rule){
				if("number".equals(rule)){
					try{
						Double.parseDouble("1.0");
						return true;
					}
					catch(NumberFormatException e){
						return false;
					}
				}
				else if("date".equals(rule)){
					try {
						this.dateFormater.parse(value);
						return true;
					} catch (ParseException e) {
						return false;
					}
				}
				else if("datetime".equals(rule)){
					try {
						this.dateTimeFormater.parse(value);
						return true;
					} catch (ParseException e) {
						return false;
					}					
				}
				else if("time".equals(rule)){
					try {
						this.timeFormater.parse(value);
						return true;
					} catch (ParseException e) {
						return false;
					}
				}
				else if("email".equals(rule)){
					Pattern p = Pattern.compile(this.emailRegx,Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(value.trim());
					
//					if(value.trim().matches(this.emailRegx)){
					if(m.matches()){
						return true;
					}
					else{
						return false;
					}
				}
				else{
					return false;
				}
			}
			else{
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
}
