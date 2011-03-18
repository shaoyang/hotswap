package com.taobao.baoxian.osgi.check;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.taobao.baoxian.osgi.check.exception.DataMapResourceNotValidException;
import com.taobao.baoxian.utils.conf.Config;

public class DataMap {

	private static Validator validator;
	private Map<String, DataMapItemElement> items = new LinkedHashMap<String, DataMapItemElement>();

	static {
		File schemaFile = new File(Config.RESOURCES_DIR + "\\tpl-datamap-rules.xsd");
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
	 * datamap �е�item Ԫ��,inner class
	 */
	private class DataMapItemElement {

		private Map<String, Object> targetMap = null;
		public DataMapItemElement(String itemName, Map<String, Object> target) {
			super();
			this.targetMap = target;
		}
		public Map<String, Object> getTargetMap() {
			return targetMap;
		}
	}

	/**
	 * 
	 * @param dataMapXML
	 * @throws TPLDataMapResourceNotValidException
	 * @throws DocumentException
	 * @throws SAXException
	 */
	public DataMap(String dataMapXML)
			throws DataMapResourceNotValidException,DocumentException,SAXException {

		// У��
		StringReader sr = new StringReader(dataMapXML);
		StringReader strReader = new StringReader(dataMapXML);
		//xml����
		Source source = new StreamSource(sr);
		SAXReader reader = new SAXReader();
		
		try {
			validator.validate(source);
		} catch (SAXException e) {
			e.printStackTrace();
			throw e;

		} catch (IOException e) {
			e.printStackTrace();
			throw new DataMapResourceNotValidException(
					"The input datamap.xml is not valid!");
		}


		Document document = reader.read(strReader);

		Element root = document.getRootElement();

		List<?> itemList = root.elements("item");

		for (Object itemElem : itemList) {
			Element aItemElem = (Element) itemElem;

			String name = aItemElem.attributeValue("name");
			List<?> mapList = aItemElem.elements("map");

			DataMapItemElement aItemDataMap = new DataMapItemElement(name, this
					.parseItemMap(mapList));
			this.items.put(name, aItemDataMap);
		}
	}

	private Map<String, Object> parseItemMap(List<?> mapList) {
		Map<String, Object> targets = new LinkedHashMap<String, Object>();

		if (null != targets) {
			for (Object map : mapList) {
				Element mapElem = (Element) map;

				Element targetElem = mapElem.element("target");

				// ������target��ǩ���ݹ��ȡ
				if (null != targetElem) {
					targets.put(mapElem.attributeValue("value"), this
							.parseItemMap(targetElem.elements("map")));
				} else {
					targets.put(mapElem.attributeValue("value"), mapElem
							.attributeValue("target"));
				}
			}
		}

		return targets;
	}

	/**
	 * ������ѯ
	 * 
	 * @param itemName ����
	 * @param value
	 * @return
	 */
	public String getTargetByValue(String itemName, String value) {

		DataMapItemElement item = this.items.get(itemName);

		if (null != item) {
			Map<String, Object> maps = item.getTargetMap();
			return (String) maps.get(value);
		}

		return null;
	}

	/**
	 * �༶��ѯ
	 * 
	 * @param itemName ����
	 * @param values
	 * @return
	 */
	public String getTargetByValues(String itemName, String[] values) {

		DataMapItemElement item = this.items.get(itemName);
		Map<?, ?> map = item.getTargetMap();

		for (String value : values) {
			Object target = map.get(value);
			if (target instanceof String) {
				return (String) target;
			} else if (target instanceof Map<?, ?>) {
				map = (Map<?, ?>) target;
				continue;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * getTargetByValue �����ı�����
	 * 
	 * @param itemName
	 * @param value
	 * @return
	 */
	public String target(String itemName, String value) {
		return this.getTargetByValue(itemName, value);
	}

	/**
	 * getTargetByValues �����ı�����
	 * 
	 * @param itemName
	 * @param value
	 * @return
	 */
	public String target(String itemName, String[] values) {
		if (null != values) {
			if (1 == values.length) {
				return this.getTargetByValue(itemName, values[0]);
			} else {
				return this.getTargetByValues(itemName, values);
			}

		} else {
			return "";
		}
	}

	/**
	 * �������ӳ�����
	 * 
	 * @param itemName ������
	 * @return
	 */
	private Map<String, Object> getMapByItemName(String itemName) {

		if (null != itemName) {
			DataMapItemElement item = this.items.get(itemName);
			if (null != item) {
				return item.getTargetMap();
			}
		}
		return null;
	}

	public Map<String, Object> item(String itemName) {
		Map<String, Object> map = this.getMapByItemName(itemName);

		if (null == map) {
			return new LinkedHashMap<String, Object>();
		}

		return map;
	}

	/*
	 * һ����һ����ָ���Ϊ# �����������ָ���Ϊ| ������������ָ���Ϊ, �ļ����ļ���ָ���Ϊ~
	 */
	private final static String[] sameLevelSeparaters = { "#", "|", ",", "~" };

	/*
	 * ������һ����ָ���Ϊ$ �����������ָ���Ϊ% �������ļ���ָ���Ϊ!
	 */
	private final static String[] diffLevelSeparaters = { "$", "%", "!" };

	/**
	 * ת����ǰ�˽����ĸ�ʽ
	 * 
	 * @param maps
	 * @return
	 */
	public static String toJsVar(Map<?, ?>... maps) {
		if (maps.length != 0) {

			// return toJsString(maps[maps.length - 1], maps, 0, new String[]
			// {});
			return toJsString(maps, null, 0);
		}
		return "";
	}

	/**
	 * 
	 * @param allMaps
	 * @param parentValues
	 * @param currLvl ��ǰ���𣬴�0��ʼ
	 * @return
	 */
	public static String toJsString(Map<?, ?>[] allMaps, Object[] parentValues,
			int currLvl) {

		StringBuilder sb = new StringBuilder();

		// ͬ���ָ�����
		String sepInLevel = sameLevelSeparaters[currLvl];
		// ��ͬ����ָ���
		String sepOutLevel = null;
		if (0 < currLvl) {
			sepOutLevel = diffLevelSeparaters[currLvl - 1];
		}

		// ����һ���ķָ���
		if (currLvl > -1 && null != allMaps && allMaps.length > currLvl) {
			if (null != sepOutLevel) {
				sb.append(sepOutLevel);
			}

			// ��λtarget

			Map<?, ?> currMap = allMaps[currLvl];

			if (null != parentValues) {

				for (Object aParentValue : parentValues) {

					Object theTarget = currMap.get(aParentValue);

					if (theTarget instanceof Map<?, ?>) {
						currMap = (Map<?, ?>) theTarget;
					} else {
						// ����
						return "";
						// throw new IllegalArgumentException("ת��js������������");
					}
				}

			}

			for (Object value : currMap.keySet()) {

				sb.append(currMap.get(value) + "_" + value);

				// ������һ��
				if (currLvl < allMaps.length - 1) {

					Object[] nextParentValues = null;

					if (null == parentValues) {
						nextParentValues = new Object[] { value };
					} else {
						nextParentValues = new Object[parentValues.length + 1];
						System.arraycopy(parentValues, 0, nextParentValues, 0,
								parentValues.length);

						nextParentValues[nextParentValues.length - 1] = value;
					}

					sb
							.append(toJsString(allMaps, nextParentValues,
									currLvl + 1));
				}

				sb.append(sepInLevel);
			}

			// ɾ�����һ�����÷ָ��
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}


	public static String toJsString(Map<?, ?>[] maps) {

		StringBuilder sbAll = new StringBuilder();

		if (null != maps) {

			if (maps.length > 0) {
				StringBuilder sb0 = new StringBuilder();
				// �����һ��
				for (Object value0 : maps[0].keySet()) {
					Object target0 = maps[0].get(value0);

				}

				String js0 = sb0.toString();
				if (js0.endsWith(sameLevelSeparaters[0])) {

				} else {

				}
			}

		}

		return sbAll.toString();
	}

	/**
	 * ��datamapת����ǰ��js��ʽ
	 * 
	 * @param map ���һ��map
	 * @param maps ���м����map
	 * @param dataIdx ����
	 * @param parentValues
	 * @return
	 */
	@Deprecated
	private static String toJsString(Map<?, ?> map, Map<?, ?>[] maps,
			int dataIdx, String[] parentValues) {
		StringBuffer sb = new StringBuffer();

		// xml �е� valueԪ����Ϊkey
		for (Object value : map.keySet()) {

			Object target = map.get(value);

			Map<?, ?> mapForFindTarget = maps[dataIdx];

			Object theTarget = target;

			if (null != parentValues) {
				Object currTarget = null;
				if (0 == parentValues.length) {
					currTarget = mapForFindTarget.get(value);
					theTarget = currTarget;
				} else {
					currTarget = mapForFindTarget.get(parentValues[0]);
					for (int i = 1; i < parentValues.length; i++) {
						if (null == currTarget) {
							currTarget = "";
							break;
						}
						currTarget = ((Map<?, ?>) currTarget)
								.get(parentValues[i]);
					}
					theTarget = ((Map<?, ?>) currTarget).get(value);
				}
			}

			// sb.append(((Map<?, ?>) theTarget).get(value) + "_" + value);
			sb.append(theTarget + "_" + value);

			if (target instanceof Map<?, ?>) {
				sb.append(diffLevelSeparaters[dataIdx]);

				String[] newValues = new String[parentValues.length + 1];
				System.arraycopy(parentValues, 0, newValues, 0,
						parentValues.length);
				newValues[newValues.length - 1] = (String) value;
				sb.append(toJsString((Map<?, ?>) target, maps, dataIdx + 1,
						newValues));
			}

			sb.append(sameLevelSeparaters[dataIdx]);
		}

		String retStr = sb.toString();

		if (retStr.endsWith(sameLevelSeparaters[dataIdx])) {
			return retStr.substring(0, retStr.length() - 1);
		}

		return sb.toString();
	}
}

