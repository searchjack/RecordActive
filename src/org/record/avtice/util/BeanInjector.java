package org.record.avtice.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BeanInjector {
	@SuppressWarnings("unused")
	public static <T> T injectActiveRecord(Class<T> modelClass, HttpServletRequest req, HttpServletResponse res) {

//		System.out.println("*** "+ req);
		
		Map<String, Object> vals = new HashMap<String, Object>();
		
		Map<String, String> fields = ClassReflect.getFields(modelClass);
		Set<Entry<String, String>> entry = fields.entrySet();
		
		Enumeration<?> keys = req.getParameterNames();
		while(keys.hasMoreElements()) {
			Object key = keys.nextElement();
			String[] fieldSplit = key.toString().split("\\.");
			if(fieldSplit.length == 2) {
				String type = fields.get(fieldSplit[1].toLowerCase());
				if(type != null
						&& type.length() >= 1) {      // 该字段是需要被持久化的
					vals.put(fieldSplit[1], req.getParameter(key.toString()));
					
//					System.out.println(fieldSplit[1] +"->"+ req.getParameter(key.toString()));
					
				}
			} else {
			}
//			System.out.println(keys.nextElement());
		}

		return ClassReflect.setterVal(modelClass, vals);

	}
}
