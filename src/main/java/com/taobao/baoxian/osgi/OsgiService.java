package com.taobao.baoxian.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.taobao.baoxian.osgi.check.CheckItem;

public interface OsgiService extends OsgiCheck {
	
	/**
	 * 获得校验资源
	 * @param userData
	 * @return
	 */
	public List<CheckItem> getCheckResource(Map<Object, Object> renderData);
	
	/**
	 * 获得原始数据
	 * @param fileName
	 * @return
	 */
	public String getRawString(String fileName) throws IOException;
	
	/**
	 * 渲染Osgi 服务页面
	 */
	public String renderOsgi(String fileName,Map<Object,Object> renderData) throws IOException;
	
	/**
	 * 从String渲染Osgi服务页面
	 * @param osgiContent
	 * @param renderData
	 * @return
	 * @throws ParseErrorException
	 * @throws MethodInvocationException
	 * @throws ResourceNotFoundException
	 * @throws IOException
	 */
	public String renderOsgiFromStr(String osgiContent,
			Map<Object, Object> renderData) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException;

	public void testExtend();

	String renderOsgiDetail(Map<Object, Object> model);

	String renderOsgiEstimate(Map<Object, Object> model);

	String renderOsgiConfirm(Map<Object, Object> model);

	String renderXmlCalculate(Map<Object, Object> model);

	String renderXmlCheck(Map<Object, Object> model);

	String renderXmlCheck2(Map<Object, Object> model);

	String renderXmlPolicy(Map<Object, Object> model);
}
