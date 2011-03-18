package com.taobao.test.bridge2;

import java.lang.reflect.Method;

public class TestBridge {
	public static void main(String[] args) {
		Class<B> clazz = B.class;
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			System.out.println(getMethodInfo(m) + " is Bridge Method? " + m.isBridge());
		}
	}
	
	public static String getMethodInfo(Method m){
		StringBuilder sb = new StringBuilder();
		sb.append(m.getReturnType()).append(" ");
		sb.append(m.getName());
		Class[]params = m.getParameterTypes();
		for (int i = 0; i < params.length; i++) {
			sb.append(params[i].getName()).append(" ");
		}
		return sb.toString();
	}
}