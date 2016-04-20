package com.pingan.jinke.migrate;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.ZookeeperConfiguration;
import com.pingan.jinke.infra.padis.core.ZookeeperRegistryCenter;
import com.pingan.jinke.infra.padis.service.MigrateService;

public class MigrateTest {

	public static void main(String[] args) {


		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:2181", "padis", 1000, 3000, 3));
		
		regCenter.init();
		
		
		
		MigrateService service = new MigrateService(regCenter);
		
		service.delAllMigrateSlot("test");
		
		
	}

}
