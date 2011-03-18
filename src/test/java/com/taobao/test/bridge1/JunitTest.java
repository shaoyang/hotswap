package com.taobao.test.bridge1;

import junit.framework.TestCase;

public class JunitTest extends TestCase{
	/*
	private CourseService courseService = new CourseServiceImpl();
	public void testGetAllCourses(){
		assert(courseService.getAllCourses() != null);
	}
	
	public void testBridgeMethod(){
		C c = new D();
		c.id(new Object());
	}
	*/
	public void testFunction(){
		
		FunctionTest ftest = new FunctionTest();
		
		Function<String,String> lower = new Function<String,String>(){
			public String apply(String arg) {
				return arg.toLowerCase().toString();
			}
			
		};
		System.out.println(ftest.applyFunc(lower, "hello"));
	}
}
