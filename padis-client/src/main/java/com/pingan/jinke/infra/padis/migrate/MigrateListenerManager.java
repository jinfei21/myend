package com.pingan.jinke.infra.padis.migrate;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.AbstractNodeListener;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.core.ClusterManager;

public class MigrateListenerManager extends AbstractListenerManager{

	private MigrateService migrateService;

	public MigrateListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterManager clusterManager) {
		super(instance, coordinatorRegistryCenter,clusterManager);
		this.migrateService = new MigrateService(instance,coordinatorRegistryCenter);
	}

	@Override
	public void start() {
		addDataListener(new MigrateStatusListener() , migrateService.getRootMigratePath());
	}

	class MigrateStatusListener extends AbstractNodeListener{

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
			
		}
	}
}
