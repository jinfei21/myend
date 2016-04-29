package com.pingan.jinke.infra.padis.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.pingan.jinke.infra.padis.common.Status.MIGRATE;
import static com.pingan.jinke.infra.padis.common.Status.OFFLINE;
import static com.pingan.jinke.infra.padis.common.Status.PRE_MIGRATE;

import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.exceptions.ClusterException;
import com.pingan.jinke.infra.padis.exceptions.ConnectionException;
import com.pingan.jinke.infra.padis.exceptions.DataException;
import com.pingan.jinke.infra.padis.exceptions.MaxRedirectionsException;
import com.pingan.jinke.infra.padis.exceptions.RedirectionException;
import com.pingan.jinke.infra.padis.node.Group;
import com.pingan.jinke.infra.padis.node.Slot;
import com.pingan.jinke.infra.padis.util.CRC16Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PadisClientCommand<T> {

	protected PadisClientPoolManager poolManager;

	protected ClusterInfoCacheManager clusterManager;

	protected int maxRedirection;

	public PadisClientCommand(ClusterInfoCacheManager clusterManager, PadisClientPoolManager poolManager,
			int maxRedirection) {
		this.clusterManager = clusterManager;
		this.poolManager = poolManager;
		this.maxRedirection = maxRedirection;
	}

	public abstract T execute(Client client);

	public T run(String key, boolean isWrite) throws Exception {

		checkNotNull(key, "No way to dispatch this command to Redis Cluster.");

		this.clusterManager.checkLimit();

		int sid = CRC16Utils.getSlot(key);

		Slot slot = this.clusterManager.getSlot(sid);

		checkNotNull(slot, "slot is null,sid=%s", sid);

		if ((isWrite && PRE_MIGRATE == slot.getStatus()) || OFFLINE == slot.getStatus()) {
			throw new ClusterException("Can not write key for slot {sid=" + sid + "} pre_migrate.");
		}

		if (isWrite) {
			return writeWithRetries(key, slot, maxRedirection);
		} else {
			return readWithRetries(key, slot, maxRedirection, true);
		}
	}

	private T readWithRetries(String key, Slot slot, int redirections, boolean master) throws Exception {

		if (redirections <= 0) {
			throw new RedirectionException("Too many Cluster redirections?");
		}
		HostAndPort srcMaster = getGroup(slot.getSrc_gid()).getMaster();
		if (master) {
			if (MIGRATE == slot.getStatus()) {
				HostAndPort toMaster = getGroup(slot.getTo_gid()).getMaster();
				migrate(srcMaster, toMaster, key, slot);
				srcMaster = toMaster;
			}
		} else {
			srcMaster = getGroup(slot.getSrc_gid()).getSlave();
		}

		Client client = this.poolManager.lease(srcMaster);

		T result = null;
		try {
			result = execute(client);
			this.poolManager.release(client);
		} catch (ConnectionException t) {
			log.error(String.format("scoket error,execute %s,for slot_%s", client.getHostPort(), slot.getId()), t);
			closeClient(client);
			return readWithRetries(key, slot, redirections - 1, false);
		} catch (RedirectionException t) {
			log.error(String.format("data error,execute %s,for slot_%s", client.getHostPort(), slot.getId()), t);
			this.poolManager.release(client);
			return readWithRetries(key, slot, redirections - 1, false);
		}

		return result;

	}

	private void migrate(HostAndPort srcMaster, HostAndPort toMaster, String key, Slot slot) {
		Client client = null;
		try {
			client = this.poolManager.lease(srcMaster);
			client.migrate(toMaster.getHost(), toMaster.getPort(), key, 0, 500);
			this.poolManager.release(client);
		} catch (ConnectionException t) {
			log.error(String.format("socket error,migrate %s to %s,for slot_%s", srcMaster, toMaster, slot.getId()), t);
			closeClient(client);
		} catch (DataException t) {
			log.error(String.format("data error,migrate %s to %s,for slot_%s", srcMaster, toMaster, slot.getId()), t);
			this.poolManager.release(client);
		} catch (Exception t) {
			log.error(String.format("pool error,migrate %s to %s,for slot_%s", srcMaster, toMaster, slot.getId()), t);
			closeClient(client);
		}
	}

	private T writeWithRetries(String key, Slot slot, int redirections) throws Exception {

		if (redirections <= 0) {
			throw new MaxRedirectionsException("Too many Cluster redirections?");
		}

		HostAndPort srcMaster = getGroup(slot.getSrc_gid()).getMaster();
		if (MIGRATE == slot.getStatus()) {
			HostAndPort toMaster = getGroup(slot.getTo_gid()).getMaster();
			migrate(srcMaster, toMaster, key, slot);
			srcMaster = toMaster;
		}

		Client client = this.poolManager.lease(srcMaster);

		T result = null;
		try {
			result = execute(client);
			this.poolManager.release(client);
		} catch (ConnectionException t) {
			log.error(String.format("scoket error,execute %s,for slot_%s", client.getHostPort(), slot.getId()), t);
			closeClient(client);
			return writeWithRetries(key, slot, redirections - 1);
		} catch (RedirectionException t) {
			log.error(String.format("data error,execute %s,for slot_%s", client.getHostPort(), slot.getId()), t);
			this.poolManager.release(client);
			return writeWithRetries(key, slot, redirections - 1);
		}

		return result;
	}

	private void closeClient(Client client) {
		try {
			this.poolManager.releaseClose(client);
		} catch (Throwable t) {
			log.error(String.format("close %s fail.", client.getHostPort()), t);
		}
	}

	private Group getGroup(int gid) {
		Group group = this.poolManager.getGroup(gid);
		checkNotNull(group, "group is null,gid=%s", gid);
		return group;
	}

}
