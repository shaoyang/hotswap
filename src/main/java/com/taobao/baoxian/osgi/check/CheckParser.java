package com.taobao.baoxian.osgi.check;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.taobao.baoxian.osgi.check.exception.CheckResourceNotValidException;
import com.taobao.baoxian.osgi.check.exception.DataMapResourceNotValidException;
import com.taobao.baoxian.utils.render.VelocityRender;

public class CheckParser {

	private static Logger logger = Logger.getLogger("com.taobao.baoxian.osgi");
	public final static String TPL_VAR_FLG = "#";
	private static Validator validator;
	

	static {
		//在服务器版本须先获得webapp路径,在服务器上是以war包形式工作地
		//String currDir = DataChecker.class.getClassLoader().getResource("").getPath();
		//System.out.println(currDir);
		//Config.RESOURCES_DIR
		File schemaFile = new File("src/main/webapp/WEB-INF/check-rules.xsd");
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		try {
			Schema schema = schemaFactory.newSchema(schemaFile);
			validator = schema.newValidator();
		} catch (SAXException e) {
			e.printStackTrace();
			validator = null;
		}
	}
	
	/**
	 * 获得文件内容
	 * @param fileName 文件名
	 * @return
	 * @throws IOException
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
	 */

	
	public String getRawString(String filePath){
		File file = new File(filePath);
		BufferedInputStream fis = null;
		if(file!=null){
			int len = (int)file.length();
			byte[] buffer = new byte[len];
			try {
				fis = new BufferedInputStream(new FileInputStream(file));

				fis.read(buffer);
		
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (fis != null){ 
				try{ 
					fis.close(); 
				} catch (IOException ignored) { }
			}
			try {
				return new String(buffer,"gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	//解析tpl-check-rules.xsd校验规则，将校验选项存储在CheckItem中
	public List<CheckItem> parseCheckResource(String checkResource)
		throws DocumentException, CheckResourceNotValidException, SAXException {

		// 1. 校验 check.xml
		StringReader sr = new StringReader(checkResource.trim());
		
		Source source = new StreamSource(sr);
		
		try {
			validator.validate(source);
		} catch (SAXException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new CheckResourceNotValidException("");
		} 
		
		ArrayList<CheckItem> items = new ArrayList<CheckItem>();
		
		SAXReader reader = new SAXReader();
		
		StringReader strReader = new StringReader(checkResource);
		
		Document document = reader.read(strReader);
		
		Element root = document.getRootElement();
		
		//获得所有item标签内容
		List<?> itemElems = root.elements("item");
		
		// 获取所有item元素，解析然后添加到items
		for (Iterator<?> itemsIt = itemElems.iterator(); itemsIt.hasNext();) {
			Element itemElem = (Element) itemsIt.next();
			String itemName = itemElem.attributeValue("name");
			String errMsg = null;
			List<Rule> itemRules = new ArrayList<Rule>();
			// 获取出错信息
			Element errMsgElem = itemElem.element("errmsg");
			if (null != errMsgElem) {
				errMsg = errMsgElem.getTextTrim();
			}
		
			// 获得验证规则
			//获得itemElem下的elements
			List<?> itemRuleNodes = itemElem.elements();
			for (Iterator<?> itemRulesIt = itemRuleNodes.iterator(); itemRulesIt
					.hasNext();) {
				Element ruleElem = (Element) itemRulesIt.next();
		
				String ruleName = ruleElem.getName();
				String ruleErrMsg = null;
				Map<String, String> ruleAttrs = new HashMap<String, String>();
				ArrayList<Object> ruleCond = null;
		
				// 跳过errmsg节点
				if (false == ruleElem.getName().equalsIgnoreCase("errmsg")) {
		
					// 获得错误信息
					Element ruleErrMsgElem = ruleElem.element("errmsg");
					if (null != ruleErrMsgElem) {
						ruleErrMsg = ruleErrMsgElem.getTextTrim();
					}
		
					// 获取属性
					int ruleAtrrNum = ruleElem.attributeCount();
					if (0 != ruleAtrrNum) {
						for (int i = 0; i < ruleAtrrNum; i++) {
							Attribute aAttr = ruleElem.attribute(i);
							ruleAttrs.put(aAttr.getName(), aAttr
									.getStringValue());
						}
					}
		
					// 获取条件
					Element ruleCondElem = ruleElem.element("when");
		
					ruleCond = this.parseConditionElement(ruleCondElem);
		
					// 创建Rule对象
					itemRules.add(new Rule(ruleName, ruleCond, ruleErrMsg,
							ruleAttrs));
				}
		
			}
		
			CheckItem checkItem = new CheckItem(itemName, itemRules,
					errMsg);
		
			if (null != itemElem.attributeValue("inLoop")) {
				checkItem.setInLoop(true);
			}
		
			if (null != itemElem.attributeValue("bound")) {
				checkItem.setBound(true);
			}
		
			if (null != itemElem.attributeValue("byPattern")) {
				checkItem.setByPattern(true);
			}
		
			items.add(checkItem);
		}
		
		return items;
	}
	
	/**
	 * 解析条件结构（递归）
	 * 
	 * <and>(LinkedHashMap<and,List>) <boolexpr... /> (BoolExpresion) 
	 * <or>(LinkedHashMap) <boolexpr... /> (BoolExpresion) </or> </and>
	 * @param condElem
	 * @return
	 */
	private ArrayList<Object> parseConditionElement(Element condElem) {
		ArrayList<Object> cond = new ArrayList<Object>();

		if (null != condElem) {
			List<?> subCondElems = condElem.elements();
			for (Iterator<?> subCondElemsIt = subCondElems.iterator(); subCondElemsIt
					.hasNext();) {
				Element aSubCondElem = (Element) subCondElemsIt.next();
				if (aSubCondElem.getName().equalsIgnoreCase("and")
						|| aSubCondElem.getName().equalsIgnoreCase("or")) {
					// 对于and ，or元素用Map 进行封装
					Map<String, Object> logicBody = new LinkedHashMap<String, Object>();
					logicBody.put(aSubCondElem.getName(), this
							.parseConditionElement(aSubCondElem));

					cond.add(logicBody);

				} else if (aSubCondElem.getName().equalsIgnoreCase("boolexpr")) {
					String left = aSubCondElem.attributeValue("left");
					String operator = aSubCondElem.attributeValue("op");
					String right = aSubCondElem.attributeValue("right");
					BoolExpresion aExpr = new BoolExpresion(left, operator,
							right);

					if (null != aSubCondElem.attributeValue("leftAtOuter")
							&& "true" == aSubCondElem
									.attributeValue("leftAtOuter")) {
						aExpr.setLeftAtOuter(true);
					}

					if (null != aSubCondElem.attributeValue("rightAtOuter")
							&& "true" == aSubCondElem
									.attributeValue("rightAtOuter")) {
						aExpr.setRightAtOuter(true);
					}

					cond.add(aExpr);
				}

			}
		}

		return cond;
	}

	//获得check.xml资源
	public List<CheckItem> getCheckResource(Map<Object, Object> userData) {
		List<CheckItem> items = null;
		//这三步是核心
		try {
			//获得check.xml字符串
			String checkStr = this.getRawString("check.xml");
			//将check.xml中的vm变量进行渲染
			String xml = this.renderTPLFromStr(checkStr, userData);
			//解析渲染过的xml文件，得到校验规则
			items = parseCheckResource(xml);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CheckResourceNotValidException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return items;
	}
	
	
	
	/**
	 * 将 check.xml 转成前端可识别的Json格式。
	 * 
	 * @param checkItems
	 * @return
	 */
	public String validResourceToJs(List<CheckItem> checkItems) {

		StringBuffer sb = new StringBuffer();
		sb.append("<script type=\"text/javascript\">");
		sb.append("var Validator_JSON = {");

		for (CheckItem item : checkItems) {

			// 联合校验类型用追加#B
			if (item.isBound()) {
				sb.append(" '" + item.getItemName() + "#B' : { ");
			} else {
				sb.append(" '" + item.getItemName() + "' : { ");
			}

			if (item.isInLoop()) {
				sb.append("inLoop : true ,");
			}
			/*
			 * if (item.isBound()) { sb.append("bound : true ,"); }
			 */
			if (item.isByPattern()) {
				sb.append("byPattern : true ,");
			}

			for (Rule rule : item.getRules()) {
				sb.append(rule.getName() + " : {");

				Map<String, String> attrs = rule.getAttributes();

				if (null != attrs) {
					if ("custom".equals(rule.getName())) {
						sb.append("validatorName : '"
								+ attrs.get("validatorName") + "' ,");
						sb.append("params : {");

						for (Object key : attrs.keySet()) {
							if (true == "custom".equals(rule.getName())
									&& false == "validatorName".equals(key)) {
								sb.append(key + " : '" + attrs.get(key) + "',");
							}
						}
						sb.append("},");
					} else {
						for (Object key : attrs.keySet()) {
							sb.append(key + " : '" + attrs.get(key) + "',");
						}
					}
				}
				// 条件
				List<Object> cond = rule.getCondition();

				if (null != cond && 0 != cond.size()) {
					sb.append("condition : '");
					sb.append(convtCondToJs(cond.get(0), item.isInLoop()));
					sb.append("', ");
				}

				sb.append("errmsg : '" + rule.getErrMsg() + "'");
				sb.append(" }, ");
			}

			sb.append(" },");
		}

		sb.append("};");
		sb.append("</script>");

		return sb.toString().replaceAll(",(\\s)*}", "}");
	}

	/**
	 * 转换条件表达式到js
	 * 
	 * @return
	 */
	private static String convtCondToJs(Object aCond, boolean isInLoop) {

		if (null == aCond) {
			return "";
		}

		StringBuffer sb = new StringBuffer();

		// 如果布尔表达式
		if (aCond instanceof BoolExpresion) {
			BoolExpresion theBoolExpr = (BoolExpresion) aCond;

			if (null != theBoolExpr.getOperator()
					&& null != theBoolExpr.getLeft()) {

				boolean leftIsAVar = isVar(theBoolExpr.getLeft());
				boolean rightIsAVar = isVar(theBoolExpr.getRight());

				sb.append(" ");
				// 处理左值
				if (true == leftIsAVar) {
					sb.append(theBoolExpr.getLeft());
					/*
					 * if (true == isInLoop && false ==
					 * theBoolExpr.isLeftAtOuter()) { sb.append("#N"); }
					 */

				} else {
					//循环体变量
					if (theBoolExpr.getLeft().trim().startsWith("{")
							&& theBoolExpr.getLeft().trim().endsWith("}")) {
						sb.append(" " + theBoolExpr.getLeft() + " ");
					}
					//常量
					else {
						sb.append(" \"" + theBoolExpr.getLeft() + "\" ");
					}
				}

				// 处理操作符
				if (true == leftIsAVar
						&& "checked".equals(theBoolExpr.getOperator())) {
					sb.append(".checked ");
				} else {
					sb.append(" " + theBoolExpr.getJsOP() + " ");
				}

				// 处理右值
				// 如果不是checked类型，右值才有效
				/*
				 * if (false == "checked".equals(theBoolExpr.getOperator())) {
				 * // 变量 if (true == rightIsAVar) { sb.append(" " +
				 * theBoolExpr.getRight() + " "); } // 常量 else { sb.append(" \""
				 * + theBoolExpr.getRight() + "\" "); } }
				 */

				if (true == rightIsAVar) {
					sb.append(" " + theBoolExpr.getRight() + " ");
				}
				else {
					//循环体变量
					if (theBoolExpr.getRight().trim().startsWith("{")
							&& theBoolExpr.getRight().trim().endsWith("}")) {
						sb.append(" " + theBoolExpr.getRight() + " ");
					} 
					// 常量
					else {
						sb.append(" \"" + theBoolExpr.getRight() + "\" ");
					}
				}

				sb.append(" ");
			}

		} else if (aCond instanceof Map<?, ?>) {
			boolean isAnd = true;

			Object andOr = ((Map<?, ?>) aCond).get("and");

			if (null == andOr) {
				andOr = ((Map<?, ?>) aCond).get("or");
				isAnd = false;
			}

			if (null != andOr && andOr instanceof List<?>) {
				sb.append(" (");

				List<?> theSubCondList = (List<?>) andOr;
				int idx = 1;
				for (Object cond : theSubCondList) {
					if (1 != idx) {
						if (true == isAnd) {
							sb.append(" && ");
						} else {
							sb.append(" || ");
						}
					}
					sb.append(convtCondToJs(cond, isInLoop));
					idx++;
				}

				sb.append(") ");
			}
		}

		return sb.toString();
	}

	protected String renderTPLFromStr(String tplContent,
			Map<Object, Object> userData) throws ParseErrorException,
			MethodInvocationException, ResourceNotFoundException, IOException {

		if (null == userData) {
			userData = new HashMap<Object, Object>();
		}

		Map<Object, Object> model = new HashMap<Object, Object>();

		// 工具
		/*
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
		*/
		// 获得用户数据
		model.put("datamap", this.getDataMap(userData));

		// 装配全局
		Map<Object, Object> commonData = this.getCommonData(userData);
		if (null != commonData) {
			commonData.putAll(model);
			if (null != userData) {
				userData.putAll(commonData);
			}
		}

		String commonString = "";			//this.getCommonStirng();
		if (null == commonString) {
			commonString = "";
		} else {
			commonString = commonString.trim();
		}

		model.put("tplVal", userData);

		String retContent = commonString + tplContent;
		try {
			return VelocityRender.render(retContent.trim(), model);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		//	logger.error("获取当前webapp路径出错", e);
		}

		return null;
	}
	
	public DataMap getDataMap(Map<Object, Object> userData) {

		InputStream datamapIns = this.getClass().getResourceAsStream(
				"datamap.xml");
		InputStreamReader insR = null;
		BufferedReader br = null;
		try {
			if (null != datamapIns) {

				StringBuffer sb = new StringBuffer();
				insR = new InputStreamReader(datamapIns, "gbk");
				br = new BufferedReader(insR);

				String aLine = null;

				while (null != (aLine = br.readLine())) {
					sb.append(aLine);
					sb.append("\n");
				}

				return new DataMap(sb.toString());
			}

		} catch (DataMapResourceNotValidException e) {
			e.printStackTrace();
		//	this.logger.error("datamap.xml 语法错误", e);
		} catch (DocumentException e) {
			e.printStackTrace();
		//	this.logger.error("datamap.xml 语法错误", e);
		} catch (IOException e) {
			e.printStackTrace();
		//	this.logger.error("加载 datamap.xml 错误", e);
		} catch (SAXException e) {
			e.printStackTrace();
		//	this.logger.error("解析 datamap.xml 错误", e);
		} finally {

			try {
				if (null != insR) {
					insR.close();
				}
				if (null != br) {
					br.close();
				}
				if (null != datamapIns) {
					datamapIns.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	

	
	/**
	 * 判断是否是变量
	 * 
	 * @param mayBeAVar
	 * @return
	 */
	private static boolean isVar(String mayBeAVar) {
		if (null != mayBeAVar) {
			if (mayBeAVar.trim().startsWith(TPL_VAR_FLG)) {
				return true;
			}
		}
		return false;
	}

	/**获得公共数据
	 * 
	 * @param val
	 * @return
	 */
	public Map<Object, Object> getCommonData(Map<Object, Object> val) {
		return null;
	}
}
