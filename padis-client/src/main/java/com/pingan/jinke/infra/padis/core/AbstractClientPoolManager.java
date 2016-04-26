package com.pingan.jinke.infra.padis.core;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.pingan.jinke.infra.padis.common.ClusterManager;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.group.GroupListenerManager;
import com.pingan.jinke.infra.padis.node.Group;

public abstract class AbstractClientPoolManager implements ClusterManager {

	protected GroupListenerManager groupListenerManager;

	protected ConcurrentMap<Integer, Group> groupMap;
	
	
	public AbstractClientPoolManager(String instance, final CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.groupListenerManager = new GroupListenerManager(instance, coordinatorRegistryCenter, this);
		this.groupMap = Maps.newConcurrentMap();
	}
	
	public Group getGroup(int gid) {
		return this.groupMap.get(gid);
	}

	public void addGroup(Group group) {
		this.groupMap.put(group.getId(), group);
	}

	public void delGroup(int gid) {
		this.groupMap.remove(gid);
	}
	
	public abstract void closePool(HostAndPort node);
}
