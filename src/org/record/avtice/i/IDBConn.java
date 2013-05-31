package org.record.avtice.i;

/**
 * if u nedd, u can load this info from .properties
 * @author searchjack
 *
 */
public interface IDBConn {

	/////////////////////////////////////////////////
	//			      fields
	/////////////////////////////////////////////////
	String DRIVER = "org.gjt.mm.mysql.Driver";
	String HOSTNAME = "127.0.0.1";
    String PORT = "3306";
    String USERNAME = "root";
    String USERPWD = "root";
    String DBNAME = "blg";
    
    
	/////////////////////////////////////////////////
	//			      methods
	/////////////////////////////////////////////////
    java.sql.Connection getConn();
    void closeConn();

}
