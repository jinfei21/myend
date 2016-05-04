package com.pingan.jinke.infra.padis.common;

import java.util.Set;

import com.yjfei.cache.padis.common.HostAndPort;

public interface PoolManager<T> {
	
	T lease(HostAndPort node) throws Exception;
	
	void release(T client);
	
	void releaseClose(T client) throws Exception;
		
	void init(Set<HostAndPort> set,PoolConfig config);
	
}
