package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * һ��������У��ӿڣ�ֻ�����Զ���У�顣 
 * �ڱ�д�Զ���У��ʱ�����TPLValidator������������
 * ���Բ���ʵ�ָýӿڣ���TPLValidator��TPLValidatorEx
 * ͬʱ����ʱ��ϵͳ���޵���TPLValidator�еķ��������Ҫ��
 * ʹ TPLValidatorEx ��У�鷽�������ã�TPLValidator�е�����
 * У�鷽�����뷵��true.
 */
public interface TPLValidatorEx {

	/**
	 * У��ȫ���ı�ֵ
	 * 
	 * @param attributes ����
	 * @param values ȫ���û���������
	 * @return �Ƿ�ͨ��У��
	 */
	public boolean check(Map<String, String> attributes,
			Map<Object, Object> values);

}
