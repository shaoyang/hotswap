package com.taobao.baoxian.osgi.impl;

import org.apache.log4j.Logger;

import com.taobao.baoxian.osgi.TestInterface;

public class TestBaseImpl implements TestInterface{
	
	private static Logger logger = Logger.getLogger("com.taobao.baoxian.osgi");

	public void testLoad() {
		System.out.println("TestBaseImpl");
	}

}
