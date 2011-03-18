package com.taobao.baoxian.pojo;

import java.util.Map;

public class OsgiInfo {
	private OsgiItem osgiItem;

	public OsgiItem getOsgiItem() {
		return osgiItem;
	}

	public void setOsgiItem(OsgiItem osgiItem) {
		this.osgiItem = osgiItem;
	}
	
	private Map<Object ,Object> renderData;

	public Map<Object, Object> getRenderData() {
		return renderData;
	}

	public void setRenderData(Map<Object, Object> renderData) {
		this.renderData = renderData;
	}

}
