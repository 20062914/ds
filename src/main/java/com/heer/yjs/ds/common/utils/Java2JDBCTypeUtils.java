package com.heer.yjs.ds.common.utils;

import java.lang.reflect.Field;
import java.util.Date;


public class Java2JDBCTypeUtils {
	public static String getJdbcType(Field f){
		Class clazz = f.getType();
		if(String.class.equals(clazz)) return "VARCHAR";
		if(Double.class.equals(clazz)) return "NUMERIC";
		if(Date.class.equals(clazz)) return "DATE";
		return null;
		
	}

}
