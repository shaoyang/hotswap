package com.taobao.baoxian.dao.support;

import com.ibatis.sqlmap.client.SqlMapClient;

public abstract class DaoSupport {
	
	protected SqlMapClient mysqlClient;
	
	public void setMysqlClient(SqlMapClient mysqlClient) {
		this.mysqlClient = mysqlClient;
	}
}
