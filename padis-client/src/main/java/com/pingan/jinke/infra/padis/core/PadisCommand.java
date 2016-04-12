package com.pingan.jinke.infra.padis.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.pingan.jinke.infra.padis.common.Status.MIGRATE;
import static com.pingan.jinke.infra.padis.common.Status.OFFLINE;
import static com.pingan.jinke.infra.padis.common.Status.PRE_MIGRATE;

import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.PoolManager;
import com.pingan.jinke.infra.padis.exceptions.ClusterException;
import com.pingan.jinke.infra.padis.group.Group;
import com.pingan.jinke.infra.padis.slot.Slot;
import com.pingan.jinke.infra.padis.util.CRC16Utils;

import lombok.extern.slf4j.Slf4j;



@Slf4j
public abstract class PadisCommand<T> {

	protected PoolManager poolManager;

	protected ClusterManager clusterManager;

	public PadisCommand(ClusterManager clusterManager, PoolManager poolManager) {
		this.clusterManager = clusterManager;
		this.poolManager = poolManager;
	}

	public abstract T execute(Client client);

	public T run(String key,boolean isWrite) throws Exception {

		checkNotNull(key, "No way to dispatch this command to Redis Cluster.");

		int sid = CRC16Utils.getSlot(key);

		Slot slot = this.clusterManager.getSlot(sid);

		checkNotNull(slot, "slot is null,sid=%s", sid);

		if (isWrite || PRE_MIGRATE == slot.getStatus() || OFFLINE == slot.getStatus()) {
			throw new ClusterException("Can not write key for slot {sid=" + sid + "} pre_migrate.");
		}

		Client client = null;
		HostAndPort srcMaster = getGroup(slot.getSrc_gid()).getMaster();
		if (MIGRATE == slot.getStatus()) {
			HostAndPort toMaster = getGroup(slot.getTo_gid()).getMaster();

			try {
				client = this.poolManager.lease(srcMaster);
				client.migrate(toMaster.getHost(), toMaster.getPort(), key, 0, 500);
				this.poolManager.release(client);
			} catch (Throwable t) {
				log.error(String.format("migrate %s to %s,for slot_%s", srcMaster, toMaster, sid), t);
				this.poolManager.releaseClose(client);
			}
			client = this.poolManager.lease(toMaster);
		} else {
			client = this.poolManager.lease(srcMaster);
		}
		T result = null;
		try {
			result = execute(client);
			this.poolManager.release(client);
		} catch (Throwable t) {
			log.error(String.format("execute %s,for slot_%s",  client.getHostPort(), sid), t);
			this.poolManager.releaseClose(client);
			throw new ClusterException(t.getMessage());
		}
		return result;
	}

	private Group getGroup(int gid) {
		Group group = this.clusterManager.getGroup(gid);
		checkNotNull(group, "group is null,gid=%s", gid);
		return group;
	}

}
