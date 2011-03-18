package com.taobao.baoxian.osgi.check;

import java.util.List;

/**
 * 一个校验项，对应一个<item name=’abc’>
*/
public class CheckItem {
	/**
	 * 布尔表达式类型
	 */
	private String itemName = null;
	private List<Rule> rules = null;
	private String errMsg = null;
	private boolean byPattern = false;
	private boolean inLoop = false;
	private boolean bound = false;
	public boolean isByPattern() {
		return byPattern;
	}

	public CheckItem(String itemName, List<Rule> rules, String errMsg,
			boolean byPattern) {
		this(itemName, rules, errMsg);
		this.byPattern = byPattern;
	}

	public CheckItem(String itemName, List<Rule> rules, String errMsg) {
		this.itemName = itemName;
		this.rules = rules;
		this.errMsg = errMsg;
	}

	public String getItemName() {
		return itemName;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public boolean isInLoop() {
		return inLoop;
	}

	public void setInLoop(boolean inLoop) {
		this.inLoop = inLoop;
	}

	public boolean isBound() {
		return bound;
	}

	public void setBound(boolean bound) {
		this.bound = bound;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public void setByPattern(boolean byPattern) {
		this.byPattern = byPattern;
	}

	@Override
	public String toString() {
		StringBuffer rtsb = new StringBuffer();

		rtsb.append(" TPLCheckItem : { ");
		rtsb.append(" Item's Name : ");
		rtsb.append(this.itemName);
		rtsb.append(" Item's error Msg : ");
		rtsb.append(this.errMsg);
		rtsb.append(" Item's check rules : ");
		rtsb.append(rules.toString());
		rtsb.append("}\n");

		return rtsb.toString();
	}


}
