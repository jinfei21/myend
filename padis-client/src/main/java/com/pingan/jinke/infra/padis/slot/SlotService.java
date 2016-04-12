package com.pingan.jinke.infra.padis.slot;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

public class SlotService {

	private SlotNode slotNode;
	
	private NodeStorage nodeStorage;
	
	public SlotService(String instance,CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.slotNode = new SlotNode(instance);
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
	}
	
	public String getRootSlotPath(){
		return this.slotNode.getRootSlotPath();
	}
	
	public Slot getSlot(int id){
		String data = this.nodeStorage.getNodePathDataDirectly(this.slotNode.getSlotPath(id));		
		Slot slot = JSON.parseObject(data, Slot.class);		
		return slot;
	}
	
	public void setSlot(Slot slot){
		String data = JSON.toJSONString(slot);
		String path = this.slotNode.getSlotPath(slot.getId());
		this.nodeStorage.updateNodePath(path, data);
	}
	
	public List<Slot> getAllSlots(){
		List<Slot> list = Lists.newArrayList();
		
		for(int i=0;i<1024;i++){
			Slot slot = getSlot(i);
			list.add(slot);
		}
		
		return list;
	}
}
