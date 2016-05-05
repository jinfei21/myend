package com.yjfei.cache.padis.common;

public interface Pool<T> {

	T lease() throws Exception;
	
	T lease(long timeout) throws Exception;	
	
	void release(T t);
	
	void releaseClose(T t) throws Exception;
	
	void close();
}
