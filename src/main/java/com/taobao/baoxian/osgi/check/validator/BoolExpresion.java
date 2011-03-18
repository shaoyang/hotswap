package com.taobao.baoxian.osgi.check.validator;


/**
 * <boolexpr left="" right="" op="" />
 */
public class BoolExpresion {
	
	/**
	 */
	public static enum OP {
		ne("!="), eq("=="), gt(">"), lt("<"), ge(">="), le("<="), checked(
				"checked");

		private String jsOP = null;

		private OP(String jsOP) {
			this.jsOP = jsOP;
		}

		public String getJslOP() {
			return this.jsOP;
		}
		
		/**
		 * ���ַ�ת���ɶ�Ӧ��ö������
		 * @param str
		 * @return
		 */
		public static OP strToEnum(String str ){
			if("ne".equals(str)){
				return OP.ne;
			}
			else if("eq".equals(str)){
				return OP.eq;
			}
			else if("gt".equals(str)){
				return OP.gt;
			}
			else if("lt".equals(str)){
				return OP.lt;
			}
			else if("ge".equals(str)){
				return OP.ge;
			}
			else if("le".equals(str)){
				return OP.le;
			}
			else if("checked".equals(str)){
				return OP.checked;
			}
			return null;
		}
		
	}

	//��ֵ
	private String left = null;
	//�ַ����͵Ĳ�����
	private String operator = null;
	//��ֵ
	private String right = null;
	//��ֵ�Ƿ����ⲿ������ֻ����ѭ����������
	private boolean leftAtOuter = false;
	//��ֵ�Ƿ����ⲿ������ֻ����ѭ����������
	private boolean rightAtOuter = false;
	
	//js��ʽ�Ĳ�����
	private String jsOP = null;
	
	public BoolExpresion(String left, String operator, String right) {
		this.left = left;
		this.operator = operator;
		
		if(null != operator){
			OP tempOP = OP.strToEnum(operator);
			if(null != tempOP){
				this.jsOP = tempOP.getJslOP();
			}
		}
		
		this.right = right;
	}

	
	@Override
	public String toString() {
		StringBuffer rtsb = new StringBuffer();

		rtsb.append(" BoolExpresion : { ");
		rtsb.append(" ");
		rtsb.append(this.left);
		rtsb.append(" ");
		rtsb.append(this.operator);
		rtsb.append(" ");
		rtsb.append(this.right);
		rtsb.append(" }");

		return rtsb.toString();
	}

	public String getLeft() {
		return left;
	}

	public String getOperator() {
		return operator;
	}

	public String getRight() {
		return right;
	}

	public boolean isLeftAtOuter() {
		return leftAtOuter;
	}

	public void setLeftAtOuter(boolean leftAtOuter) {
		this.leftAtOuter = leftAtOuter;
	}

	public boolean isRightAtOuter() {
		return rightAtOuter;
	}

	public void setRightAtOuter(boolean rightAtOuter) {
		this.rightAtOuter = rightAtOuter;
	}

	public String getJsOP() {
		return jsOP;
	}

}