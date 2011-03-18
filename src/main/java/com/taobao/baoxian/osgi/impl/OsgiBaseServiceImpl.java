package com.taobao.baoxian.osgi.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.AlternatorTool;
import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.IteratorTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.RenderTool;
import org.apache.velocity.tools.generic.ResourceTool;
import org.apache.velocity.tools.generic.SortTool;
import org.apache.velocity.tools.generic.ValueParser;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import com.taobao.baoxian.osgi.OsgiService;
import com.taobao.baoxian.osgi.check.CheckItem;
import com.taobao.baoxian.osgi.check.CheckParser;
import com.taobao.baoxian.osgi.check.exception.CheckResourceNotValidException;
import com.taobao.baoxian.osgi.check.validator.TPLValidator;
import com.taobao.baoxian.utils.HtmlEntity;
import com.taobao.baoxian.utils.render.VelocityRender;

public class OsgiBaseServiceImpl implements OsgiService{

	private static Logger logger = Logger.getLogger("com.taobao.baoxian.osgi");

	
	//校验资源解析器	 
	private CheckParser checkParser;
	
	public void setCheckParser(CheckParser checkParser) {
		this.checkParser = checkParser;
	}

	@Override
	public String renderOsgiFromStr(String osgiContent,
			Map<Object, Object> renderData) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {

		if (null == renderData) {
			renderData = new HashMap<Object, Object>();
		}

		Map<Object, Object> model = new HashMap<Object, Object>();

		// 工具
		model.put("DateUtils", new DateUtils());
		model.put("HtmlEntity", new HtmlEntity());

		model.put("date", new DateTool());
		model.put("math", new MathTool());
		model.put("alternator", new AlternatorTool());
		model.put("comparisonDate", new ComparisonDateTool());
		model.put("escapeTool", new EscapeTool());
		model.put("iteratorTool", new IteratorTool());
		model.put("list", new ListTool());
		model.put("number", new NumberTool());
		model.put("render", new RenderTool());
		model.put("esource", new ResourceTool());
		model.put("sort", new SortTool());
		model.put("valueParser", new ValueParser());

		// 数据
		//model.put("datamap", this.getDataMap(renderData));

		// 装配全局
		/*
		Map<Object, Object> commonData = this.getCommonData(renderData);
		if (null != commonData) {
			commonData.putAll(model);
			if (null != renderData) {
				renderData.putAll(commonData);
			}
		}
		*/
		String commonString = "";/*this.getCommonStirng();
		if (null == commonString) {
			commonString = "";
		} else {
			commonString = commonString.trim();
		}
		
		*/
		/**
		 * 每一步的数据都是有限的，因此每次往model添加数据时，都是在当前的页面发挥作用
		 * 返回时，数据还在不在，所以在程序中要保持之前填写的数据到程序的变量中，而这个变量则是
		 * 保存在容器中的，当返回是可以利用该变量进行渲染的
		 * */
		
		
		Map<Object,Object> userData = new HashMap<Object,Object>();
		
		userData.put("holder_email","1");
		userData.put("holder_name","1");
		userData.put("holder_birthday","1");
		userData.put("holder_card_type","1");
		userData.put("holder_card_no","1");
		userData.put("holder_phone_area","1");
		userData.put("holder_phone","1");
		userData.put("holder_phone_mobile","1");
		model.put("tplVal", userData);
		
		String retContent = commonString + osgiContent;

		try {
			return VelocityRender.render(retContent.trim(), model);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			if(logger.isDebugEnabled())
				logger.error("获取当前webapp路径出错", e);
		}

		return null;
	}

	
	//获取服务文件内容
	@Override
	public String getRawString(String fileName) throws IOException{

		StringBuffer sb = new StringBuffer();

		InputStream ins = this.getClass().getResourceAsStream(fileName);

		InputStreamReader insr = new InputStreamReader(ins, "gbk");

		BufferedReader br = new BufferedReader(insr);

		String aLine = null;

		while (null != (aLine = br.readLine())) {
			sb.append(aLine.trim());
			sb.append("\n");
		}

		ins.close();
		insr.close();
		br.close();

		return sb.toString().trim();
	}


	//获得校验资源
	@Override
	public List<CheckItem> getCheckResource(Map<Object, Object> userData) {

		List<CheckItem> checkItems = null;

		try {
			String checkStr = this.getRawString("check.xml");

			String xml = this.renderOsgiFromStr(checkStr, userData);
			
			checkItems = this.checkParser.parseCheckResource(xml);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CheckResourceNotValidException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return checkItems;
	}

	
	
	public String renderOsgi(String fileName,Map<Object,Object> renderData) throws IOException{
		URL ins = this.getClass().getResource(fileName);
		if(ins == null){
			return null;
		}else{
			String content = this.getRawString(fileName);
			return this.renderOsgiFromStr(content,renderData);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String renderOsgiDetail(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("detail.vm",
					(Map<Object, Object>) HtmlEntity.encode(model));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("加载  detail.vm 文件异常", e);
			return "";
		} catch(Exception e){
			logger.error("加载  detail.vm 文件异常", e);
		}
		return renderResult;
	}

	@Override
	public String renderOsgiEstimate(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("estimate.vm", model);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("加载  estimate.vm 文件异常", e);
			return "";
		}

		if (null != renderResult) {
			//this.hasEstimatePage = true;
		}

		return renderResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderOsgiConfirm(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("confirm.vm",
					(Map<Object, Object>) HtmlEntity.encode(model));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("加载 confirm.vm 文件异常", e);

			return "";
		}
		return renderResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderXmlCalculate(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("xml_calc.vm",
					(Map<Object, Object>) HtmlEntity.encode(model));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("寻找  xml_calc.vm 文件异常", e);
			return "";
		}
		return renderResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderXmlCheck(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("xml_check.vm",
					(Map<Object, Object>) HtmlEntity.encode(model));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("载入  xml_check.vm 文件异常", e);
			return "";
		}
		return renderResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderXmlCheck2(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("xml_check2.vm",
					(Map<Object, Object>) HtmlEntity.encode(model));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("载入  xml_check2 文件异常", e);
			return "";
		}
		return renderResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderXmlPolicy(Map<Object, Object> model) {
		String renderResult = null;
		try {
			renderResult = this.renderOsgi("xml_policy.vm",
					(Map<Object, Object>) HtmlEntity.encode(model));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("载入  xml_check2 文件异常", e);
			return "";
		}
		return renderResult;
	}

	/**
	 * 由子类继承，可以实现自定义的校验规则
	 */
	@Override
	public Map<String, TPLValidator> getCustomValidators() {
		return null;
	}
	
	
	public void testExtend(){
		System.out.println("Successed");
	}
	

}
