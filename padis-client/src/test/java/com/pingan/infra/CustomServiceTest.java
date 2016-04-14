package com.pingan.infra;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.common.ZookeeperConfiguration;
import com.pingan.jinke.infra.padis.core.ZookeeperRegistryCenter;
import com.pingan.jinke.infra.padis.custom.Custom;
import com.pingan.jinke.infra.padis.custom.CustomListenerManager;
import com.pingan.jinke.infra.padis.custom.CustomService;
import com.pingan.jinke.infra.padis.group.GroupListenerManager;

public class CustomServiceTest {

	public static void main(String[] args) {
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:2181", "padis", 1000, 3000, 3));
		
		regCenter.init();
		CustomListenerManager listener = new CustomListenerManager("test",regCenter,null);
		listener.start();
		
		
		GroupListenerManager gl = new GroupListenerManager("test",regCenter,null);
		gl.start();
		
		CustomService customService = new CustomService("test",regCenter);
		
		
		customService.registerCustom();
		
		
		System.out.println("fsafsafd");
		
		Custom custom = new Custom();
		custom.setStatus(Status.LIMIT);
		customService.updateCustom(custom);
		
		System.out.println("fsafsafd");
	}

}
