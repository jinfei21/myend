package com.pingan.jinke.infra.padis;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.yjfei.cache.padis.IPadis;

public class PadisFactory {

	private static ConcurrentMap<String, IPadis> cache = Maps.newConcurrentMap();

	private String zkAddr;

	public PadisFactory(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public IPadis getPadisClient(String instance, String namespace) {
		IPadis padisClient = this.cache.get(instance);

		if (padisClient == null) {
			PadisDirectClient newClient = new PadisDirectClient(zkAddr, instance, namespace);
			
			IPadis oldClient = this.cache.putIfAbsent(instance, newClient);
			if(oldClient == null){
				newClient.init();
				padisClient = newClient;
			}else{
				padisClient = oldClient;
			}
		}

		padisClient.setNameSpace(namespace);
		return padisClient;
	}

}