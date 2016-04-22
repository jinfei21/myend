package com.pingan.jinke.infra.padis.migrate;

import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.Migrate;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.common.TaskInfo;
import com.pingan.jinke.infra.padis.core.Client;
import com.pingan.jinke.infra.padis.group.GroupService;
import com.pingan.jinke.infra.padis.node.CustomNode;
import com.pingan.jinke.infra.padis.node.Group;
import com.pingan.jinke.infra.padis.node.Slot;
import com.pingan.jinke.infra.padis.slot.SlotService;
import com.pingan.jinke.infra.padis.storage.NodeStorage;
import com.pingan.jinke.infra.padis.util.CRC16Utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class MigrateTask extends Thread {

	private TaskInfo taskInfo;
	private volatile boolean isFinished;
	private boolean run;
	
	private MigrateNode migrateNode;

	private SlotService slotService;

	private GroupService groupService;
	
	private NodeStorage nodeStorage;
	
	private RemoteCountDown countdown;
	
	public MigrateTask(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
		this.isFinished = false;
		this.run = false;

	}

	public boolean isFinished() {
		return this.isFinished;
	}

	public void start(CoordinatorRegistryCenter coordinatorRegistryCenter) {
		if (!this.run) {
			this.run = true;
			this.slotService = new SlotService(taskInfo.getInstance(), coordinatorRegistryCenter);
			this.groupService = new GroupService(coordinatorRegistryCenter);
			this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
			this.countdown = new RemoteCountDown(new CustomNode(taskInfo.getInstance()).getRootCustomPath(), coordinatorRegistryCenter);
			this.migrateNode = new MigrateNode();
			this.start();
		}
	}
	
	private Migrate getMigrate(int slotid){
		String data = this.nodeStorage.getNodePathDataDirectly(migrateNode.getMigrateSlotPath(taskInfo.getInstance(), slotid));
		Migrate migrate = null;
		if(data != null){
			migrate = JSON.parseObject(data, Migrate.class);
		}
		return migrate;
	}

	private void updateMigrate(Migrate migrate){
		nodeStorage.replaceNodePath(migrateNode.getMigrateSlotPath(taskInfo.getInstance(), migrate.getSlot_id()), JSON.toJSONString(migrate));
	}
	
	private void delMigrate(Migrate migrate){
		nodeStorage.removeNodeIfExisted(migrateNode.getMigrateSlotPath(taskInfo.getInstance(), migrate.getSlot_id()));
	}
	
	@Override
	public void run() {
		for (int cur = taskInfo.getFrom(); cur <= taskInfo.getTo(); cur++) {
			try {
				Slot slot = this.slotService.getSlot(cur);
				Migrate migrate = getMigrate(slot.getId());
				if ( migrate != null) {
					
					migrateSingleSlot(slot,migrate);
					delMigrate(migrate);
				} else {
					log.error(String.format("slot %s is null.", cur));
				}

			} catch (Throwable t) {
				log.error(String.format("migrate slot:%s fail!", cur), t);
			}
		}
		isFinished = true;
	}

	private void preMigrateStatus(Slot slot,Migrate migrate) throws InterruptedException {

		slot.setStatus(Status.PRE_MIGRATE);
		slot.setTo_gid(migrate.getTo_gid());
		slot.setModify(System.currentTimeMillis());
		countdown.fresh();
		slotService.setSlot(slot);
		countdown.await(30);
		
		slot.setStatus(Status.MIGRATE);
		slot.setTo_gid(migrate.getTo_gid());
		slot.setModify(System.currentTimeMillis());
		slotService.setSlot(slot);
		
		migrate.setStatus(Status.MIGRATE);
		updateMigrate(migrate);
	}
	
	private void postMigrateStatus(Slot slot,Migrate migrate) throws InterruptedException {
		slot.setTo_gid(-1);
		slot.setSrc_gid(migrate.getTo_gid());
		slot.setModify(System.currentTimeMillis());
		slot.setStatus(Status.ONLINE);
		countdown.fresh();
		slotService.setSlot(slot);
		countdown.await(30);
	}
	
	private void migrateSingleSlot(Slot slot,Migrate migrate) throws InterruptedException {

		
		if(migrate.getFrom_gid() == migrate.getTo_gid()){
			log.error(String.format("can not migrate slot:%s from %s to %s.",slot.getId(),migrate.getFrom_gid(),migrate.getTo_gid()));
		}else{
			//等待所有的客户端确认状态
			preMigrateStatus( slot, migrate);
			
			Group fromGroup = groupService.getGroup(migrate.getFrom_gid());
			Group toGroup = groupService.getGroup(migrate.getTo_gid());
			
			Client client = new Client(fromGroup.getMaster());
			
			Set<String> keys = client.keys(taskInfo.getInstance()+"*");
			
			for(String key:keys){
				int id = CRC16Utils.getSlot(key);
				if(id == slot.getId()){
					try{
						client.migrate(toGroup.getMaster().getHost(), toGroup.getMaster().getPort(), key, 0, migrate.getDelay());
					}catch(Throwable t){
						log.error(String.format("migrate key:%s  from %s to %s", key,fromGroup.getMaster(),toGroup.getMaster()), t);
					}
					
				}
			}
			
			//完成
			postMigrateStatus(slot, migrate);
			
		}

	}

}
