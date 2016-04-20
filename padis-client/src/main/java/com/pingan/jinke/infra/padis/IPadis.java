package com.pingan.jinke.infra.padis;

public interface IPadis {

	String get(final  String key) throws Exception;
	
	String set(final String key, final String value) throws Exception;
	
	void setNameSpace(String nameSpace);
}
