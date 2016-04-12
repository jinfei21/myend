package com.pingan.jinke.infra.padis.group;

import java.util.List;

import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.core.ClusterManager;

public class GroupListenerManager extends AbstractListenerManager{

	private GroupService groupService;

	public GroupListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterManager clusterManager) {
		super(instance, coordinatorRegistryCenter,clusterManager);
		this.groupService = new GroupService(coordinatorRegistryCenter);
	}
	
	public List<Group> getAllGroups(){
		return this.groupService.getAllGroups();
	}

	@Override
	public void start() {
		
	}

	
	
}
