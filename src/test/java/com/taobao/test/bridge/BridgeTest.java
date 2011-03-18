package com.taobao.test.bridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

public class BridgeTest {
	public static void print(Class cls){
		final List<MethodInfo> result = new ArrayList<MethodInfo>();
		ReflectionUtils.doWithMethods(cls, new ReflectionUtils.MethodCallback() {
			
			public void doWith(Method method) throws IllegalArgumentException,
					IllegalAccessException {
				MethodInfo info = new MethodInfo();
				info.classInfo = method.getDeclaringClass();  
                info.name = method.getName();  
                info.paraTypes = method.getParameterTypes();  
                info.returnType = method.getReturnType();  
                info.bridge = method.isBridge();  
                  
                result.add(info);  

			}
		});
		for(MethodInfo info:result){
			System.out.println(info.toString());
		}
	}
}
