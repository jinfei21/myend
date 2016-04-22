package com.pingan.jinke.infra.padis.migrate;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.google.common.collect.Sets;
import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.AbstractNodeListener;

public class RemoteCountDown extends AbstractListenerManager{
	
	private CountDownLatch countDown;
	
	private Set<String> nodeSet;

	public RemoteCountDown(String path,CoordinatorRegistryCenter coordinatorRegistryCenter) {
		super(path, coordinatorRegistryCenter,null);
		this.nodeSet = Sets.newHashSet();
	}

	@Override
	public void start() {		
		addDataListener(new CountDownListener() , instance);
	}
	
	public void fresh(){
		
		nodeSet.clear();
		for(String node:this.nodeStorage.getNodePathChildrenKeys(instance)){
			nodeSet.add(instance+"/"+node);
		}
		if(nodeSet.isEmpty()){
			this.countDown = new CountDownLatch(0);			
		}else{
			this.countDown = new CountDownLatch(1);
		}
	}
	
	public void await(long timeout) throws InterruptedException{		
		this.countDown.await(timeout, TimeUnit.SECONDS);
	}
	
	class CountDownListener extends AbstractNodeListener{

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
			if(Type.NODE_UPDATED == event.getType()||Type.NODE_REMOVED == event.getType()){				
				nodeSet.remove(path);
				if(nodeSet.isEmpty()){
					countDown.countDown();
				}
			}
		}
	}
}
