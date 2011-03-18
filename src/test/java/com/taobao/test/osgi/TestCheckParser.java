package com.taobao.test.osgi;

import java.util.List;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

import com.taobao.baoxian.osgi.check.CheckItem;
import com.taobao.baoxian.osgi.check.CheckParser;
import com.taobao.baoxian.osgi.check.exception.CheckResourceNotValidException;

public class TestCheckParser extends TestCase{

	public void testCheck(){
		String filePath = "/home/villasy/resources/yangguang/check.xml";
		List<CheckItem> checkList = null;
		CheckParser checkParser = new CheckParser();
		String checkResource = checkParser.getRawString(filePath);
		assertNotNull(checkResource);
		
		try {
			checkList = checkParser.parseCheckResource(checkResource);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CheckResourceNotValidException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		assertNotNull(checkList);
		String jsonStr = checkParser.validResourceToJs(checkList);
		assertNotNull(jsonStr);
	}
}
