package com.taobao.test.tfs;


import java.sql.SQLException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.taobao.baoxian.pojo.OsgiItem;
import com.taobao.baoxian.tfs.TfsManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring-dao.xml","classpath:WEB-INF/spring-service.xml",
		"classpath:WEB-INF/spring-ibatis.xml","classpath:WEB-INF/spring-conf.xml"})
public class TfsManagerTest{

	@Resource
	private TfsManager tfsManager;

	@Test
	public void tesTfsManager(){
		
		int itemId = 100;
		
		OsgiItem osgiItem;
		try {
			osgiItem = (OsgiItem)this.tfsManager.getOsgiItemById(itemId);
			System.out.println(osgiItem.getJarpath());
			assert(osgiItem != null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
}