package com.taobao.baoxian.dao.impl;

import java.sql.SQLException;

import com.taobao.baoxian.dao.TfsDao;
import com.taobao.baoxian.pojo.OsgiItem;
import com.taobao.baoxian.dao.support.DaoSupport;

public class TfsDaoImpl extends DaoSupport implements TfsDao {
	
	public OsgiItem getOsgiItemById(int itemId) throws SQLException{
		return (OsgiItem)mysqlClient.queryForObject("osgiitem.getItemById",itemId);
	}
}
