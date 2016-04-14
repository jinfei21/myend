package com.pingan.jinke.infra.padis.core;

import static com.pingan.jinke.infra.padis.common.Status.LIMIT;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.PoolManager;
import com.pingan.jinke.infra.padis.custom.Custom;
import com.pingan.jinke.infra.padis.custom.CustomListenerManager;
import com.pingan.jinke.infra.padis.group.Group;
import com.pingan.jinke.infra.padis.group.GroupListenerManager;
import com.pingan.jinke.infra.padis.migrate.MigrateListenerManager;
import com.pingan.jinke.infra.padis.slot.Slot;
import com.pingan.jinke.infra.padis.slot.SlotListenerManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterManager {

	private ConcurrentMap<Integer, Slot> slotMap;

	private ConcurrentMap<Integer, Group> groupMap;

	private SlotListenerManager slotListenerManager;

	private MigrateListenerManager migrateListenerManager;

	private GroupListenerManager groupListenerManager;

	private CustomListenerManager customListenerManager;
	
	private Custom custom;

	public ClusterManager(final String instance, final CoordinatorRegistryCenter coordinatorRegistryCenter) {
		this.slotListenerManager = new SlotListenerManager(instance, coordinatorRegistryCenter, this);
		this.migrateListenerManager = new MigrateListenerManager(instance, coordinatorRegistryCenter, this);
		this.groupListenerManager = new GroupListenerManager(instance, coordinatorRegistryCenter, this);
		this.customListenerManager = new CustomListenerManager(instance, coordinatorRegistryCenter, this);
		this.slotMap = Maps.newConcurrentMap();
		this.groupMap = Maps.newConcurrentMap();
	}

	public void loadRemoteConfig() {

		List<Slot> slotList = this.slotListenerManager.getAllSlots();
		List<Group> groupList = this.groupListenerManager.getAllGroups();

		for (Slot slot : slotList) {
			this.slotMap.put(slot.getId(), slot);
		}

		for (Group group : groupList) {
			this.groupMap.put(group.getId(), group);
		}
	}
	
	public void setPoolManager(PoolManager poolManager){
		groupListenerManager.setPoolManager(poolManager);
	}

	public Group getGroup(int gid) {
		return this.groupMap.get(gid);
	}
	
	public void addGroup(Group group){
		this.groupMap.put(group.getId(), group);
	}
	
	public void delGroup(int gid) {
		this.groupMap.remove(gid);
	}

	public Slot getSlot(int sid) {
		return this.slotMap.get(sid);
	}
	
	public void addSlot(Slot slot){
		this.slotMap.put(slot.getId(), slot);
	}

	public Set<HostAndPort> getAllMaster() {
		Set<HostAndPort> set = Sets.newHashSet();
		for (Slot slot : this.slotMap.values()) {
			Group group = getGroup(slot.getSrc_gid());
			set.add(group.getMaster());
		}
		return set;
	}
	
	
	public boolean limit(){
		if(custom == null){
			return false;
		}else{
			if(custom.getStatus() == LIMIT){				
				int limit = new Random().nextInt(100);
				if(limit < custom.getLimit()){
					return true;
				}				
			}
		}
		return false;
	}
	
	public void updateCustom(Custom custom){
		this.custom = custom;
	}

	/**
	 * ¿ªÆôËùÓÐ¼àÌýÆ÷.
	 */
	public void startAllListeners() {
		this.slotListenerManager.start();
		this.migrateListenerManager.start();
		this.groupListenerManager.start();
		this.customListenerManager.start();
	}
}
