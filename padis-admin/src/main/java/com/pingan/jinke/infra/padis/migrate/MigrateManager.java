package com.pingan.jinke.infra.padis.migrate;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MigrateManager {

	private NodeStorage nodeStorage;
	private volatile boolean run;
	
	public MigrateManager(CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);	
		this.run = true;
	}
	
	public void postTask(MigrateTask task){
		
	}
	
	public void start(){
		while(run){
			
		}
	}
	
	public void stop(){
		this.run = false;
	}
}
