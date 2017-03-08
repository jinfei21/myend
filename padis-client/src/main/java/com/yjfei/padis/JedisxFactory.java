package com.yjfei.padis;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.yjfei.padis.IJedisx;

public class JedisxFactory {

	private static ConcurrentMap<String, IJedisx> cache = Maps.newConcurrentMap();

	private String zkAddr;

	public JedisxFactory(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public IJedisx getJedisxClient(String instance, String namespace, String password) {
		IJedisx jedisClient = this.cache.get(instance);

		if (jedisClient == null) {
			JedisDirectClient newClient = new JedisDirectClient(zkAddr, instance, namespace, password);
			
			IJedisx oldClient = this.cache.putIfAbsent(instance, newClient);
			if(oldClient == null){
				newClient.init();
				jedisClient = newClient;
			}else{
				jedisClient = oldClient;
			}
		}

		jedisClient.setNameSpace(namespace);
		return jedisClient;
	}

}