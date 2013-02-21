package org.record.avtice.i;

import java.util.*;

import com.jfinal.plugin.activerecord.Page;

public interface IRecordActive<T> {

	/////////////////////////////////////////////////
	//		      methods
	/////////////////////////////////////////////////

	// save
	/////////////////////////////////////////////////
	
	/**
	 * save myself
	 * 
	 * @return
	 */
	abstract Integer save();
	
	/**
	 * @param bean    对象实例
	 * @return        操作影响的行数
	 */
	abstract Integer save(T bean);
	
	/**
	 * 保存或者更新
	 * 
	 * @param bean    对象实例
	 * @return        操作影响的行
	 */
	abstract Integer saveOrUpdate(Class<T> bean);
	
	/**
	 * @param beans   对象集
	 * @return        保存各个实例影响的行
	 */
	abstract Map<String, Integer> save(List<T> beans);

	// delete
	/////////////////////////////////////////////////

	abstract Integer delete();
	
	
	/**
	 * @param bean  带 ID 的对象实例
	 * @return      执行删除影响行
	 */
	abstract Integer delete(T bean);
	
	/**
	 * @param id    将要删除行 ID
	 * @return      执行删除影响行
	 */
	abstract Integer delete(Integer id);
	
	abstract Integer delete(String id);
	
	/**
	 * @param ids    ID 集合
	 * @return       执行删除影响行(集合)
	 */
	abstract Map<String, Integer> delete(List<Integer> ids);
	
	/**
	 * @param field    字段名
	 * @param value    字段值 
	 * @return         影响行
	 */
	abstract Integer delete(String field, Object value);
	
	/**
	 * @param param    'WHERE' 参数所需条件
	 * @return         执行删除影响的行
	 */
	abstract Map<String, Integer> delete(Map<String, String> param);

	// update
	/////////////////////////////////////////////////

	
	/**
	 * update myself
	 * 
	 * @return
	 */
	abstract Integer update();
	
	/**
	 * 按指定字段更新
	 * 
	 * @param bean 带值实例
	 * @param field 字段
	 * @return
	 */
	abstract Integer update(T bean, String field);
	
	abstract Map<String, Integer> update(List<T> beans, String field);
	
	/**
	 * @param bean    一个对象实例
	 * @return        保存对象影响的行
	 */
	abstract Integer update(T bean);
	
	/**
	 * @param beans  对象集
	 * @return       key 是传入对象 ID， value 是对象保存时影响的行
	 */
	abstract Map<String, Integer> update(List<T> beans);

	// read (query)
	/////////////////////////////////////////////////

	/**
	 * @param id    关键字
	 * @return      子类对象
	 */
	abstract T get(Integer id);
	
	/**
	 * @param field    字段名
	 * @param value    字段段值
	 * @return         子类对象
	 */
	abstract List<T> get(String field, Object value);
	
	/**
	 * @return    所有表数据对象
	 */
	abstract List<T> get();
	
	/**
	 * 按 params 中所有 key=value 条件查询   <br/>
	 * 
	 * @param params    键值对
	 * @return          对象集
	 */
	abstract List<T> get(Map<String, String> params);
	
	/**
	 * field 字段的中值包含于 params 中的所有行
	 * 
	 * @param field    字段名
	 * @param params   值集合
	 * @return         对象集
	 */
	abstract List<T> get(String field, Set<Object> params);
	
	/**
	 * 同 MYSQL 的 select * from tab <b>limit</b> start,offset
	 * 
	 * @param start     起始位置(并不是ID，它是按现在ID升排序)
	 * @param offset    偏移量
	 * @return          对象集
	 */
	abstract List<T> get(Integer start, Integer offset);
	
	abstract Page<?> pager(Integer start, Integer offset);
	
	//  other
	/////////////////////////////////////////////////

	/**
	 * 查询记录总数
	 * 
	 * @return
	 */
	abstract Long count();

}
