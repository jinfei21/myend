package com.pingan.jinke.infra.cluster;

import com.pingan.jinke.infra.padis.PadisClusterClient;
import com.yjfei.cache.padis.PadisConfig;

public class main {

	public static void main(String[] args) throws Exception {
		PadisConfig config = new PadisConfig();
		
		config.setInstance("testCluster");
		config.setNameSpace("test");
		PadisClusterClient client = new PadisClusterClient(config);
		client.set("key1", "value");
		
		System.out.println("-------------------------------");
	}

}
