package com.yjfei.padis.util;

import java.io.InputStream;

public class IOStream {
	
	
	public static InputStream getResourceAsStream(String path){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		if(classLoader == null){
			classLoader = IOStream.class.getClassLoader();
		}
		return classLoader.getResourceAsStream(path);
	}
}
