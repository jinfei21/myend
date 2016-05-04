package com.pingan.jinke.migrate;

import com.pingan.jinke.infra.padis.PadisFactory;
import com.yjfei.cache.padis.IPadis;
import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.common.ZookeeperConfiguration;
import com.yjfei.cache.padis.service.MigrateService;
import com.yjfei.cache.padis.storage.ZookeeperRegistryCenter;

public class MigrateTest {

	public static void main(String[] args) {


		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:2181", "padis", 1000, 3000, 3));
		
		regCenter.init();
		
		
		
		MigrateService service = new MigrateService(regCenter);
		
		service.delAllMigrateSlot("test");
		
		PadisFactory factory = new PadisFactory("localhost:2181");
		IPadis padis = factory.getPadisClient("test", "ns");
		
		
		System.out.println("fsafsafasf");
		
		
	}

}
