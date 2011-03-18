package com.taobao.baoxian.tfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import com.taobao.baoxian.dao.TfsDao;
import com.taobao.baoxian.pojo.OsgiItem;

public class TfsManager {
	
	private TfsDao tfsDao;
	
	public void setTfsDao(TfsDao tfsDao) {
		this.tfsDao = tfsDao;
	}
	
	
	public OsgiItem getOsgiItemById(int itemId) throws SQLException{
		return tfsDao.getOsgiItemById(itemId);
	}
	/**
	 * 通过数据库取得jar的存储路径，将文件以输出流的方式写到OutputStream
	 * @author ShaoYang
	 * @param tfsId		file identifier
	 * @param suffix 	file suffix
	 * @param out		output
	 * @return boolean
	 * @throws MalformedURLException
	 */
	public static boolean fetchFile(String tfsId,String suffix,OutputStream out) throws MalformedURLException{
		StringBuffer sb = new StringBuffer();
		sb.append("jar:file://");
		sb.append(tfsId);
		sb.append(suffix);
		sb.append("!/");
		URL url = new URL(sb.toString().trim());
		InputStream ins = null;
		try {
			ins = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
