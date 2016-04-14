package com.pingan.jinke.infra.padis.slot;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.alibaba.fastjson.JSON;
import com.pingan.jinke.infra.padis.common.AbstractListenerManager;
import com.pingan.jinke.infra.padis.common.AbstractNodeListener;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.core.ClusterManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlotListenerManager extends AbstractListenerManager{

	private SlotService slotService;
	
	
	public SlotListenerManager(String instance, CoordinatorRegistryCenter coordinatorRegistryCenter,ClusterManager clusterManager) {
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
	
	class SlotStatusListener extends AbstractNodeListener{

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
			if(Type.NODE_UPDATED == event.getType()){
				try {
					String json = new String(event.getData().getData());
					Slot slot = JSON.parseObject(json, Slot.class);
					getClusterManager().addSlot(slot);
				} catch (Throwable t) {
					log.error("slot update fail!", t);
				}
			}
		}
		
	}

	
}
