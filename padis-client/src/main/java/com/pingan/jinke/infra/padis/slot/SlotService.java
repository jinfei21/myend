package com.pingan.jinke.infra.padis.slot;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.node.Slot;
import com.yjfei.cache.padis.node.SlotNode;
import com.yjfei.cache.padis.storage.NodeStorage;
import com.yjfei.cache.padis.util.RegExceptionHandler;

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
		this.nodeStorage.replaceNodePath(path, data);
	}
	
	public void updateSlot(Slot slot){
		Slot old = getSlot(slot.getId());
		if(old != null){
			slot.setCreate(old.getCreate());
			slot.setModify(System.currentTimeMillis());
			setSlot(slot);
		}		
	}
	
	public List<Slot> getAllSlots(){
		List<Slot> list = Lists.newArrayList();
		ExecutorService service = Executors.newCachedThreadPool();
		
		int num = 10;
		List<Future<List<Slot>>> fList = Lists.newArrayList();
		
		for(int i=0;i<1024;i = i+num){
			int to = i + num;
			if(to > 1024){
				to = 1024;
			}
			Future<List<Slot>> f = service.submit(new SlotCallable(i, to));
			fList.add(f);
		}
		
		for(Future<List<Slot>> f:fList){
			try{
				List<Slot> sList = f.get();
				list.addAll(sList);
			}catch(Exception e){
				RegExceptionHandler.handleException(e);
			}
		}
		service.shutdown();
		return list;
	}
	
	public Set<Integer> getAllGroups(){
		Set<Integer> set = Sets.newHashSet();
		
		for(Slot slot:getAllSlots()){
			if(null != slot){
				set.add(slot.getSrc_gid());	
			}
		}
		return set;
	}
	
	public Set<Integer> getSlotGroups(List<Slot> slotList){
		Set<Integer> set = Sets.newHashSet();
		
		for(Slot slot : slotList){
			if(null != slot){
				set.add(slot.getSrc_gid());	
			}
		}
		return set;
	}
	
	
	class SlotCallable implements Callable<List<Slot>>{

		private int from;
		private int to;
		
		public SlotCallable(int from,int to){
			this.from = from;
			this.to = to;
		}
		
		@Override
		public List<Slot> call() throws Exception {
			List<Slot> list = Lists.newArrayList();

			for(int i=from;i<to;i++){
				Slot slot = getSlot(i);
				list.add(slot);
			}
			return list;
		}
		
	}
}
