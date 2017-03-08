package com.yjfei.padis.cluster;

import com.yjfei.padis.JedisxClusterClient;
import com.yjfei.padis.JedisxConfig;

public class main {

	public static void main(String[] args) throws Exception {
		JedisxConfig config = new JedisxConfig();
		
		config.setInstance("testCluster");
		config.setNameSpace("test");
		JedisxClusterClient client = new JedisxClusterClient(config);
		client.set("key1", "value");
		
		System.out.println("-------------------------------");
	}

}
