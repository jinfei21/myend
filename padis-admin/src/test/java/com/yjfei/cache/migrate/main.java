package com.yjfei.cache.migrate;

import com.yjfei.cache.padis.IPadis;
import com.yjfei.cache.padis.PadisDirectClient;

public class main {

	public static void main(String[] args) throws Exception {

		String zkAddr = "localhost:2181";
		String instance = "test";
		String namespace = "ns";
		IPadis padis =  new PadisDirectClient(zkAddr, instance, namespace);
		
		padis.set("key", "value");
		
		padis.get("key");
		
		padis.delete("key");

		padis.close();
		
	}

}
