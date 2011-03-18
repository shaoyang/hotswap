package com.taobao.baoxian.osgi.check;


import java.util.ArrayList;
import java.util.Map;

/**
 * 一个校验规则，如required.
 * @author chenghao
 * @version 1.0
 */
public class Rule{
	
	private String name = null;
	private ArrayList<Object> condition = null;
	private String errMsg = null;
	private Map<String,String> attributes = null;
	

	public Rule(String name, ArrayList<Object> condition, String errMsg,
			Map<String,String> attributes) {
		super();
		this.name = name;
		this.condition = condition;
		this.errMsg = errMsg;
		this.attributes = attributes;
	}


	@Override
	public String toString() {
		StringBuffer rtsb = new StringBuffer();
		
		rtsb.append(" Ruel["+this.name+"] : {");
		rtsb.append(" the rule's error Msg : ");
		rtsb.append(this.errMsg);
		rtsb.append(" the rule's condition : ");
		rtsb.append(this.condition.toString());
		rtsb.append(" the rule's attrs : ");
		rtsb.append(this.attributes.toString());
		rtsb.append("}");
		
		return rtsb.toString();
	}


	public String getName() {
		return name;
	}


	public ArrayList<Object> getCondition() {
		return condition;
	}


	public String getErrMsg() {
		return errMsg;
	}


	public Map<String, String> getAttributes() {
		return attributes;
	}		
	
}