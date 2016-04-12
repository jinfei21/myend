package com.pingan.jinke.infra.padis.migrate;

import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.core.ClusterManager;

public class MigrateListenerManager extends AbstractListenerManager{



	public MigrateListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterManager clusterManager) {
		super(instance, coordinatorRegistryCenter,clusterManager);
	}

	@Override
	public void start() {
		
	}

}
