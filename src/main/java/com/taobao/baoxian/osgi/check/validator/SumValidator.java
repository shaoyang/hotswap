package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * 最大字符个数
 */
public class SumValidator implements TPLValidator {

	// 没用用到
	@Deprecated
	public boolean check(Map<String, String> attributes, String value) {
		return false;
	}

	/**
	 * 
	 */
	public boolean check(Map<String, String> attributes, String[] values) {
		// 缺少必要属性
		if (null == values || null == attributes
				|| null == attributes.get("result")) {
			return false;
		} else {

			try {
				int result = Integer.parseInt(attributes.get("result"));
				
				int tempResult = 0;
				
				for (int i = 0; i < values.length; i++) {
					
					int v = Integer.parseInt(values[i]);
					tempResult = tempResult + v;
				}

				if (tempResult == result) {
					return true;
				}
				else{
					return false;
				}

			} catch (NumberFormatException ex) {
				return false;
			}
		}
	}
}
