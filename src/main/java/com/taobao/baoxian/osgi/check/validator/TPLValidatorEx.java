package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * 一个更灵活的校验接口，只用于自定义校验。 
 * 在编写自定义校验时，如果TPLValidator不能满足需求，
 * 可以补充实现该接口，当TPLValidator和TPLValidatorEx
 * 同时存在时，系统有限调用TPLValidator中的方法，如果要想
 * 使 TPLValidatorEx 的校验方法起作用，TPLValidator中的两个
 * 校验方法必须返回true.
 */
public interface TPLValidatorEx {

	/**
	 * 校验全部的表单值
	 * 
	 * @param attributes 属性
	 * @param values 全部用户输入数据
	 * @return 是否通过校验
	 */
	public boolean check(Map<String, String> attributes,
			Map<Object, Object> values);

}
