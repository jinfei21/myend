package com.pingan.jinke.infra.padis.common;

import java.util.Set;

import com.pingan.jinke.infra.padis.core.Client;

public interface PoolManager {
	
	Client lease(HostAndPort node) throws Exception;
	
	void release(Client client);
	
	void releaseClose(Client client) throws Exception;
	
	void closePool(HostAndPort node);
	
	void init(Set<HostAndPort> set);
	
}
