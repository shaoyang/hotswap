package com.taobao.baoxian.osgi.check.validator;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 校验身份证
 */
public class IdCardUnionValidator implements TPLValidator {
	
	private static char[] szVerCode = { '1', '0', 'X', '9', '8', '7', '6', '5',
			'4', '3', '2' };

	private int[] iW = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	public boolean check(Map<String, String> attributes, String value) {

		// 缺少必要属性
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
			
			if(null != attributes){
				//关联生日
				if(null != attributes.get("birthdayItem")){
					SimpleDateFormat inputBirthdayFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
					SimpleDateFormat cardBirthdayFormat = new SimpleDateFormat(
					"yyyyMMdd");
					
					try {
						Date inputBirthdayDate = inputBirthdayFormat.parse(attributes.get("birthdayItem"));
						Date cardBirthdayDate = cardBirthdayFormat.parse(idNo.substring(6,14));
						
						if(false == inputBirthdayDate.equals(cardBirthdayDate)){
							return false;
						}
						
					} catch (ParseException e) {
						e.printStackTrace();
						return false;
					}
					
				}
				
				//关联性别
				if(null != attributes.get("genderItem")){
					String genderBit = idNo.substring(16,17);
					try{
						int genderFlag = Integer.parseInt(genderBit);
						int inputFlag = Integer.parseInt(attributes.get("genderItem"));
						
						if( (genderFlag % 2 ) != (inputFlag % 2)){
							return false;
						}
					}
					catch(NumberFormatException e){
						e.printStackTrace();
						return false;
					}
				}
			}
			
			//校验身份证格式
			return this.checkIdCard(idNo);
		}	
	}

	/**
	 * 校验一组值，其中一个为空即返回 false
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
	 * 15位身份证转18位身份证
	 * 
	 * @param idCard
	 * @return
	 */
	private String IdCard15to18(String idCard) {
		idCard = idCard.trim().toUpperCase();
		StringBuffer idCard18 = new StringBuffer(idCard);
		// 加权因子
		// int[] weight = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
		// 校验码值
		char[] checkBit = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3',
				'2' };
		int sum = 0;
		// 15位的身份证
		if (idCard != null && idCard.length() == 15) {
			idCard18.insert(6, "19");
			for (int index = 0; index < idCard18.length(); index++) {
				char c = idCard18.charAt(index);
				int ai = Integer.parseInt(new Character(c).toString());
				// sum = sum+ai*weight[index];
				// 加权因子的算法
				int Wi = ((int) Math.pow(2, idCard18.length() - index)) % 11;
				sum = sum + ai * Wi;
			}
			int indexOfCheckBit = sum % 11; // 取模
			idCard18.append(checkBit[indexOfCheckBit]);
		}

		return idCard18.toString();
	}

	/**
	 * 校验身份证格式
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
		IdCardUnionValidator t = new IdCardUnionValidator();
		
		System.out.println(t.check(null, "2208021"));
	}
}
