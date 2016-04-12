package com.pingan.jinke.infra.padis.custom;

import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.core.ClusterManager;

public class CustomListenerManager extends AbstractListenerManager{
	

	public CustomListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterManager clusterManager) {
		super(instance, coordinatorRegistryCenter,clusterManager);
	}

	@Override
	public void start() {
		
	}

}
