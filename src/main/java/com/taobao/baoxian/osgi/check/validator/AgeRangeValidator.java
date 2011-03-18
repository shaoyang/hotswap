package com.taobao.baoxian.osgi.check.validator;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class AgeRangeValidator implements TPLValidator {

	/**
	 * У�����䷶Χ����min��max֮�䣨��min��max��
	 */
	public boolean check(Map<String, String> attributes, String value) {

		// ȱ�ٱ�Ҫ����
		if (null == value || null == attributes
				|| null == attributes.get("min")
				|| null == attributes.get("max")) {
			return false;
		} else {
			int min = Integer.parseInt(attributes.get("min"));
			int max = Integer.parseInt(attributes.get("max"));

			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			
			try {
				Date birthDay = sDateFormat.parse(value.trim());

				GregorianCalendar minDate = new GregorianCalendar();
				minDate.setTime(new Date());
				minDate.add(GregorianCalendar.YEAR, - min);
				
				GregorianCalendar maxDate = new GregorianCalendar();
				maxDate.add(GregorianCalendar.YEAR, - max - 1);
				
				boolean f1 = birthDay.before(minDate.getTime()) || birthDay
				.equals(minDate.getTime()); 
				boolean f2 = birthDay.after(maxDate.getTime()) || birthDay
				.equals(maxDate.getTime());
				
				if (f1 && f2) {
					return true;
				}
				else{
					return false;
				}

			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
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
}
