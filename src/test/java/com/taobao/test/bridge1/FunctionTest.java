package com.taobao.test.bridge1;

public class FunctionTest {
	public <A,R> R applyFunc(Function<A,R> func,A arg){
		return (R) func.apply(arg);
	}
}
