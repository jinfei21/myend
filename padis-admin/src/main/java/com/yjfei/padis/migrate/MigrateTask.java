package com.yjfei.padis.migrate;

import java.util.Set;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.common.Migrate;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.common.TaskInfo;
import com.yjfei.padis.group.GroupService;
import com.yjfei.padis.node.CustomNode;
import com.yjfei.padis.node.Group;
import com.yjfei.padis.node.Slot;
import com.yjfei.padis.service.MigrateService;
import com.yjfei.padis.slot.SlotService;
import com.yjfei.padis.util.CRC16Utils;
import com.yjfei.padis.util.SleepUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@Setter
@Getter
@Slf4j
public class MigrateTask extends Thread {

	private TaskInfo taskInfo;
	private volatile boolean isFinished;
	private volatile boolean run;
	private String passwd;
	
	private SlotService slotService;

	private GroupService groupService;
	
	private MigrateService migrateService;
	
	private DistributeCountDown countdown;
	
	public MigrateTask(TaskInfo taskInfo,CoordinatorRegistryCenter coordinatorRegistryCenter,String passwd) {
		this.taskInfo = taskInfo;
		this.isFinished = false;
		this.passwd = passwd;
		this.run = false;
		this.slotService = new SlotService(taskInfo.getInstance(), coordinatorRegistryCenter);
		this.groupService = new GroupService(coordinatorRegistryCenter);
		this.migrateService = new MigrateService(coordinatorRegistryCenter);
		this.countdown = new DistributeCountDown(new CustomNode(taskInfo.getInstance()).getRootCustomPath(), coordinatorRegistryCenter);
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	public void start(ThreadPoolTaskExecutor executor) {
		if (!this.run) {
			this.run = true;
			executor.execute(this,1000);
		}
	}

	
	@Override
	public void run() {
		countdown.start();
		SleepUtils.sleep(50);
		for (int cur = taskInfo.getFrom(); cur <= taskInfo.getTo(); cur++) {
			Slot slot = null;
			try {
				slot = this.slotService.getSlot(cur);
				Migrate migrate = this.migrateService.getSlotMigrate(taskInfo.getInstance(), cur);
				if ( migrate != null) {
					
					migrateSingleSlot(slot,migrate);
					
					this.migrateService.delSlotMigrate(taskInfo.getInstance(), cur);
				} else {
					log.error(String.format("slot %s is null.", cur));
					this.migrateService.updateSlotMigrate(taskInfo.getInstance(), cur, Status.ERROR);
				}

			} catch (Throwable t) {
				this.migrateService.updateSlotMigrate(taskInfo.getInstance(), cur, Status.ERROR);
				log.error(String.format("migrate slot:%s fail!", cur), t);
			}
		}
		countdown.close();
		isFinished = true;
	}

	private void preMigrateStatus(Slot slot,Migrate migrate) throws InterruptedException {

		slot.setStatus(Status.PRE_MIGRATE);
		slot.setTo_gid(migrate.getTo_gid());
		slot.setModify(System.currentTimeMillis());
		countdown.fresh();
		slotService.setSlot(slot);
		countdown.await(600);
		
		slot.setStatus(Status.MIGRATE);
		slot.setTo_gid(migrate.getTo_gid());
		slot.setModify(System.currentTimeMillis());
		slotService.setSlot(slot);
		
		migrate.setStatus(Status.MIGRATE);
		migrateService.updateSlotMigrate(taskInfo.getInstance(), migrate);
	}

	private void migrateSingleSlot(Slot slot,Migrate migrate) throws InterruptedException {

		
		if(migrate.getFrom_gid() == migrate.getTo_gid()){
			log.error(String.format("can not migrate slot:%s from %s to %s.",slot.getId(),migrate.getFrom_gid(),migrate.getTo_gid()));
		}else{
			//等待所有的客户端确认状态
			preMigrateStatus( slot, migrate);
			Jedis client = null;
			Group fromGroup = groupService.getGroup(migrate.getFrom_gid());
			Group toGroup = groupService.getGroup(migrate.getTo_gid());
			
			try{
				
				client = new Jedis(fromGroup.getMaster().getHost(),fromGroup.getMaster().getPort(),1000,2000);
				client.auth(passwd);
				Set<String> keys = client.keys(taskInfo.getInstance()+"*");
				
				pingHost(toGroup);
				
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
			}catch(JedisConnectionException t){
				log.error(String.format("slot:%s,from:%s,to:%s", slot.getId(),fromGroup==null?slot.getSrc_gid():fromGroup.getMaster(),toGroup==null?slot.getTo_gid():toGroup.getMaster()), t);
				if(client != null){
					client.close();
				}
				if(migrate.getDelay() < 100){
					postMigrateStatus(slot, migrate.getFrom_gid());
					throw t;
				}
			}
			//完成
			postMigrateStatus(slot, migrate.getTo_gid());
			
		}
	}

	private void pingHost(Group grp) {
		Jedis jedis = new Jedis(grp.getMaster().getHost(), grp.getMaster().getPort());
		jedis.auth(passwd);
		String msg = "can not connect to group" + grp.getId() + ", "+ grp.getMaster().getHost() + " : " + grp.getMaster().getPort();
		try{
		     if (!jedis.ping().equals("PONG")) {
			     jedis.close();
			     throw new JedisException(msg);
		     }
		}catch(JedisConnectionException e){
			 jedis.close();
			 throw new JedisException(msg);
		}
		jedis.close();
	}
	
	
	private void postMigrateStatus(Slot slot,int gid) throws InterruptedException {
		slot.setTo_gid(-1);
		slot.setSrc_gid(gid);
		slot.setModify(System.currentTimeMillis());
		slot.setStatus(Status.ONLINE);
		countdown.fresh();
		slotService.setSlot(slot);
		countdown.await(600);
	}
	

}
