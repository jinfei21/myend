package com.yjfei.cache.padis.util;

public class StringUtils {
	public static boolean isBlank(String str){
		return str==null?true:str.trim().length()==0;
	}
	
	public static boolean isNotBlank(String str){
		return !isBlank(str);
	}
}

