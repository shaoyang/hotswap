package com.taobao.baoxian.osgi.check.validator;


import java.util.List;

/**
 * 后台校验数据的结果封装
 *
 */
public class CheckResult {
	private List<String> errMsgs = null;
	private List<String> errItems = null;
	
	private boolean passed = false;

	public CheckResult(List<String> errMsgs,
			List<String> itemName, boolean passed) {
		super();
		this.errMsgs = errMsgs;
		this.errItems = itemName;
		this.passed = passed;
	}

	public List<String> getErrMsgs() {
		return errMsgs;
	}

	public List<String> getErrItems() {
		return errItems;
	}

	public boolean isPassed() {
		return passed;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
	
		sb.append("\tpassed : ");
		sb.append(this.passed);
		sb.append("\n\terr items : ");
		sb.append(this.errItems);
		sb.append("\n\terr messages : ");
		sb.append(this.errMsgs);
		
		return sb.toString();
	}
	
}
