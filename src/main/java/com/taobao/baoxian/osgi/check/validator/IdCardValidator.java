package com.taobao.baoxian.osgi.check.validator;


import java.util.Map;

/**
 * У�����֤
 */
public class IdCardValidator implements TPLValidator {
	
	private static char[] szVerCode = { '1', '0', 'X', '9', '8', '7', '6', '5',
			'4', '3', '2' };

	private int[] iW = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	public boolean check(Map<String, String> attributes, String value) {

		// ȱ�ٱ�Ҫ����
		if (null == value) {
			return false;
		} else {
			String idNo = value.trim();
			
			if(15 == idNo.length()){
				idNo = this.IdCard15to18(idNo);
			}
			
			if(18 != idNo.length()){
				return false;
			}
			
			
			//У�����֤��ʽ
			return this.checkIdCard(idNo);
		}	
	}

	/**
	 * У��һ��ֵ������һ��Ϊ�ռ����� false
	 */
	public boolean check(Map<String, String> attributes, String[] values) {
		if (null == values) {
			return false;
		}

		for (String aValue : values) {
			if (false == this.check(attributes, aValue)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 15λ���֤ת18λ���֤
	 * 
	 * @param idCard
	 * @return
	 */
	private String IdCard15to18(String idCard) {
		idCard = idCard.trim().toUpperCase();
		StringBuffer idCard18 = new StringBuffer(idCard);
		// ��Ȩ����
		// int[] weight = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
		// У����ֵ
		char[] checkBit = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3',
				'2' };
		int sum = 0;
		// 15λ�����֤
		if (idCard != null && idCard.length() == 15) {
			idCard18.insert(6, "19");
			for (int index = 0; index < idCard18.length(); index++) {
				char c = idCard18.charAt(index);
				int ai = Integer.parseInt(new Character(c).toString());
				// sum = sum+ai*weight[index];
				// ��Ȩ���ӵ��㷨
				int Wi = ((int) Math.pow(2, idCard18.length() - index)) % 11;
				sum = sum + ai * Wi;
			}
			int indexOfCheckBit = sum % 11; // ȡģ
			idCard18.append(checkBit[indexOfCheckBit]);
		}

		return idCard18.toString();
	}

	/**
	 * У�����֤��ʽ
	 * 
	 * @param idCard
	 * @return
	 */
	private boolean checkIdCard(String idCard) {
		idCard = idCard.toUpperCase();
		if (idCard.length() != 18) {
			return false;
		} else {
			char[] idCards = idCard.toCharArray();
			char verifyChar = idCards[17];
			char result = doVerify(idCards);
			if (result == verifyChar) {
				return true;
			}
		}
		return false;
	}

	private char doVerify(char[] idCard) {
		int iS = 0;
		int i;
		for (i = 0; i < 17; i++) {
			iS += (int) (idCard[i] - '0') * iW[i];
		}
		int iY = iS % 11;
		return szVerCode[iY];
	}

	public static void main(String[] args) {
		IdCardValidator t = new IdCardValidator();
		
		System.out.println(t.check(null, "2208021"));
	}
}
