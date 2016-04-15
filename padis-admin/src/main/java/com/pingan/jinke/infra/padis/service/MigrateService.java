package com.pingan.jinke.infra.padis.service;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

public class MigrateService {
	
	private NodeStorage nodeStorage;
	
	public MigrateService(String instance,CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);		
	}


	
}
