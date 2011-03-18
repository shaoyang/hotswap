package com.taobao.baoxian.osgi.check;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import com.taobao.baoxian.osgi.OsgiCheck;
import com.taobao.baoxian.osgi.check.exception.CheckConditionLogicException;
import com.taobao.baoxian.osgi.check.exception.CheckResourceNotValidException;
import com.taobao.baoxian.osgi.check.exception.ValidatorNotSupportedException;
import com.taobao.baoxian.osgi.check.validator.CheckResult;
import com.taobao.baoxian.osgi.check.validator.TPLValidator;
import com.taobao.baoxian.osgi.check.validator.TPLValidatorEx;
import com.taobao.baoxian.osgi.check.validator.ValidatorFactory;



/**
 * 校验表单数据
 */
public class DataChecker {

	private enum LogicIdentifer {
		AND, OR;
	}

	public DataChecker() {
		new CheckParser();
	}

	/**
	 * 检查数据完整性
	 * 
	 * @param originalData
	 * @param items
	 * @return
	 */
	public String checkIntegrity(Map<Object, Object> originalData,
			List<CheckItem> items) {

		if (null == originalData) {
			return "用户数据为空";
		}

		for (Iterator<?> itemsIt = items.iterator(); itemsIt.hasNext();) {
			CheckItem theItem = (CheckItem) itemsIt.next();

			if (null == originalData.get(theItem.getItemName())) {

				if (null != theItem.getItemName()) {
					return theItem.getItemName();
				} else {
					return "item name 为空";
				}

			}
		}
		return null;
	}

	/**
	 * 校验用户输入的表单数据
	 * @param originalData	表单数据
	 * @param items			校验项
	 * @param customChecker 自定义校验
	 * @return
	 * @throws TPLCheckConditionLogicException
	 */
	public CheckResult checkData(Map<Object, Object> originalData,
			List<CheckItem> items, OsgiCheck customChecker)
			throws DocumentException, ValidatorNotSupportedException,
			CheckResourceNotValidException, CheckConditionLogicException {

		// 记录出错信息
		List<String> errMsgs = new ArrayList<String>();
		// 记录出错表单，和 errMsgs“未必”一对一
		List<String> errItems = new ArrayList<String>();

		for (Iterator<?> itemsIt = items.iterator(); itemsIt.hasNext();) {
			CheckItem theItem = (CheckItem) itemsIt.next();

			// 校验循环体的内容校验
			if (true == theItem.isInLoop() && false == theItem.isBound()
					&& false == theItem.isByPattern()) {
				// 搜集相关数据
				Object tempValues = originalData.get(theItem.getItemName());
				if (tempValues instanceof Object[]) {

					Object[] values = (Object[]) tempValues;
					for (int i = 0; i < values.length; i++) {
						String err = null;
						try {
							err = this.checkValue(theItem,
									(String) values[i], originalData, customChecker, i);
						} catch (CheckConditionLogicException e) {
							throw new CheckConditionLogicException("错误位置:"
									+ theItem.getItemName());
						}

						if (null != err && false == err.isEmpty()) {
							errMsgs.add(err);
							errItems.add(theItem.getItemName() + "-" + i);
						}
					}
				}
			}
			// 循环体内的联合校验
			else if (true == theItem.isInLoop() && true == theItem.isBound()
					&& false == theItem.isByPattern()) {
				// 搜集相关数据
				Object tempValues = originalData.get(theItem.getItemName());

				if (tempValues instanceof Object[]) {

					Object[] values = (Object[]) tempValues;
					String[] strValues = new String[values.length];

					for (int i = 0; i < strValues.length; i++) {
						strValues[i] = String.valueOf(values[i]);
					}

					String err = null;
					try {
						err = this.checkBoundValues(theItem, strValues,
								originalData, customChecker);
					} catch (CheckConditionLogicException e) {
						throw new CheckConditionLogicException("错误位置:"
								+ theItem.getItemName());
					}

					if (null != err) {
						errMsgs.add(err);
						for (int i = 0; i < values.length; i++) {
							if (null != err && false == err.isEmpty()) {
								errItems.add(theItem.getItemName() + "#" + i);
							}
						}
					}
				}
			} else {
				// 单一值
				Object value = originalData.get(theItem.getItemName());
				if (null != value) {
					String aErrMsg = null;
					try {
						aErrMsg = this.checkValue(theItem, (String) value,
								originalData, customChecker, null);
					} catch (CheckConditionLogicException e) {
						throw new CheckConditionLogicException("错误位置:"
								+ theItem.getItemName());
					}
					if (null != aErrMsg) {
						errMsgs.add(aErrMsg);
						errItems.add(theItem.getItemName());
					}
				}
			}
		}

		if (errItems.isEmpty()) {
			return new CheckResult(null, null, true);
		} else {
			return new CheckResult(errMsgs, errItems, false);
		}
	}

	/**
	 * 判断校验表达式 支持断路操作
	 * 
	 * @param cond
	 * @param data
	 * @param loopIdx TODO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean judgeCondition(List<Object> cond, Map<Object, Object> data,
			LogicIdentifer logic, Integer loopIdx)
			throws CheckConditionLogicException {

		if (null != cond) {
			Boolean result = null;

			for (Iterator<Object> condIt = cond.iterator(); condIt.hasNext();) {
				Object aCondSnippet = condIt.next();

				// 临时结果
				Boolean tempResult = null;
				// 如果是and 或 or 则继续向下层遍历
				if (aCondSnippet instanceof Map<?, ?>) {
					Map<?, ?> aCondSnippetAsMap = (Map<?, ?>) aCondSnippet;
					// 只能是一维的映射表
					if (1 < aCondSnippetAsMap.size()) {
						throw new CheckConditionLogicException(
								"Logic Map data must has one element only!");
					}

					if (aCondSnippetAsMap.containsKey("and")) {
						List<?> theAndValue = (List<?>) aCondSnippetAsMap
								.get("and");
						tempResult = this.judgeCondition(
								(List<Object>) theAndValue, data,
								LogicIdentifer.AND, loopIdx);
					} else if (aCondSnippetAsMap.containsKey("or")) {
						List<?> theOrValue = (List<?>) aCondSnippetAsMap
								.get("or");
						tempResult = this.judgeCondition(
								(List<Object>) theOrValue, data,
								LogicIdentifer.OR, loopIdx);

					} else {
						throw new CheckConditionLogicException(
								"Then Element of Logic Map must only contains 'and' or 'or'!");
					}

				}
				// 如果是布尔表达式则进行计算
				else if (aCondSnippet instanceof BoolExpresion) {
					BoolExpresion aCondSnippetAsBoolExpr = (BoolExpresion) aCondSnippet;

					String left = aCondSnippetAsBoolExpr.getLeft().trim();
					String op = aCondSnippetAsBoolExpr.getOperator().trim();
					String right = aCondSnippetAsBoolExpr.getRight().trim();

					// 以 $ 开头则为变量
					if (left.startsWith(CheckParser.TPL_VAR_FLG)) {
						left = (String) data.get(left.substring(1));
					}
					// 循环体内变量
					else if (left.startsWith("{") && left.endsWith("}")) {
						Object tempValues = data.get(left.substring(1, left
								.length() - 1));
						if (null != tempValues
								&& tempValues instanceof Object[]) {
							Object[] tempValuesArr = (Object[]) tempValues;
							// 防止越界
							if (null != loopIdx
									&& loopIdx.intValue() < tempValuesArr.length) {
								left = (String) tempValuesArr[loopIdx
										.intValue()];
							}
						}
					}

					if (right.startsWith(CheckParser.TPL_VAR_FLG)) {
						right = (String) data.get(right.substring(1));
					} else if (right.startsWith("{") && right.endsWith("}")) {
						Object tempValues = data.get(right.substring(1, right
								.length() - 1));
						if (null != tempValues
								&& tempValues instanceof Object[]) {
							Object[] tempValuesArr = (Object[]) tempValues;
							// 防止越界
							if (null != loopIdx
									&& loopIdx.intValue() < tempValuesArr.length) {
								right = (String) tempValuesArr[loopIdx
										.intValue()];
							}
						}
					}

					if (null == left) {
						throw new CheckConditionLogicException("缺少左值");
					}

					if (null == op) {
						throw new CheckConditionLogicException("缺少操作符");
					}

					tempResult = this.caculateBool(left, op, right);
				}

				// 处理中间结果
				if (null == result) {
					result = tempResult;
				} else {
					// 上级为 and
					if (LogicIdentifer.AND == logic) {
						if (false == tempResult) {
							return false;
						} else if (true == tempResult) {
							if (false == result) {
								return false;
							} else {
								// 结果由下一个判断决定
								continue;
							}
						} else {
							// 结果由下一个判断决定
							continue;
						}
					}
					// 上级为 or
					else if (LogicIdentifer.OR == logic) {
						if (true == tempResult) {
							return true;
						} else if (false == tempResult) {
							if (true == result) {
								return true;
							} else {
								// 结果由下一个判断决定
								continue;
							}
						} else {
							// 结果由下一个判断决定
							continue;
						}

					}
					// 表达式
					else {
						result = tempResult;
					}
				}
			}

			return result;
		}

		// 条件为null 后的为空list时返回true
		return true;
	}

	/**
	 * 用来判读逻辑表达式
	 * 
	 * @param left
	 * @param op
	 * @param right
	 * @return
	 */
	private boolean caculateBool(String left, String op, String right) {

		// 判断是否相等
		if (op.equalsIgnoreCase("eq")) {
			if (left.equalsIgnoreCase(right)) {
				return true;
			} else {
				return false;
			}
		}
		// 判断不等于
		else if (op.equalsIgnoreCase("ne")) {
			if (left.equalsIgnoreCase(right)) {
				return false;
			} else {
				return true;
			}
		}
		// > < >= <= 只能是数字
		else if (op.equalsIgnoreCase("lt") || op.equalsIgnoreCase("gt")
				|| op.equalsIgnoreCase("le") || op.equalsIgnoreCase("ge")) {

			int leftInt = 0;
			int rightInt = 0;

			try {

				leftInt = Integer.parseInt(left);
				rightInt = Integer.parseInt(right);

			} catch (NumberFormatException ex) {
				return false;
			}

			if (op.equalsIgnoreCase("lt")) {
				return (leftInt < rightInt);
			} else if (op.equalsIgnoreCase("gt")) {
				return (leftInt > rightInt);
			} else if (op.equalsIgnoreCase("le")) {
				return (leftInt <= rightInt);
			} else if (op.equalsIgnoreCase("ge")) {
				return (leftInt >= rightInt);
			} else {
				return false;
			}
		}
		// 其他不支持的操作符号
		else {
			return false;
		}
	}

	private String checkBoundValues(CheckItem item, String[] values,
			Map<Object, Object> originalData, OsgiCheck customChecker)
			throws CheckConditionLogicException {

		List<Rule> theRules = item.getRules();

		// 遍历每个item下的每个验证规则
		for (Iterator<?> rulesIt = theRules.iterator(); rulesIt.hasNext();) {
			Rule aRule = (Rule) rulesIt.next();

			List<Object> cond = aRule.getCondition();

			boolean condPassed = true;

			// 存在条件结构
			if (null != cond && false == cond.isEmpty()) {
				if (true == this.judgeCondition(aRule.getCondition(),
						originalData, null, null)) {
					condPassed = true;

				} else {
					condPassed = false;
				}
			}
			// 不存在条件结构
			else {
				condPassed = true;
			}

			if (true == condPassed) {
				// 真正的校验
				TPLValidator validator = null;

				if (aRule.getName().equalsIgnoreCase("custom")) {

					validator = ValidatorFactory
							.createCustomValidator(customChecker
									.getCustomValidators().get(
											aRule.getAttributes().get(
													"myCustomCheckDate")));

				} else {
					validator = ValidatorFactory.createValidator(aRule
							.getName());
				}

				// 转换属性
				Map<String, String> newAttrs = this.attrsToValues(aRule
						.getAttributes(), originalData, -1);

				if (false == validator.check(newAttrs, values)) {
					return aRule.getErrMsg();
				}
				//存在扩展校验则
				else if(validator instanceof TPLValidatorEx && false == ((TPLValidatorEx)validator).check(newAttrs, originalData)){
					return aRule.getErrMsg();					
				}
				else  {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * 校验单个value
	 * 
	 * @param item
	 * @param value
	 * @param originalData
	 * @param customChecker TODO
	 * @param loopIdx TODO
	 * @return
	 * @throws TPLCheckConditionLogicException
	 */
	private String checkValue(CheckItem item, String value,
			Map<Object, Object> originalData, OsgiCheck customChecker,
			Integer loopIdx) throws CheckConditionLogicException {

		List<Rule> theRules = item.getRules();

		// 遍历每个item下的每个验证规则
		for (Iterator<?> rulesIt = theRules.iterator(); rulesIt.hasNext();) {
			Rule aRule = (Rule) rulesIt.next();

			List<Object> cond = aRule.getCondition();

			boolean condPassed = true;

			// 存在条件结构
			if (null != cond && false == cond.isEmpty()) {
				if (true == this.judgeCondition(aRule.getCondition(),
						originalData, null, loopIdx)) {
					condPassed = true;

				} else {
					condPassed = false;
				}
			}
			// 不存在条件结构
			else {
				condPassed = true;
			}

			if (true == condPassed) {
				// 真正的校验
				TPLValidator validator = null;

				if (aRule.getName().equalsIgnoreCase("custom")) {
					if (null != customChecker) {
						if (null != aRule.getAttributes()
								&& null != aRule.getAttributes().get(
										"validatorName")) {

							String customeValdName = aRule.getAttributes().get(
									"validatorName");

							Map<String,TPLValidator> validers = customChecker.getCustomValidators(); 
							
							TPLValidator rawValidator = validers.get(customeValdName);

							validator = ValidatorFactory
									.createCustomValidator(rawValidator);
						}
					} else {
						validator = null;
					}
				} else {
					validator = ValidatorFactory.createValidator(aRule
							.getName());
				}

				// 转换属性
				Map<String, String> newAttrs = this.attrsToValues(aRule
						.getAttributes(), originalData, loopIdx);

				if (false == validator.check(newAttrs, value)) {
					return aRule.getErrMsg();
				}
				//存在扩展校验则
				else if(validator instanceof TPLValidatorEx && false == ((TPLValidatorEx)validator).check(newAttrs, originalData)){
					return aRule.getErrMsg();					
				}
			}
		}

		return null;
	}

	private Map<String, String> attrsToValues(Map<String, String> attrs,
			Map<Object, Object> itemData, Integer loopIdx) {

		Map<String, String> retAttrs = new LinkedHashMap<String, String>();

		for (String attrName : attrs.keySet()) {
			String attrValue = attrs.get(attrName);
			if (attrValue.startsWith(CheckParser.TPL_VAR_FLG)) {

				Object v = (String) itemData.get(attrValue.substring(1));
				if (null == v) {
					v = "";
				}
				retAttrs.put(attrName, (String) v);
			}  
			//循环体中的属性
			else if(null != loopIdx && loopIdx >= 0 && attrValue.startsWith("{") && attrValue.endsWith("}")){
				//引用的属性名（变量名）
				String refAttrName = attrValue.substring(1, attrValue.length()-1);
				Object refAttrValueGroup = itemData.get(refAttrName);
				
				if(refAttrValueGroup instanceof Object[]){
					Object[] refAttrValues = (Object[])refAttrValueGroup;
					if(refAttrValues.length > loopIdx){
						retAttrs.put(attrName, String.valueOf(refAttrValues[loopIdx]));
					}
				}
			}
			else {
				retAttrs.put(attrName, attrValue);
			}
		}
		return retAttrs;
	}


}
