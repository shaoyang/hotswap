package com.taobao.baoxian.osgi.check.validator;


import java.util.HashMap;
import java.util.Map;

/**
 * 校验方法工厂
 */
public class ValidatorFactory {
	
	private static Map<String,TPLValidator> validatorPool = null;
	
	static {
		validatorPool = new HashMap<String, TPLValidator>();
		//必填项
		validatorPool.put("required", new RequiredValidator());
		//最小长度
		validatorPool.put("minSize", new MinSizeValidator());
		//最大长度
		validatorPool.put("maxSize", new MaxSizeValidator());
		//正则校验
		validatorPool.put("regx", new RegxValidator());
		//年龄范围
		validatorPool.put("ageRange", new AgeRangeValidator());
		//身份证
		validatorPool.put("idCard", new IdCardValidator());
		//身份证
		validatorPool.put("idCardUnion", new IdCardUnionValidator());
		//字符长度
		validatorPool.put("length", new LengthValidator());
		//加和
		validatorPool.put("sum", new SumValidator());
		//大于某个整数
		validatorPool.put("gt", new GtValidator());
		//小于某个整数
		validatorPool.put("lt", new LtValidator());
		//日期范围（零时）
		validatorPool.put("dateRange", new DateRangeValidator());
	}
	
	public static TPLValidator createValidator(String validatorName){
		if(null != validatorName){
			return validatorPool.get(validatorName.trim());
		}
		else{
			return null;
		}
	}
	
	/**
	 * 对于自定义校验方法比较特殊
	 * @param s 第三方校验
	 * @return
	 */
	public static TPLValidator createCustomValidator(TPLValidator s ){
		return new CustomValidator(s);
	}
}
