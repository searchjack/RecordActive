package org.record.avtice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.record.avtice.i.IDBConn;
import org.record.avtice.i.IRecordActive;
import org.record.avtice.util.BasicType;
import org.record.avtice.util.BeanInjector;
import org.record.avtice.util.ClassReflect;
import org.record.avtice.util.DBkit;

import com.jfinal.plugin.activerecord.Page;

/**
 * <b>对于继承此类的所有子类</b>
 * <b>被视为要持久化的字段具备如下特点：</b>
 * <li/> 在该子类中声明有该字段
 * <li/> 在该子类中有相应 setter/getter 方法              <br/> <br/>
 * <b>满足此条件的都将被视为需要持久化的字段</b>     <br/>
 * <b>字段名为 ID 的字段默认为表主键，系统自增长(在持久化 bean 对象时，不会持久化此字段)</b>    <br/> 
 * 
 * @author searchjack
 *
 * @param <T>
 */
public class RecordActive<T> extends ATable<T> implements IRecordActive<T>, IDBConn {
	
	
	/////////  SQL 半成品  ////////
	static final String TABLE = "{table}";
	static final String S_FIELDS = "{fields}";
	static final String S_VALUES = "{values}";
	static final String D_FIELD = "{field}";
	static final String D_VALUE = "{value}";
	static String SQL_INSERT;
	static String SQL_DELETE;
	static String SQL_UPDATE;
	static String SQL_QUERY;

	public RecordActive() {
		table = getTableName();
		fieldNames = new HashSet<String>();
		fields = getFields();
		
//		pr();
		
		SQL_INSERT = "INSERT INTO "+ table +"("+ S_FIELDS +") VALUES( "+ S_VALUES +" )";
		SQL_DELETE = "DELETE FROM "+ table +" WHERE "+ D_FIELD +"="+ D_VALUE;
		SQL_UPDATE = "UPDATE "+ table +" SET ";
		SQL_QUERY = "SELECT "+ arryToStr() +" FROM "+ table +" ";
		
	}
	
/*	private void pr() {
		System.out.println("fieldNames : ");
		for(String s : fieldNames) {
			System.out.print(" - "+ s);
		}
		System.out.println("fields : ");
		Set<Entry<String, String>> entry = fields.entrySet();
		for(Entry<String, String> e : entry) {
			System.out.println(e.getKey() +" - "+ e.getValue());
		}
	}*/
	private String getTableName() {
//		a - 97
//		z - 122
//		A - 65
//		Z - 90
		StringBuffer tab = new StringBuffer();
		char[] beanNameArr = getShortName(this.getClass().getName()).toCharArray();
		Boolean isTheFirstCh = true;
		for(Character c : beanNameArr) {
			if(isTheFirstCh) {
				isTheFirstCh = false;
				tab.append(Character.toLowerCase(c));
			} else {
				int intc = (int)c;
				if(intc >= 65
						&& intc <=90) {    // 若此字母是大写
					tab.append("_"+ Character.toLowerCase(c));
				} else {
					tab.append(c);
				}
			}
		}
//		System.out.println("tab name : "+ tab.toString());
		return tab.toString();		
	}
	private String arryToStr() {
		StringBuffer fs = new StringBuffer();
		Integer len = fieldNames.size();
		for(String f : fieldNames) {
			fs.append(f);
			if(--len >= 1) {
				fs.append(",");
			}
		}
		return fs.toString();
	}

	


	///////////////////////////////////////////////////////////////////////
	//            利用 reflect 从 bean 获取表内容   --  你无需在意
	///////////////////////////////////////////////////////////////////////
	/**
	 * 获取当前类的子类的所有具有 Getter|Setter 方法了字段 
	 */
	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new HashMap<String, String>();
		@SuppressWarnings("rawtypes")
		Class c = this.getClass();
		Field[] df = c.getDeclaredFields();
		for (Field f : df) {
			if(existGetterSetter(f.getName())) {                     //  存在 getter/setter 的字段才被视作需要持久化的字段
				fields.put(f.getName(), getShortName(f.getType().toString()));
				setFieldNames(f.getName());
			}
		}
		return fields;
	}
	static Method[] ms = null;
	private Boolean existGetterSetter(String field) {
		Boolean get = false;
		Boolean set = false;
		Boolean res = false;
		if(ms == null)
			ms = this.getClass().getMethods();
		
//		System.out.println(" - "+ field);
		
		for(Method m : ms) {

//			System.out.println("*** "+ m.getName() +" - "+ "get"+ field);
//			System.out.println("*** "+ m.getName());
			
			if(get == false
					&& m.getName().equalsIgnoreCase("get"+ field)) {
				
//				System.out.println("= get");
				
				get = true;
			}
			if(set == false
					&& m.getName().equalsIgnoreCase("set"+ field)) {
				
//				System.out.println("= set");
				
				set = true;
			}
			if(set && get) {
				
//				System.out.println("==");
				
				return true;
			}
		}
		return res;
	}
	private void setFieldNames(String fName){
		fieldNames.add(fName);
	}
	private String getShortName(String longName) {
		String[] splitN = longName.split("\\.");
		Integer index = splitN.length - 1;
		return splitN[index];
	}
	
	/**
	 * 获取<b>指定字段<b/>在当前类的子类中的<b>类型<b/>
	 */
	@Override
	public String getType(String fieldName) throws SecurityException, NoSuchFieldException {
		Field f = this.getClass().getDeclaredField(fieldName);
		return getShortName(f.getType().toString());
	}

	
	@Override
	public Object getValue(T bean, String field) {
		Object rtn = null;
		Method[] ms = bean.getClass().getDeclaredMethods();
		for(Method m : ms) {
			String name = m.getName();
			Integer params = m.getParameterTypes().length;
			if(name.startsWith("get")
					&& params <= 0) {                                       // if it's getter method without parameters
				String type = getShortName(m.getReturnType().toString());
				if(BasicType.Contain(type)) {                               // if the method return a basic type value
					if(name.equalsIgnoreCase("get"+ field)) {               // if the the class conatin the field
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
	@Override
	public Map<String, Object> getValues(T bean) {
		Map<String, Object> values = new HashMap<String, Object>();
		if(ms == null)
			ms = bean.getClass().getDeclaredMethods();
		for(Method m : ms) {
			String mName = m.getName();
			Integer params = m.getParameterTypes().length;
			if(mName.startsWith("get")
					&& params <= 0) {                                      // if it's getter method without parameters
				
//				System.out.println(" - "+ m.getName() +" - "+ params);
				
				String type = getShortName(m.getReturnType().toString());
				if(BasicType.Contain(type)) {                              // if the method return a basic type value
					String existFieldName = existGetMethod(mName);
					
//					System.out.println(" - "+ existGetterSetter(mName.substring(3)));
					
					if(existGetterSetter(mName.substring(3))
							&& existFieldName != null
							&& existFieldName.length() > 0) {               // if the the class conatin the field
						
//						System.out.println(" - "+ type);
						
						try {
							Object val = m.invoke(bean);
							values.put(existFieldName, val);
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
	private String existGetMethod(String method) {
		String field = null;
		for (String f : fieldNames) {
//			System.out.println(method.toLowerCase() +" - "+ "get"+ f.toLowerCase());
			if(method.toLowerCase().equalsIgnoreCase("get"+ f.toLowerCase())) {				
				field = f;
				break;
			}
		}
		return field;
	}


	
	///////////////////////////////////////////////////////////////////////
	//             belong to ATable   --  你无需在意
	///////////////////////////////////////////////////////////////////////
	public String getTable() {     // 可有不要
		return table;
	}
	
	
	///////////////////////////////////////////////////////////////////////
	//             belong to IDBconn   --  你无需在意
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//             获取一个 java.sql.Connection   --  你无需在意
	///////////////////////////////////////////////////////////////////////
	static Connection conn = null;
	public Connection getConn() {
		if(conn == null) {
			try {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection("jdbc:mysql://"+ HOSTNAME +":"+ PORT +"/"+ DBNAME, USERNAME, USERPWD);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return conn;
		}
		return conn;
		
		// Deploy to Appfog
//		return DBkit.getConn();

	}
	/**
	 * 关闭数据库连接
	 */
	public void closeConn() {
		try {
			if(conn != null
					&& !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	//                           C R U D  -- 你所需要的
	///////////////////////////////////////////////////////////////////////

	// save
	///////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	@Override
	public Integer save() {
		
		System.out.println("b.save();          -- "+this.getClass()+" 344 line");
		
//		Map<String, Object> values = ClassReflect.getValues(this);
//		Object bean = ClassReflect.setterVal(this.getClass(), values);
//		return save((T)bean);

		return save((T) this);
	}
	@Override
	public Integer save(T bean) {
		Integer rtn = 0;
		Map<String, Object> values = ClassReflect.getValues(bean);
		
//		System.out.println(ClassReflect.getValue(bean, "id"));
//		System.out.println(ClassReflect.getValue(bean, "title"));
//		System.out.println("s : "+ values.size());
		
		Set<Entry<String, Object>> entry = values.entrySet();
		
		StringBuffer sql_fields = new StringBuffer();
		StringBuffer sql_values = new StringBuffer();
		Integer len = entry.size();
		for(Entry<String, Object> e : entry) {
			
//			System.out.println("field : "+ e.getKey() +"  value : "+ e.getValue());
			
			if(e.getKey().equalsIgnoreCase("id")) {         // not save or update the 'id' field
				sql_fields.append("id");
				sql_values.append("null");
				if(--len >= 1) {
					sql_fields.append(",");
					sql_values.append(",");
				}				
			} else {
				sql_fields.append(e.getKey());
				sql_values.append("'"+ e.getValue() +"'");
				if(--len >= 1) {
					sql_fields.append(",");
					sql_values.append(",");
				}
			}
		}
		
		String sql = SQL_INSERT.replace(S_VALUES, sql_values.toString()).replace(S_FIELDS, sql_fields.toString()) + ";";
		showSQL(sql);
		try {
			PreparedStatement pstmt = getConn().prepareStatement(sql);
			rtn = pstmt.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return rtn;
	}

	@Override
	public Map<String, Integer> save(List<T> beans) {
		Map<String, Integer> rtns = new HashMap<String, Integer>();
		for(T t : beans) {
			Integer rtn = save(t);
			String id = getValue(t, "id").toString();
			rtns.put(id, rtn);
		}
		return rtns;
	}

	@Override
	public Integer saveOrUpdate(Class<T> bean) {
		Object id = ClassReflect.getValue(bean, "id");
		if(id != null) {
			Integer intId = Integer.valueOf(id.toString());
			if(intId > 1) {         // 该主键已经存在，执行更新操作
				return update((T) bean);
			} else {                // 创建记录
				return save((T) bean);
			}
		}

		return 0;
	}

	// delete
	///////////////////////////////////////////////////////////////////////

	@Override
	public Integer delete() {
		String id = null;
		id = ClassReflect.getValue(this, "id").toString();
		return delete(id);
	}
	@Override
	public Integer delete(T bean) {
		String val = ClassReflect.getValue(bean, "id").toString();
		Integer rtn = delete(val);
		return rtn;
	}
	@Override
	public Integer delete(Integer id) {
		return delete(id.toString());
	}
	@Override
	public Integer delete(String id) {
		Integer rtn = 0;
		String sql = SQL_DELETE.replace(D_FIELD, "id").replace(D_VALUE, id) + ";";
		showSQL(sql);
		
		try {
			rtn = getConn().prepareStatement(sql).executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rtn;
	}

	@Override
	public Map<String, Integer> delete(List<Integer> ids) {
		Map<String, Integer> rtns = new HashMap<String, Integer>();
		for(Integer i : ids) {
			Integer rtn = delete(i);
			rtns.put(i.toString(), rtn);
		}
		return rtns;
	}
	@Override
	public Integer delete(String field, Object value) {
		Integer rtn = 0;
		String sql = SQL_DELETE.replace(D_FIELD, field).replace(D_VALUE, value.toString()) + ";";
		showSQL(sql);
		
		try {
			PreparedStatement pstmt = getConn().prepareStatement(sql);
			List<Object> val = new ArrayList<Object>();
			val.add(value);
			DBkit.pstmtSetValue(pstmt, val);
			rtn = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rtn;
	}
	@Override
	public Map<String, Integer> delete(Map<String, String> param) {
		Map<String, Integer> rtns = new HashMap<String, Integer>();
		Set<Entry<String, String>> entry = param.entrySet();
		for (Entry<String, String> e : entry) {
			Integer rtn = delete(e.getValue(), e.getKey());
			rtns.put(e.getKey(), rtn);
		}

		return rtns;
	}
	
	// update
	///////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	@Override
	public Integer update() {		
		return update((T) this);
	}
	@Override
	public Integer update(T bean, String field) {

		Map<String, Object> values = getValues((T)bean);
		Integer rtn = 0;
		String id_val = "";                                                //  primary key
		StringBuffer sql_set = new StringBuffer();
		List<Object> sql_key = new ArrayList<Object>();
		Set<Entry<String, Object>> entry = values.entrySet();
		Integer len = entry.size()-1;
		for(Entry<String, Object> e : entry) {
			if(e.getKey().equalsIgnoreCase(field)) {
				id_val = e.getValue().toString();
			} else {                                                //  不/不能更新主键
				String k = e.getKey();
				sql_set.append(k +"=?");
				sql_key.add(values.get(k));
				if(--len >= 1) {
					sql_set.append(", ");
				}
			}
		}
		String sql = SQL_UPDATE + sql_set.toString() + " WHERE "+ field +"='"+ id_val +"' ;";
		showSQL(sql);
		
		PreparedStatement pstmt;
		try {
			pstmt = getConn().prepareStatement(sql);
			
			DBkit.pstmtSetValue(pstmt, sql_key);
			
			rtn = pstmt.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return rtn;
	
	}

	@Override
	public Map<String, Integer> update(List<T> beans, String field) {
		Map<String, Integer> rtns = new HashMap<String, Integer>();
		for(T t : beans) {
			int rtn = update(t, field);
			rtns.put(ClassReflect.getValue(t, field).toString(), rtn);
		}
		return rtns;
	}

	@Override
	public Integer update(T bean) {
		return update(bean, "id");
	}

	@Override
	public Map<String, Integer> update(List<T> beans) {
		Map<String, Integer> rtns = new HashMap<String, Integer>();
		for(T t : beans) {
			Integer rtn = update(t);
			rtns.put(getValue(t, "id").toString(), rtn);
		}

		return rtns;
	}

	// read ( query )
	///////////////////////////////////////////////////////////////////////

	@Override
	public T get(Integer id) {
		for(T t : get("id", id)) {			
			return t;
		}
		
		return null;
	}

	@Override
	public List<T> get(String field, Object value) {
		String sql = SQL_QUERY +" WHERE "+ field +"='"+ value +"' ;"; 
		showSQL(sql);
		
		ResultSet rs = null;
		Map<String, Object> val = new HashMap<String, Object>();
		List<T> rtns = new ArrayList<T>();
		
		Integer index = 0 ;
		Object[] arrName = fieldNames.toArray();
		try {
			rs = getConn().prepareStatement(sql).executeQuery();
			while(rs.next()) {
				Integer count = fieldNames.size();
				for(Integer c=1; c <= count; c++) {
					if(index >= count)
						index = 0;
					val.put(arrName[index++].toString().toLowerCase(), rs.getString(c));
				}
				rtns.add(setterVal(val));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtns;
	}

	/**
	 *  把传入的 Map 转换为一个 RecordActive 子对象
	 * @param val   包含当前子类对象的属性值的集合
	 * @return	          一个当前子类对象实列
	 */
	private T setterVal(Map<String, Object> val) {
		@SuppressWarnings("unchecked")
		T bean = (T) ClassReflect.setterVal(this.getClass(), val);
		
		return bean;
	}
	/**
	 * 按 Setter 方法查找对应字段
	 * 
	 * @param method
	 * @return
	 */
	private String existSetMethod(String method) {		
		String field = null;
		if(method.startsWith("set")) {
			for (String f : fieldNames) {
				if(method.toLowerCase().equalsIgnoreCase("set"+ f.toLowerCase())) {				
					field = f;
					break;
				}
			}
		}
		
		return field;
	}
	@Override
	public List<T> get() {
		List<T> rtns = new ArrayList<T>();
		String sql = SQL_QUERY ;
		showSQL(sql);
		
		ResultSet rs = null;
		HashMap<String, Object> val = null;
		Integer index = 0 ;
		Object[] arrName = fieldNames.toArray();
		try {
			rs = getConn().prepareStatement(sql).executeQuery();
			while(rs.next()) {
				Integer count = fieldNames.size();
				val = new HashMap<String, Object>();
				for(Integer c=1; c <= count; c++) {
					if(index >= count)
						index = 0;
					val.put(arrName[index++].toString().toLowerCase(), rs.getString(c));
				}
				rtns.add(setterVal(val));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtns;
	}

	@Override
	public List<T> get(Map<String, String> params) {
		StringBuffer sql_match = new StringBuffer();
		
		Set<Entry<String, String>> entry = params.entrySet();
		Integer len = params.size();
		for(Entry<String, String> e : entry) {
			sql_match.append(e.getKey() +"='"+ e.getValue() +"' ");
			if(--len >= 1) {
				sql_match.append(" AND ");
			}
		}
		String sql = SQL_QUERY +" WHERE "+ sql_match.toString() +" ; ";
		showSQL(sql);
		
		List<T> rtns = new ArrayList<T>();
		ResultSet rs = null;
		HashMap<String, Object> val = null;
		Integer index = 0 ;
		Object[] arrName = fieldNames.toArray();
		try {
			rs = getConn().prepareStatement(sql).executeQuery();
			while(rs.next()) {
				Integer count = fieldNames.size();
				val = new HashMap<String, Object>();
				for(Integer c=1; c <= count; c++) {
					if(index >= count)
						index = 0;
					val.put(arrName[index++].toString().toLowerCase(), rs.getString(c));
				}
				rtns.add(setterVal(val));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rtns;
	}

	@Override
	public List<T> get(String field, Set<Object> params) {
		List<T> rtns = new ArrayList<T>();
//		Set<Entry<String, String>> entry = params.entrySet();
		for(Object e : params) {
			T rtn = get(field, e.toString()).get(0);
			rtns.add(rtn);
		}
		return rtns;
	}
	
	@Override
	public List<T> get(Integer start, Integer offset) {
		String sql = SQL_QUERY +" LIMIT "+ start +","+ offset;
		showSQL(sql);
		
		List<T> rtns = new ArrayList<T>();
		ResultSet rs = null;
		HashMap<String, Object> val = null;
		Integer index = 0 ;
		Object[] arrName = fieldNames.toArray();
		try {
			rs = getConn().prepareStatement(sql).executeQuery();
			while(rs.next()) {
				Integer count = fieldNames.size();
				val = new HashMap<String, Object>();
				for(Integer c=1; c <= count; c++) {
					if(index >= count)
						index = 0;
					val.put(arrName[index++].toString().toLowerCase(), rs.getString(c));
				}
				rtns.add(setterVal(val));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtns;
	}

	@Override
	public Page<T> pager(Integer currPg, Integer sizePg) {
		Integer totalPage = 0;
		Integer totalRow = null;

		Long count = count();                             // 取得总记录条数
		if(count != null) {
			totalRow = Integer.valueOf(count.toString()); // 总记录条数
		} else {
			totalRow = 0;
		}
				

		if(sizePg <= 0) {								  // 设置总页数
			sizePg = 5;
		}
		totalPage = totalRow / sizePg ;
		if(totalRow % sizePg >= 1) {
			totalPage ++ ;
		}

		if(currPg > totalPage)                                        // 设置当前页
			currPg = totalPage;
		
		Integer offset = sizePg;                                       // 设置查询数据偏移量
		if(currPg * sizePg > totalRow) {
			offset = totalRow;
		}

		Integer start = null;                                         // 设置起始记录
		start = currPg * sizePg - sizePg;

		List<T> s = get(start, offset);
		
		Page<T> pg = new Page<T>(s, currPg, offset, totalPage, totalRow);

		return pg;
	}


	// other
	///////////////////////////////////////////////////////////////////////

	@Override
	public Long count() {
		Long count = null;
		String sql ="SELECT COUNT(*) FROM "+ getTable() +" ; ";
		showSQL(sql);
		
		try {
			PreparedStatement pstmt = getConn().prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getLong(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get(HttpServletRequest req, HttpServletResponse res) {
		return (T) BeanInjector.injectActiveRecord(this.getClass(), req, res);
	}
	
	
	/**
	 * 显示 SQL 语句
	 * @param sql
	 */
	private void showSQL(String sql) {
		System.out.println("SQL :-)  "+ sql);
	}


}
