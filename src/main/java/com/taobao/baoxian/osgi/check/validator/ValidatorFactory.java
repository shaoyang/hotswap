package com.taobao.baoxian.osgi.check.validator;


import java.util.HashMap;
import java.util.Map;

/**
 * У�鷽������
 */
public class ValidatorFactory {
	
	private static Map<String,TPLValidator> validatorPool = null;
	
	static {
		validatorPool = new HashMap<String, TPLValidator>();
		//������
		validatorPool.put("required", new RequiredValidator());
		//��С����
		validatorPool.put("minSize", new MinSizeValidator());
		//��󳤶�
		validatorPool.put("maxSize", new MaxSizeValidator());
		//����У��
		validatorPool.put("regx", new RegxValidator());
		//���䷶Χ
		validatorPool.put("ageRange", new AgeRangeValidator());
		//���֤
		validatorPool.put("idCard", new IdCardValidator());
		//���֤
		validatorPool.put("idCardUnion", new IdCardUnionValidator());
		//�ַ�����
		validatorPool.put("length", new LengthValidator());
		//�Ӻ�
		validatorPool.put("sum", new SumValidator());
		//����ĳ������
		validatorPool.put("gt", new GtValidator());
		//С��ĳ������
		validatorPool.put("lt", new LtValidator());
		//���ڷ�Χ����ʱ��
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
	 * �����Զ���У�鷽���Ƚ�����
	 * @param s ������У��
	 * @return
	 */
	public static TPLValidator createCustomValidator(TPLValidator s ){
		return new CustomValidator(s);
	}
}
