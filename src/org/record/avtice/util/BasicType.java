package org.record.avtice.util;

public abstract class BasicType {
	public static String TYPES = "Boolean|boolean|Byte|byte|Character|char|Double|double|Float|float|Integer|int|Long|long|Short|short|String";
	
	public static final String BOOLEAN = "boolean";
	public static final String BYTE = "byte";
	public static final String CHARACTER = "char";
	public static final String DOUBLE = "double";
	public static final String FLOAT  = "float";
	public static final String INTEGER  = "int";
	public static final String LONG  = "long";
	public static final String SHORT  = "short";
	public static final String STRING = "str";
	

	/**
	 * 判断传入的数据类型是不是基本数据类型
	 * 
	 * @param type    数据类型
	 * @return
	 */
	public static Boolean Contain(String type) {
		if(TYPES.contains(type)) {
//			System.out.println("BasicType contain : "+ type);
			return true;
		}
		return false;
	}
}
