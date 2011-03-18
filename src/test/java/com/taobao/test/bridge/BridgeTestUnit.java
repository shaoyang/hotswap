package com.taobao.test.bridge;

import junit.framework.TestCase;


public class BridgeTestUnit extends TestCase{
	public void testBridge(){
		BridgeTest.print(MyEnable.class);
		System.out.println("-------");
		BridgeTest.print(new Other<String>().getClass());
	}
}
