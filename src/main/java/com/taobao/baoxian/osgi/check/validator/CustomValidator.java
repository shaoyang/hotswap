package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * �Զ���У���װ��
 * @author chenghao
 * @version 1.0
 */
public class CustomValidator implements TPLValidator, TPLValidatorEx {

	//����У����
	private TPLValidator thirdpartyValidator = null;
	
	public TPLValidator getThirdpartyValidator() {
		return thirdpartyValidator;
	}

	public CustomValidator(TPLValidator thirdpartyValidator) {
		super();
		this.thirdpartyValidator = thirdpartyValidator;
	}

	public boolean check(Map<String, String> attributes, String value) {
		if(null != this.thirdpartyValidator){
			return this.thirdpartyValidator.check(attributes, value);
		}
		else{
			return true;
		}
	}

	public boolean check(Map<String, String> attributes, String[] values) {
		if(null != this.thirdpartyValidator){
			return this.thirdpartyValidator.check(attributes, values);
		}
		else{
			return true;
		}
	}

	/* 
	 * @see com.taobao.baoxian.tpl.validator.TPLValidatorEx#check(java.util.Map, java.util.Map)
	 */
	@Override
	public boolean check(Map<String, String> attributes,
			Map<Object, Object> values) {
		if(null != this.thirdpartyValidator && this.thirdpartyValidator instanceof TPLValidatorEx ){
			return ((TPLValidatorEx)this.thirdpartyValidator).check(attributes, values);
		}
		else{
			return true;
		}
	}
}
