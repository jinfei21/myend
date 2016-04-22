package com.pingan.jinke.infra.padis.service;

import java.util.List;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.Migrate;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.migrate.MigrateNode;
import com.pingan.jinke.infra.padis.node.Slot;
import com.pingan.jinke.infra.padis.node.SlotNode;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

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

	public List<Migrate> getTask(String instance) {
		List<Migrate> migrates = Lists.newArrayList();

		String instancePath = migrateNode.getMigratePath(instance);
		List<String> nodes = this.nodeStorage.getNodePathChildrenKeys(instancePath);

		for (String node : nodes) {
			String data = this.nodeStorage.getNodePathDataDirectly(instancePath + "/" + node);
			Migrate migrate = JSON.parseObject(data, Migrate.class);
			migrates.add(migrate);
		}

		return migrates;
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
	
}
