package com.yjfei.padis.custom;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.alibaba.fastjson.JSON;
import com.yjfei.padis.common.AbstractListenerManager;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.core.ClusterInfoCacheManager;
import com.yjfei.padis.node.Custom;
import com.yjfei.padis.storage.AbstractNodeListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomListenerManager extends AbstractListenerManager{
	
	private CustomService customService;
	
	public CustomListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterInfoCacheManager clusterManager) {
		super(instance, coordinatorRegistryCenter,clusterManager);
		this.customService = new CustomService(instance, coordinatorRegistryCenter);
		this.customService.registerCustom();
	}
	
	public String getCustomPath(){
		return this.customService.getLocalCustomPath();
	}
	
	public Custom updateCustom(Custom custom){
		return this.customService.updateCustom(custom);
	}
	
    public ClusterInfoCacheManager getClusterManager(){
    	return (ClusterInfoCacheManager) clusterManager;
    }

	@Override 
	public void start() {
		addDataListener(new CustomStatusListener() , customService.getRootCustomPath());
	}

	class CustomStatusListener extends AbstractNodeListener{

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
						
			if(Type.NODE_UPDATED == event.getType()){
				if(path.equals(customService.getLocalCustomPath())){
					try{
						String json = new String(event.getData().getData());					
						Custom custom = JSON.parseObject(json, Custom.class);	
						log.info("custom:{}",json);			
						getClusterManager().updateCustom(custom,false);	
					}catch(Throwable t){
						log.error("update custom fail!", t);
					}
				}
			}	
		}
		
	}
}
