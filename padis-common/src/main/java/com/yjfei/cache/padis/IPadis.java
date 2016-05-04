package com.yjfei.cache.padis;

public interface IPadis {

	String get(final  String key) throws Exception;
	
	String set(final String key, final String value) throws Exception;
	
	Long delete(final  String key) throws Exception;
	
	void setNameSpace(String nameSpace);
	
	void close();
}
