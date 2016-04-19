package com.pingan.jinke.infra.padis.migrate;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.AbstractNodeListener;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;

public class RemoteCountDown extends AbstractListenerManager{
	

	public RemoteCountDown(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter) {
		super(instance, coordinatorRegistryCenter,null);
	}

	@Override
	public void start() {
		//addDataListener(new MigrateStatusListener() , migrateService.getRootMigratePath());
	}

	class MigrateStatusListener extends AbstractNodeListener{

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
			if(Type.NODE_UPDATED == event.getType()){
	
			}
		}
	}
}
