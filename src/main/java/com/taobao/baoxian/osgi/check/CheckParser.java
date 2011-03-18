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
		//�ڷ������汾���Ȼ��webapp·��,�ڷ�����������war����ʽ������
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
	 * ����ļ�����
	 * @param fileName �ļ���
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
	
	
	//����tpl-check-rules.xsdУ����򣬽�У��ѡ��洢��CheckItem��
	public List<CheckItem> parseCheckResource(String checkResource)
		throws DocumentException, CheckResourceNotValidException, SAXException {

		// 1. У�� check.xml
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
		
		//�������item��ǩ����
		List<?> itemElems = root.elements("item");
		
		// ��ȡ����itemԪ�أ�����Ȼ����ӵ�items
		for (Iterator<?> itemsIt = itemElems.iterator(); itemsIt.hasNext();) {
			Element itemElem = (Element) itemsIt.next();
			String itemName = itemElem.attributeValue("name");
			String errMsg = null;
			List<Rule> itemRules = new ArrayList<Rule>();
			// ��ȡ������Ϣ
			Element errMsgElem = itemElem.element("errmsg");
			if (null != errMsgElem) {
				errMsg = errMsgElem.getTextTrim();
			}
		
			// �����֤����
			//���itemElem�µ�elements
			List<?> itemRuleNodes = itemElem.elements();
			for (Iterator<?> itemRulesIt = itemRuleNodes.iterator(); itemRulesIt
					.hasNext();) {
				Element ruleElem = (Element) itemRulesIt.next();
		
				String ruleName = ruleElem.getName();
				String ruleErrMsg = null;
				Map<String, String> ruleAttrs = new HashMap<String, String>();
				ArrayList<Object> ruleCond = null;
		
				// ����errmsg�ڵ�
				if (false == ruleElem.getName().equalsIgnoreCase("errmsg")) {
		
					// ��ô�����Ϣ
					Element ruleErrMsgElem = ruleElem.element("errmsg");
					if (null != ruleErrMsgElem) {
						ruleErrMsg = ruleErrMsgElem.getTextTrim();
					}
		
					// ��ȡ����
					int ruleAtrrNum = ruleElem.attributeCount();
					if (0 != ruleAtrrNum) {
						for (int i = 0; i < ruleAtrrNum; i++) {
							Attribute aAttr = ruleElem.attribute(i);
							ruleAttrs.put(aAttr.getName(), aAttr
									.getStringValue());
						}
					}
		
					// ��ȡ����
					Element ruleCondElem = ruleElem.element("when");
		
					ruleCond = this.parseConditionElement(ruleCondElem);
		
					// ����Rule����
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
	 * ���������ṹ���ݹ飩
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
					// ����and ��orԪ����Map ���з�װ
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

	//���check.xml��Դ
	public List<CheckItem> getCheckResource(Map<Object, Object> userData) {
		List<CheckItem> items = null;
		//�������Ǻ���
		try {
			//���check.xml�ַ���
			String checkStr = this.getRawString("check.xml");
			//��check.xml�е�vm����������Ⱦ
			String xml = this.renderTPLFromStr(checkStr, userData);
			//������Ⱦ����xml�ļ����õ�У�����
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
	 * �� check.xml ת��ǰ�˿�ʶ���Json��ʽ��
	 * 
	 * @param checkItems
	 * @return
	 */
	public String validResourceToJs(List<CheckItem> checkItems) {

		StringBuffer sb = new StringBuffer();
		sb.append("<script type=\"text/javascript\">");
		sb.append("var Validator_JSON = {");

		for (CheckItem item : checkItems) {

			// ����У��������׷��#B
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
				// ����
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
	 * ת���������ʽ��js
	 * 
	 * @return
	 */
	private static String convtCondToJs(Object aCond, boolean isInLoop) {

		if (null == aCond) {
			return "";
		}

		StringBuffer sb = new StringBuffer();

		// ����������ʽ
		if (aCond instanceof BoolExpresion) {
			BoolExpresion theBoolExpr = (BoolExpresion) aCond;

			if (null != theBoolExpr.getOperator()
					&& null != theBoolExpr.getLeft()) {

				boolean leftIsAVar = isVar(theBoolExpr.getLeft());
				boolean rightIsAVar = isVar(theBoolExpr.getRight());

				sb.append(" ");
				// ������ֵ
				if (true == leftIsAVar) {
					sb.append(theBoolExpr.getLeft());
					/*
					 * if (true == isInLoop && false ==
					 * theBoolExpr.isLeftAtOuter()) { sb.append("#N"); }
					 */

				} else {
					//ѭ�������
					if (theBoolExpr.getLeft().trim().startsWith("{")
							&& theBoolExpr.getLeft().trim().endsWith("}")) {
						sb.append(" " + theBoolExpr.getLeft() + " ");
					}
					//����
					else {
						sb.append(" \"" + theBoolExpr.getLeft() + "\" ");
					}
				}

				// ���������
				if (true == leftIsAVar
						&& "checked".equals(theBoolExpr.getOperator())) {
					sb.append(".checked ");
				} else {
					sb.append(" " + theBoolExpr.getJsOP() + " ");
				}

				// ������ֵ
				// �������checked���ͣ���ֵ����Ч
				/*
				 * if (false == "checked".equals(theBoolExpr.getOperator())) {
				 * // ���� if (true == rightIsAVar) { sb.append(" " +
				 * theBoolExpr.getRight() + " "); } // ���� else { sb.append(" \""
				 * + theBoolExpr.getRight() + "\" "); } }
				 */

				if (true == rightIsAVar) {
					sb.append(" " + theBoolExpr.getRight() + " ");
				}
				else {
					//ѭ�������
					if (theBoolExpr.getRight().trim().startsWith("{")
							&& theBoolExpr.getRight().trim().endsWith("}")) {
						sb.append(" " + theBoolExpr.getRight() + " ");
					} 
					// ����
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

		// ����
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
		// ����û�����
		model.put("datamap", this.getDataMap(userData));

		// װ��ȫ��
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
		//	logger.error("��ȡ��ǰwebapp·������", e);
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
		//	this.logger.error("datamap.xml �﷨����", e);
		} catch (DocumentException e) {
			e.printStackTrace();
		//	this.logger.error("datamap.xml �﷨����", e);
		} catch (IOException e) {
			e.printStackTrace();
		//	this.logger.error("���� datamap.xml ����", e);
		} catch (SAXException e) {
			e.printStackTrace();
		//	this.logger.error("���� datamap.xml ����", e);
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
	 * �ж��Ƿ��Ǳ���
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

	/**��ù�������
	 * 
	 * @param val
	 * @return
	 */
	public Map<Object, Object> getCommonData(Map<Object, Object> val) {
		return null;
	}
}
