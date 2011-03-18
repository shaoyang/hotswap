package com.taobao.test.bridge1;

abstract class C <T>{
	abstract T id(T x); 
}

class D extends C<String>{
	String id(String x){
		return x;
	}
}