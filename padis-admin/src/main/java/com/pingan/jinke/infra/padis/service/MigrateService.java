package com.pingan.jinke.infra.padis.service;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.Migrate;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.migrate.MigrateManager;
import com.pingan.jinke.infra.padis.migrate.MigrateNode;
import com.pingan.jinke.infra.padis.migrate.MigrateTask;
import com.pingan.jinke.infra.padis.slot.Slot;
import com.pingan.jinke.infra.padis.slot.SlotNode;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrateService {
	
	private NodeStorage nodeStorage;
	
	private MigrateManager manager;
	
	private MigrateNode migrateNode;
	
	public MigrateService(CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);	
		this.manager = new MigrateManager(coordinatorRegistryCenter);
		this.migrateNode = new MigrateNode();
	}


	public void addTask(String instance,int from,int to,int gid,int delay){
		
		for(int i=from;i<=to;i++){
			persistMigrate(instance, i, gid, delay);
		}
		
		manager.postTask(new MigrateTask(instance,from,to));
	}
	
	
	private void persistMigrate(String instance,int slotid,int to_gid,int delay){
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
		nodeStorage.replaceNodePath(migrateNode.getMigrateSlotPath(instance,slotid), JSON.toJSONString(migrate));
	}
	
	public List<Migrate> getTask(String instance){
		List<Migrate> migrates = Lists.newArrayList();
		
		String instancePath = migrateNode.getMigratePath(instance);
		List<String> nodes = this.nodeStorage.getNodePathChildrenKeys(instancePath);
		
		for(String node:nodes){
			String data = this.nodeStorage.getNodePathDataDirectly(instancePath+"/"+node);	
			Migrate migrate = JSON.parseObject(data, Migrate.class);	
			migrates.add(migrate);
		}
		
		return migrates;
	}
}
