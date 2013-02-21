package org.record.avtice;

import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ATable<T> {
	
	
	/////////  表信息(或称 bean 信息)  ////////
	static String table = "";								//  保存表名
	static Map<String, String> fields = null;              //  保存所有字段 (name -> type)
	static HashSet<String> fieldNames = null;              //  保存所有字段名

	
	
	
	/////////////////////////////////////////////////
	//			      methods
	/////////////////////////////////////////////////
	
	/**
	 * @return		Model 所有字段
	 */
	abstract Map<String, String> getFields();
	
	/**
	 * @param fieldName		字段名
	 * @return             字段类型
	 * @throws Exception
	 */
	abstract String getType(String fieldName) throws Exception;
	
	/**
	 * 获取对象所有值
	 * 
	 * @param bean    对象实例
	 * @return
	 */
	abstract Map<String, Object> getValues(T bean);
	
	/**
	 * @param bean     对象实例
	 * @param 		   field   字段名
	 * @return	              获取对象指定字段的值
	 */
	abstract Object getValue(T bean, String field);
	
	/**
	 * 取得值放入 Bean 中并返回
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	abstract T get(HttpServletRequest req, HttpServletResponse res);
	
	

	

}
