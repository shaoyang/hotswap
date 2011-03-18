package com.taobao.baoxian.dao;

import java.sql.SQLException;

import com.taobao.baoxian.pojo.OsgiItem;

public interface TfsDao {
	public OsgiItem getOsgiItemById(int itemId) throws SQLException;
}
