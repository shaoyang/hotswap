/**
 * @author chenghao chenghao@taobao.com
 * Created At 2010 2010-5-17 上午09:39:43
 */
package com.taobao.baoxian.utils;

import java.util.List;
import java.util.Map;

/**
 * 仿照 php 的 htmlspecialchars 和 htmlspecialchars_decode 函数 将数据中的 < > & " ' 转换成
 * html 实体。
 */
@SuppressWarnings("unchecked")
public class HtmlEntity {

	/**
	 * 将html特殊字符进行转义
	 * 
	 * @param input
	 * @return
	 */
	public static Object encode(Object input) {

		if (null != input) {

			if (input instanceof Object[]) {
				Object[] arrClone = (Object[]) input;
				for (int i = 0; i < arrClone.length; i++) {
					
					if(arrClone[i] instanceof String){
						arrClone[i] = encodeString((String)arrClone[i]);
					}
					else{
						arrClone[i] = encode(arrClone[i]);
					}
					
				}

			} else if (input instanceof Map) {

				Map mapClone = (Map) input;

				for (Object key : mapClone.keySet()) {
					if(mapClone.get(key) instanceof String){
						mapClone.put(key, encodeString((String)mapClone.get(key)));
					}
					else{
						mapClone.put(key, encode(mapClone.get(key)));						
					}
				}

			} else if (input instanceof List) {
				List listClone = (List) input;
				for (int i = 0; i < listClone.size(); i++) {
					
					if(listClone.get(i) instanceof String){
						listClone.set(i, encodeString((String)listClone.get(i)));
					}
					else{
						listClone.set(i, listClone.get(i));
					}
				}
			} 
			return input;
		} else {
			return null;
		}
	}

	public static String encodeString(String input) {

		if(null == input){
			return null;
		}
		
		return input.replace("&", "&amp;").replace("<", "&lt;").replace(">",
				"&gt;").replace("\"", "&quot;").replace("'", "&#039;");
	}

	public static String decodeString(String input) {

		if(null == input){
			return null;
		}
		
		return input.replace("&amp;", "&").replace("&lt;", "<")
		.replace("&gt;", ">").replace("&quot;", "\"").replace(
				"&#039;", "'");
	}
	
	public static Object decode(Object input) {

		if (null != input) {

			if (input instanceof Object[]) {
				Object[] arrClone = (Object[]) input;
				for (int i = 0; i < arrClone.length; i++) {
					if(arrClone[i] instanceof String){
						arrClone[i] = decodeString((String)arrClone[i]);						
					}
					else{
						arrClone[i] = decode(arrClone[i]);												
					}
				}

			} else if (input instanceof Map) {

				Map mapClone = (Map) input;

				for (Object key : mapClone.keySet()) {
					
					if(mapClone.get(key) instanceof String){
						mapClone.put(key, decodeString((String)mapClone.get(key)));						
					}
					else{						
						mapClone.put(key, decode(mapClone.get(key)));						
					}
				}

			} else if (input instanceof List) {
				List listClone = (List) input;
				for (int i = 0; i < listClone.size(); i++) {
					if(listClone.get(i) instanceof String){
						listClone.set(i, decodeString((String)listClone.get(i)));						
					}
					else{						
						listClone.set(i, decode(listClone.get(i)));
					}
				}
			}
			return input;
		} else {
			return null;
		}
	}
}
