package com.yjfei.cache.padis.slot;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.alibaba.fastjson.JSON;
import com.yjfei.cache.padis.common.AbstractListenerManager;
import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.common.Status;
import com.yjfei.cache.padis.core.ClusterInfoCacheManager;
import com.yjfei.cache.padis.node.Custom;
import com.yjfei.cache.padis.node.Slot;
import com.yjfei.cache.padis.storage.AbstractNodeListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlotListenerManager extends AbstractListenerManager{

	private SlotService slotService;
	
	
	public SlotListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterInfoCacheManager clusterManager) {
		super(instance, coordinatorRegistryCenter, clusterManager);
		this.slotService = new SlotService(instance, coordinatorRegistryCenter);
	}
	
	public List<Slot> getAllSlots(){
		return this.slotService.getAllSlots();
	}
	
	public void updateSlot(Slot slot){
		this.slotService.setSlot(slot);
	}

	@Override
	public void start() {
		addDataListener(new SlotStatusListener() , slotService.getRootSlotPath());
	}
	
    
    public ClusterInfoCacheManager getClusterManager(){
    	return (ClusterInfoCacheManager) clusterManager;
    }
	
	class SlotStatusListener extends AbstractNodeListener{

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
			if(Type.NODE_UPDATED == event.getType()){
				try {
					String json = new String(event.getData().getData());
					Slot slot = JSON.parseObject(json, Slot.class);
					getClusterManager().addSlot(slot);
					
					if(Status.PRE_MIGRATE == slot.getStatus()){
						Custom custom = new Custom();
						custom.setStatus(Status.MIGRATE);
						custom.setModify(System.currentTimeMillis());
						getClusterManager().updateCustom(custom, true);
					}else if(Status.ONLINE == slot.getStatus()){
						Custom custom = new Custom();
						custom.setStatus(Status.ONLINE);
						custom.setModify(System.currentTimeMillis());
						getClusterManager().updateCustom(custom, true);
					}
				} catch (Throwable t) {
					log.error("slot update fail!", t);
				}
			}
		}
		
	}

	
}
