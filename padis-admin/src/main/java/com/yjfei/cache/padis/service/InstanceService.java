package com.yjfei.cache.padis.service;

import java.util.List;

import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.storage.NodeStorage;

public class InstanceService {

	private NodeStorage nodeStorage;
	
	public InstanceService(CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);		
	}
	
	
	public List<String> getAllInstances(){
		return this.nodeStorage.getNodePathChildrenKeys("/instances");
	}
	
	public boolean isExisted(String instance){
		return this.nodeStorage.isNodePathExisted("/instances/"+instance);
	}
	
	public void addInstance(String instance){
		this.nodeStorage.createNodePathIfNeeded("/instances/"+instance);
	}
}
