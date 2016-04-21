package com.pingan.infra;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.common.ZookeeperConfiguration;
import com.pingan.jinke.infra.padis.custom.CustomListenerManager;
import com.pingan.jinke.infra.padis.node.Slot;
import com.pingan.jinke.infra.padis.slot.SlotService;
import com.pingan.jinke.infra.padis.storage.ZookeeperRegistryCenter;

public class CustomServiceTest {

	public static void main(String[] args) {
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:2181", "padis", 1000, 3000, 3));
		
		regCenter.init();
		CustomListenerManager listener = new CustomListenerManager("test",regCenter,null);
		listener.start();
		
//		
//		GroupListenerManager gl = new GroupListenerManager("test",regCenter,null);
//		gl.start();
//		
//		CustomService customService = new CustomService("test",regCenter);
//		
//		
//		customService.registerCustom();
//		
//		
//		System.out.println("fsafsafd");
//		
//		Custom custom = new Custom();
//		custom.setStatus(Status.LIMIT);
//		customService.updateCustom(custom);
		
		
		SlotService slotService = new SlotService("test", regCenter);
		
		for(int i=0;i<=1023;i++){
			Slot slot = new Slot();
			
			slot.setId(i);
			slot.setCreate(System.currentTimeMillis());
			slot.setStatus(Status.ONLINE);
			slot.setSrc_gid(6);

			
			slotService.setSlot(slot);
		}
		
		
		System.out.println("fsafsafd");
	}

}
