package com.yjfei.padis.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.common.Migrate;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.migrate.MigrateNode;
import com.yjfei.padis.node.Slot;
import com.yjfei.padis.node.SlotNode;
import com.yjfei.padis.storage.NodeStorage;
import com.yjfei.padis.util.RegExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrateService {

	private NodeStorage nodeStorage;

	private MigrateNode migrateNode;

	public MigrateService(CoordinatorRegistryCenter coordinatorRegistryCenter) {
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
		this.migrateNode = new MigrateNode();
	}


	public void persistMigrate(String instance, int slotid, int to_gid, int delay) {
		SlotNode slotNode = new SlotNode(instance);

		String data = nodeStorage.getNodePathDataDirectly(slotNode.getSlotPath(slotid));

		Slot slot = JSON.parseObject(data, Slot.class);

		Migrate migrate = new Migrate();
		migrate.setCreate(System.currentTimeMillis());
		migrate.setFrom_gid(slot.getSrc_gid());
		migrate.setTo_gid(to_gid);
		migrate.setPercent(0);
		migrate.setDelay(delay);
		migrate.setStatus(Status.PENDING);
		migrate.setSlot_id(slotid);
		nodeStorage.replaceNodePath(migrateNode.getMigrateSlotPath(instance, slotid), JSON.toJSONString(migrate));
	}
	
	
	public void persistMigrate(String instance, int from_slotid,int to_slotid, int to_gid, int delay) {
		ExecutorService service = Executors.newCachedThreadPool();
		
		int num = 10;
		List<Future> fList = Lists.newArrayList();
		to_slotid++;
		for (int i = from_slotid; i < to_slotid; i = i + num) {
			int to = i + num;
			if(to > to_slotid){
				to = to_slotid;
			}
			Future f = service.submit(new PersistCallable(instance,i, to,to_gid, delay));
			fList.add(f);
		}
		
		for(Future f:fList){
			try{
				f.get();
			}catch(Exception e){
				RegExceptionHandler.handleException(e);
			}
		}
		
		service.shutdown();
	}

	class PersistCallable implements Callable {
		private int from;
		private int to;
		private String instance;
		private int to_gid;
		private int delay;

		public PersistCallable(String instance, int from, int to, int to_gid, int delay) {
			this.from = from;
			this.to = to;
			this.instance = instance;
			this.to_gid = to_gid;
			this.delay = delay;
		}

		@Override
		public Object call() throws Exception {
			for (int i = from; i < to; i++) {
				persistMigrate(instance, i, to_gid, delay);
			}
			return null;
		}

	}

	public List<Migrate> getTask(String instance) {
		List<Migrate> migrates = Lists.newArrayList();

		String instancePath = migrateNode.getMigratePath(instance);
		List<String> nodes = this.nodeStorage.getNodePathChildrenKeys(instancePath);
		
		ExecutorService service = Executors.newCachedThreadPool();
		
		int num = 10;
		List<Future<List<Migrate>>> fList = Lists.newArrayList();
		
		for(int i=0;i<nodes.size();i = i+num){
			int to = i + num;
			if(to > nodes.size()){
				to = nodes.size();
			}
			Future<List<Migrate>> f = service.submit(new MigrateCallable(instance,i, to,nodes));
			fList.add(f);
		}
		
		for(Future<List<Migrate>> f:fList){
			try{
				List<Migrate> sList = f.get();
				migrates.addAll(sList);
			}catch(Exception e){
				RegExceptionHandler.handleException(e);
			}
		}
		service.shutdown();
		return migrates;
	}
	
	
	class MigrateCallable implements Callable<List<Migrate>>{

		private int from;
		private int to;
		private String instance;
		private List<String> nodes;
		
		public MigrateCallable(String instance,int from,int to,List<String> nodes){
			this.from = from;
			this.to = to;
			this.instance = instance;
			this.nodes = nodes;
		}
		
		@Override
		public List<Migrate> call() throws Exception {
			List<Migrate> list = Lists.newArrayList();
			String instancePath = migrateNode.getMigratePath(instance);
			for(int i=from;i<to;i++){
				
				String data = nodeStorage.getNodePathDataDirectly(instancePath + "/" + nodes.get(i));
				Migrate migrate = JSON.parseObject(data, Migrate.class);
				list.add(migrate);
			}
			return list;
		}
		
	}
	
	public Migrate getSlotMigrate(String instance,int slotid){
		String data = this.nodeStorage.getNodePathDataDirectly(migrateNode.getMigrateSlotPath(instance, slotid));
		Migrate migrate = null;
		if(data != null){
			migrate = JSON.parseObject(data, Migrate.class);
		}
		return migrate;
	}
	
	public void delSlotMigrate(String instance,int slotid){
		this.nodeStorage.removeNodeIfExisted(migrateNode.getMigrateSlotPath(instance, slotid));
	}
	
	public void delAllMigrateSlot(String instance){
		this.nodeStorage.removeNodeIfExisted(migrateNode.getMigratePath(instance));
	}
	
	public void updateSlotMigrate(String instance,Migrate migrate){
		nodeStorage.replaceNodePath(migrateNode.getMigrateSlotPath(instance, migrate.getSlot_id()), JSON.toJSONString(migrate));
	}
	
	public void updateSlotMigrate(String instance,int slotid,Status status){
		Migrate migrate = getSlotMigrate( instance, slotid);
		
		if(migrate != null){
			migrate.setStatus(status);
			migrate.setModify(System.currentTimeMillis());
			updateSlotMigrate(instance,migrate);
		}
	}

		
}
