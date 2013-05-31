package org.record.avtice.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DBkit {
	
	
	
	public static Connection getJFinalConn() {
		return conn;
	}
	static Connection conn = null;
	public static Connection getConn() {
		String DRIVER = "org.gjt.mm.mysql.Driver";
		String HOSTNAME = "localhost";
	    String PORT = "3306";
	    String DBNAME = "lovemie";
	    String USERNAME = "root";
	    String USERPWD = "root";
		
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
	}
	/**
	 * 关闭数据库连接
	 */
	public static void closeConn() {
		try {
			if(conn != null
					&& !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void pstmtSetValue(PreparedStatement pstmt, List<Object> sql_key) {
		int index = 1;
		for(Object key : sql_key) {
			System.out.println("k - v : "+ index +" - "+ key);
			try {
				pstmt.setObject(index, key);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			index ++ ;
		}		
	}
	
	public static String getTableName(Class<?> c) {
//		a - 97
//		z - 122
//		A - 65
//		Z - 90
		StringBuffer tab = new StringBuffer();
		char[] beanNameArr = ClassReflect.getShortName(c.getName()).toCharArray();
		Boolean isTheFirstCh = true;
		for(Character ch : beanNameArr) {
			if(isTheFirstCh) {
				isTheFirstCh = false;
				tab.append(Character.toLowerCase(ch));
			} else {
				int intc = (int)ch;
				if(intc >= 65
						&& intc <=90) {    // 若此字母是大写
					tab.append("_"+ Character.toLowerCase(ch));
				} else {
					tab.append(ch);
				}
			}
		}
//		System.out.println("tab name : "+ tab.toString());
		return tab.toString();		
	}



}
