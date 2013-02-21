package org.record.avtice.util;

import static org.record.avtice.util.BasicType.BOOLEAN;
import static org.record.avtice.util.BasicType.BYTE;
import static org.record.avtice.util.BasicType.CHARACTER;
import static org.record.avtice.util.BasicType.DOUBLE;
import static org.record.avtice.util.BasicType.FLOAT;
import static org.record.avtice.util.BasicType.INTEGER;
import static org.record.avtice.util.BasicType.LONG;
import static org.record.avtice.util.BasicType.SHORT;
import static org.record.avtice.util.BasicType.STRING;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassReflect {

	/**
	 *  把传入的 Map 转换为一个 RecordActive 子对象
	 * @param val   包含当前子类对象的属性值的集合 ()
	 * @return	          一个当前子类对象实列
	 */
	public static <T> T setterVal(Class<T> c, Map<String, Object> val) {
		
		T bean = null;
		try {
			bean = (T) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Method[] ms = null;
		if(ms == null)
			ms = c.getDeclaredMethods();
		for (Method m : ms) {
			String name = m.getName();
			if(name.startsWith("set")) {                                       // 是一个 setter 方法
				if(existSetMethod(c, name) != null) {                         // 该 setter 方法所处理的字段是需要被持久化的
					if(bean != null
							&& existSetMethod(c, name) != null) {              // 该 setter 方法指向的字段是需要被持久化的
// TODO  当执行 reflect 操作时， 按方法参数类型传入需要的类型
						try {
							
//							System.out.println("m type : "+ m.getParameterTypes()[0]);
							
							String para = null;                         // 参数
							Object objValue = val.get(name.substring(3).toLowerCase());
							if(objValue != null) {
								para = objValue.toString();
							}
							
							// Short|short
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(SHORT)) {																															
								m.invoke(bean, new Object[]{Short.valueOf(para)});
							}
							// Long|long
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(LONG)) {																															
								m.invoke(bean, new Object[]{Long.valueOf(para)});
							}
							// Float|float
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(FLOAT)) {																															
								m.invoke(bean, new Object[]{Float.valueOf(para)});
							}
							// double
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(DOUBLE)) {																															
								m.invoke(bean, new Object[]{Double.valueOf(para)});
							}
							// Character|char
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(CHARACTER)) {																							
								m.invoke(bean, new Object[]{Character.valueOf(para.charAt(0))});
							}
							// Byte|byte
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(BYTE)) {																
								m.invoke(bean, new Object[]{Byte.valueOf(para)});
							}
							// Boolean | boolean
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(BOOLEAN)) {								
								m.invoke(bean, new Object[]{Boolean.valueOf(para)});
							}
							
//							System.out.println("***"+ m.getParameterTypes().length);
							
							// String
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(STRING)) {
//								System.out.println("type : str" + "  require : "+ m.getParameterTypes()[0]);
								
								if(para == null
										|| para.length() <= 0) {
									m.invoke(bean, new Object[]{""});
								} else {
									m.invoke(bean, new Object[]{para});									
								}
//								m.invoke(bean, new Object[]{"str"});
							}
							// Integer|int
							if(m.getParameterTypes()[0].toString().toLowerCase().contains(INTEGER)) {
//								System.out.println("type : Integer" + "  require : "+ m.getParameterTypes()[0]);
//								System.out.println(name.substring(3).toLowerCase().toString());
//								System.out.println(para);
//								System.out.println(Integer.valueOf(para));
								if(para == null
										|| para.length() <= 0) {
									m.invoke(bean, new Object[]{0});
								} else {
									Integer intPara = null;
									try {
										intPara = Integer.valueOf(para);
									} catch (Exception e) {
										intPara = 0 ;
										e.printStackTrace();
									}
									m.invoke(bean, new Object[]{intPara});
								}
//								m.invoke(bean, new Object[]{120});
							}
							
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
				}
			}
		}
		
		return bean;
	}
	

	/**
	 * @param bean 类实例
	 * @return  有 Getter & Setter 方法字段对应的值
	 */
	public static <T> Map<String, Object> getValues(T bean) {
		Map<String, Object> values = new HashMap<String, Object>();
		Method[] ms = null;
		if(ms == null)
			ms = bean.getClass().getDeclaredMethods();
		for(Method m : ms) {
			String mName = m.getName();
			Integer params = m.getParameterTypes().length;
			if(mName.startsWith("get")
					&& params <= 0) {                                      // if it's getter method without parameters
				
//				System.out.println(" - "+ m.getName() +" - "+ params);
				
				String type = getShortName(m.getReturnType().toString());
				
//				System.out.println(mName +" - "+ type);
				
				if(BasicType.Contain(type)) {                              // if the method return a basic type value
					
//					System.out.println(" type - "+ type);
					
					String existFieldName = existGetMethod(bean.getClass(), mName.substring(3));

//					System.out.println("field exist - "+ existGetterSetter(bean.getClass(), mName.substring(3)));
//					System.out.println("existFieldName - "+ existFieldName);
					
					if(existGetterSetter(bean.getClass(), mName.substring(3))
							&& existFieldName != null
							&& existFieldName.length() > 0) {               // if the the class conatin the field
						
//						System.out.println(" type - "+ type);
						
						try {
							Object val = m.invoke(bean);
							values.put(ClassReflect.getFieldName(mName.substring(3)), val);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return values;
	}
	
	/**
	 * @param c  Class类
	 * @param field 字段
	 * @return 字段对应 set 方法
	 */
	public static String existGetMethod(Class<?> c, String field) {
		Boolean get = false;

		Method[] ms = null;
		if (ms == null)
			ms = c.getMethods();

		// System.out.println(" - "+ field);

		for (Method m : ms) {

			// System.out.println("*** "+ m.getName() +" - "+ "get"+ field);
			// System.out.println("*** "+ m.getName());

			if (get == false && m.getName().equalsIgnoreCase("get" + field)) {

				// System.out.println("= get");

				return m.getName();
			}
		}
		
		return null;
	}

	/**
	 * 得到 set 方法对应字段
	 * 
	 * @param c  Class类
	 * @param method 字段
	 * @return 字段对应 set 方法
	 */
	public static String existSetMethod(Class<?> c, String method) {
		if(method.startsWith("set")) {
			Boolean get = false;
			
			Method[] ms = null;
			if (ms == null)
				ms = c.getMethods();
			
			// System.out.println(" - "+ field);
			
			for (Method m : ms) {
				
				// System.out.println("*** "+ m.getName() +" - "+ "get"+ field);
				// System.out.println("*** "+ m.getName());
				
				if (get == false && m.getName().equalsIgnoreCase(method)) {
					
					// System.out.println("= get");
					
					return m.getName();
				}
			}
		}
		
		return null;
	}
	

	/**
	 * @param bean 对象实例
	 * @param field 字段名
	 * @return 字段值
	 */
	public static <T> Object getValue(T bean, String field) {
		Object rtn = null;
		Method[] ms = bean.getClass().getDeclaredMethods();
		for (Method m : ms) {
			String name = m.getName();
			Integer params = m.getParameterTypes().length;
			if (name.startsWith("get") && params <= 0) { 		// if it's getter method without parameters
				String type = getShortName(m.getReturnType().toString());
				if (BasicType.Contain(type)) { 					// if the method return a basic type value
					if (name.equalsIgnoreCase("get" + field)) { // if the the class conatin the field
						try {
							rtn = m.invoke(bean);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return rtn;
	}
	
	/**
	 * 获取<b>指定字段<b/>在当前类的子类中的<b>类型<b/>
	 * 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	public static String getType(Class<?> c, String fieldName) throws SecurityException, NoSuchFieldException {
		Field f = c.getDeclaredField(fieldName);
		return getShortName(f.getType().toString());
	}
	
	/**
	 * @param field 字段原名
	 * @return
	 */
	public static String getFieldName(String field) {
		StringBuffer t_field = new StringBuffer();
		char[] beanNameArr = field.toCharArray();
		Boolean isTheFirstCh = true;
		
		for (Character ch : beanNameArr) {
			if (isTheFirstCh) {
				isTheFirstCh = false;
				t_field.append(Character.toLowerCase(ch));
			} else {
				int intc = (int) ch;
				if (intc >= 65 && intc <= 90) { // 若此字母是大写
					t_field.append("_" + Character.toLowerCase(ch));
				} else {
					t_field.append(ch);
				}
			}
		}
		
		return t_field.toString();
	}
	
	/**
	 * 类名转换为表名， 形如 ： GoodBoy  ->  good_boy
	 * 
	 * @param c Class对象
	 * @return  数据库样式表
	 */
	public static String getTableName(Class<?> c) {
		// a - 97
		// z - 122
		// A - 65
		// Z - 90
		StringBuffer tab = new StringBuffer();
		char[] beanNameArr = getShortName(c.getName()).toCharArray();
		Boolean isTheFirstCh = true;
		for (Character ch : beanNameArr) {
			if (isTheFirstCh) {
				isTheFirstCh = false;
				tab.append(Character.toLowerCase(ch));
			} else {
				int intc = (int) ch;
				if (intc >= 65 && intc <= 90) { // 若此字母是大写
					tab.append("_" + Character.toLowerCase(ch));
				} else {
					tab.append(ch);
				}
			}
		}
		// System.out.println("tab name : "+ tab.toString());
		return tab.toString();
	}

	/**
	 * 获取有 Getter & Setter 方法字段及类型
	 * @param <T>
	 * 
	 * @param c Class对象
	 * @return  字段名及字段类型
	 */
	public static <T> Map<String, String> getFields(Class<?> c) {
		Map<String, String> fields = new HashMap<String, String>();

		Field[] df = c.getDeclaredFields();
		for (Field f : df) {
			if (existGetterSetter(c, f.getName())) { // 存在 getter & setter 的字段才被视作需要持久化的字段
				fields.put(f.getName().toLowerCase(), getShortName(f.getType().toString()));
			}
		}
		return fields;
	}

	/**
	 * 按 '.' 拆分字串，获取最后一个字串
	 * 
	 * @param longName
	 * @return
	 */
	public static String getShortName(String longName) {
		String[] splitN = longName.split("\\.");
		Integer index = splitN.length - 1;
		return splitN[index];
	}

	/**
	 * 判断字段是否存在相应 Getter & Setter 方法
	 * 
	 * @param c
	 * @param field
	 * @return
	 */
	public static <T> Boolean existGetterSetter(Class<?> c, String field) {
		Boolean get = false;
		Boolean set = false;
		Boolean res = false;

		Method[] ms = null;
		if (ms == null)
			ms = c.getMethods();

		// System.out.println(" - "+ field);

		for (Method m : ms) {

			// System.out.println("*** "+ m.getName() +" - "+ "get"+ field);
			// System.out.println("*** "+ m.getName());

			if (get == false && m.getName().equalsIgnoreCase("get" + field)) {

				// System.out.println("= get");

				get = true;
			}
			if (set == false && m.getName().equalsIgnoreCase("set" + field)) {

				// System.out.println("= set");

				set = true;
			}
			if (set && get) {

				// System.out.println("==");

				return true;
			}
		}
		return res;
	}



}
