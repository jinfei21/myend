package com.pingan.jinke.infra.padis.migrate;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

public class MigrateService {

	private MigrateNode migrateNode;
	
	private NodeStorage nodeStorage;
	
	public MigrateService(String instance,CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
		this.migrateNode = new MigrateNode(instance);
		
	}
	
	
	public String getRootMigratePath(){
		return this.migrateNode.getRootMigratePath();
	}
	
	
	
	
}
