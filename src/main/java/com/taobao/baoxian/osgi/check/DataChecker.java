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
 * У�������
 */
public class DataChecker {

	private enum LogicIdentifer {
		AND, OR;
	}

	public DataChecker() {
		new CheckParser();
	}

	/**
	 * �������������
	 * 
	 * @param originalData
	 * @param items
	 * @return
	 */
	public String checkIntegrity(Map<Object, Object> originalData,
			List<CheckItem> items) {

		if (null == originalData) {
			return "�û�����Ϊ��";
		}

		for (Iterator<?> itemsIt = items.iterator(); itemsIt.hasNext();) {
			CheckItem theItem = (CheckItem) itemsIt.next();

			if (null == originalData.get(theItem.getItemName())) {

				if (null != theItem.getItemName()) {
					return theItem.getItemName();
				} else {
					return "item name Ϊ��";
				}

			}
		}
		return null;
	}

	/**
	 * У���û�����ı�����
	 * @param originalData	������
	 * @param items			У����
	 * @param customChecker �Զ���У��
	 * @return
	 * @throws TPLCheckConditionLogicException
	 */
	public CheckResult checkData(Map<Object, Object> originalData,
			List<CheckItem> items, OsgiCheck customChecker)
			throws DocumentException, ValidatorNotSupportedException,
			CheckResourceNotValidException, CheckConditionLogicException {

		// ��¼������Ϣ
		List<String> errMsgs = new ArrayList<String>();
		// ��¼��������� errMsgs��δ�ء�һ��һ
		List<String> errItems = new ArrayList<String>();

		for (Iterator<?> itemsIt = items.iterator(); itemsIt.hasNext();) {
			CheckItem theItem = (CheckItem) itemsIt.next();

			// У��ѭ���������У��
			if (true == theItem.isInLoop() && false == theItem.isBound()
					&& false == theItem.isByPattern()) {
				// �Ѽ��������
				Object tempValues = originalData.get(theItem.getItemName());
				if (tempValues instanceof Object[]) {

					Object[] values = (Object[]) tempValues;
					for (int i = 0; i < values.length; i++) {
						String err = null;
						try {
							err = this.checkValue(theItem,
									(String) values[i], originalData, customChecker, i);
						} catch (CheckConditionLogicException e) {
							throw new CheckConditionLogicException("����λ��:"
									+ theItem.getItemName());
						}

						if (null != err && false == err.isEmpty()) {
							errMsgs.add(err);
							errItems.add(theItem.getItemName() + "-" + i);
						}
					}
				}
			}
			// ѭ�����ڵ�����У��
			else if (true == theItem.isInLoop() && true == theItem.isBound()
					&& false == theItem.isByPattern()) {
				// �Ѽ��������
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
						throw new CheckConditionLogicException("����λ��:"
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
				// ��һֵ
				Object value = originalData.get(theItem.getItemName());
				if (null != value) {
					String aErrMsg = null;
					try {
						aErrMsg = this.checkValue(theItem, (String) value,
								originalData, customChecker, null);
					} catch (CheckConditionLogicException e) {
						throw new CheckConditionLogicException("����λ��:"
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
	 * �ж�У����ʽ ֧�ֶ�·����
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

				// ��ʱ���
				Boolean tempResult = null;
				// �����and �� or ��������²����
				if (aCondSnippet instanceof Map<?, ?>) {
					Map<?, ?> aCondSnippetAsMap = (Map<?, ?>) aCondSnippet;
					// ֻ����һά��ӳ���
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
				// ����ǲ������ʽ����м���
				else if (aCondSnippet instanceof BoolExpresion) {
					BoolExpresion aCondSnippetAsBoolExpr = (BoolExpresion) aCondSnippet;

					String left = aCondSnippetAsBoolExpr.getLeft().trim();
					String op = aCondSnippetAsBoolExpr.getOperator().trim();
					String right = aCondSnippetAsBoolExpr.getRight().trim();

					// �� $ ��ͷ��Ϊ����
					if (left.startsWith(CheckParser.TPL_VAR_FLG)) {
						left = (String) data.get(left.substring(1));
					}
					// ѭ�����ڱ���
					else if (left.startsWith("{") && left.endsWith("}")) {
						Object tempValues = data.get(left.substring(1, left
								.length() - 1));
						if (null != tempValues
								&& tempValues instanceof Object[]) {
							Object[] tempValuesArr = (Object[]) tempValues;
							// ��ֹԽ��
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
							// ��ֹԽ��
							if (null != loopIdx
									&& loopIdx.intValue() < tempValuesArr.length) {
								right = (String) tempValuesArr[loopIdx
										.intValue()];
							}
						}
					}

					if (null == left) {
						throw new CheckConditionLogicException("ȱ����ֵ");
					}

					if (null == op) {
						throw new CheckConditionLogicException("ȱ�ٲ�����");
					}

					tempResult = this.caculateBool(left, op, right);
				}

				// �����м���
				if (null == result) {
					result = tempResult;
				} else {
					// �ϼ�Ϊ and
					if (LogicIdentifer.AND == logic) {
						if (false == tempResult) {
							return false;
						} else if (true == tempResult) {
							if (false == result) {
								return false;
							} else {
								// �������һ���жϾ���
								continue;
							}
						} else {
							// �������һ���жϾ���
							continue;
						}
					}
					// �ϼ�Ϊ or
					else if (LogicIdentifer.OR == logic) {
						if (true == tempResult) {
							return true;
						} else if (false == tempResult) {
							if (true == result) {
								return true;
							} else {
								// �������һ���жϾ���
								continue;
							}
						} else {
							// �������һ���жϾ���
							continue;
						}

					}
					// ���ʽ
					else {
						result = tempResult;
					}
				}
			}

			return result;
		}

		// ����Ϊnull ���Ϊ��listʱ����true
		return true;
	}

	/**
	 * �����ж��߼����ʽ
	 * 
	 * @param left
	 * @param op
	 * @param right
	 * @return
	 */
	private boolean caculateBool(String left, String op, String right) {

		// �ж��Ƿ����
		if (op.equalsIgnoreCase("eq")) {
			if (left.equalsIgnoreCase(right)) {
				return true;
			} else {
				return false;
			}
		}
		// �жϲ�����
		else if (op.equalsIgnoreCase("ne")) {
			if (left.equalsIgnoreCase(right)) {
				return false;
			} else {
				return true;
			}
		}
		// > < >= <= ֻ��������
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
		// ������֧�ֵĲ�������
		else {
			return false;
		}
	}

	private String checkBoundValues(CheckItem item, String[] values,
			Map<Object, Object> originalData, OsgiCheck customChecker)
			throws CheckConditionLogicException {

		List<Rule> theRules = item.getRules();

		// ����ÿ��item�µ�ÿ����֤����
		for (Iterator<?> rulesIt = theRules.iterator(); rulesIt.hasNext();) {
			Rule aRule = (Rule) rulesIt.next();

			List<Object> cond = aRule.getCondition();

			boolean condPassed = true;

			// ���������ṹ
			if (null != cond && false == cond.isEmpty()) {
				if (true == this.judgeCondition(aRule.getCondition(),
						originalData, null, null)) {
					condPassed = true;

				} else {
					condPassed = false;
				}
			}
			// �����������ṹ
			else {
				condPassed = true;
			}

			if (true == condPassed) {
				// ������У��
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

				// ת������
				Map<String, String> newAttrs = this.attrsToValues(aRule
						.getAttributes(), originalData, -1);

				if (false == validator.check(newAttrs, values)) {
					return aRule.getErrMsg();
				}
				//������չУ����
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
	 * У�鵥��value
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

		// ����ÿ��item�µ�ÿ����֤����
		for (Iterator<?> rulesIt = theRules.iterator(); rulesIt.hasNext();) {
			Rule aRule = (Rule) rulesIt.next();

			List<Object> cond = aRule.getCondition();

			boolean condPassed = true;

			// ���������ṹ
			if (null != cond && false == cond.isEmpty()) {
				if (true == this.judgeCondition(aRule.getCondition(),
						originalData, null, loopIdx)) {
					condPassed = true;

				} else {
					condPassed = false;
				}
			}
			// �����������ṹ
			else {
				condPassed = true;
			}

			if (true == condPassed) {
				// ������У��
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

				// ת������
				Map<String, String> newAttrs = this.attrsToValues(aRule
						.getAttributes(), originalData, loopIdx);

				if (false == validator.check(newAttrs, value)) {
					return aRule.getErrMsg();
				}
				//������չУ����
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
			//ѭ�����е�����
			else if(null != loopIdx && loopIdx >= 0 && attrValue.startsWith("{") && attrValue.endsWith("}")){
				//���õ�����������������
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
