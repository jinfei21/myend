package com.pingan.jinke.infra.padis.core;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Maps;
import com.pingan.jinke.infra.padis.common.ClusterManager;
import com.pingan.jinke.infra.padis.custom.CustomListenerManager;
import com.pingan.jinke.infra.padis.slot.SlotListenerManager;
import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.node.Custom;
import com.yjfei.cache.padis.node.Slot;
import com.yjfei.cache.padis.util.SleepUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterInfoCacheManager implements ClusterManager {

	private ConcurrentMap<Integer, Slot> slotMap;

	private SlotListenerManager slotListenerManager;

	private CustomListenerManager customListenerManager;

	private AtomicReference<Custom> atomicCustom;

	public ClusterInfoCacheManager(final String instance, final CoordinatorRegistryCenter coordinatorRegistryCenter) {
		this.slotListenerManager = new SlotListenerManager(instance, coordinatorRegistryCenter, this);
		this.customListenerManager = new CustomListenerManager(instance, coordinatorRegistryCenter, this);
		this.slotMap = Maps.newConcurrentMap();
		this.atomicCustom = new AtomicReference<Custom>();
	}

	@Override
	public void init() {
		// 启动监听器
		startAllListeners();

		List<Slot> slotList = this.slotListenerManager.getAllSlots();
		for (Slot slot : slotList) {
			this.slotMap.put(slot.getId(), slot);
		}
	}

	public Slot getSlot(int sid) {
		return this.slotMap.get(sid);
	}

	public void addSlot(Slot slot) {
		this.slotMap.put(slot.getId(), slot);
	}

	public void checkLimit() {
		Custom custom = atomicCustom.get();
		if (custom != null) {

			if (custom.getLimit() > 0) {
				int limit = new Random().nextInt(100);
				if (limit < custom.getLimit()) {
					SleepUtils.sleep(50);
				}
			}
		}
	}

	public void updateCustom(Custom custom, boolean remote) {
		if (remote) {
			custom = customListenerManager.updateCustom(custom);
		}
		this.atomicCustom.set(custom);
	}

	private void startAllListeners() {
		this.slotListenerManager.start();
		this.customListenerManager.start();
	}
}
