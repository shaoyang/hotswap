package com.taobao.baoxian.utils;


import java.util.HashMap;
import java.util.Map;

public class Customer {
	private String name;
	private Map<String,String> map = new HashMap();
	private String[] names = {"Shaoyang",null,"pantingting","gaomengmeng"};
	
	public Customer(){
		map.put("Shaoyang", "1");
		map.put("cx",null);
		map.put("ptt","2");
		map.put("gmm", "4");
	}
	public String getName() {
		return name;
	}
/*	
	public String getname(){
		return "setname:"+ name;
	}
*/	
	public void setName(String name) {
		this.name = name;
	}
	
	public String[] getNames(){
		return this.names;
	}
	public String getValue(String name){
		System.out.println(name);
		return map.get(name);
	}
}
