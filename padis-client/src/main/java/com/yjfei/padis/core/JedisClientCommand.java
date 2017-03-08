package com.yjfei.padis.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.yjfei.padis.common.Status.MIGRATE;
import static com.yjfei.padis.common.Status.OFFLINE;
import static com.yjfei.padis.common.Status.PRE_MIGRATE;

import redis.clients.jedis.Jedis;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.yjfei.padis.common.HostAndPort;
import com.yjfei.padis.exceptions.ClusterException;
import com.yjfei.padis.exceptions.ConnectionException;
import com.yjfei.padis.exceptions.DataException;
import com.yjfei.padis.exceptions.MaxRedirectionsException;
import com.yjfei.padis.exceptions.RedirectionException;
import com.yjfei.padis.node.Group;
import com.yjfei.padis.node.Slot;
import com.yjfei.padis.util.CRC16Utils;


public abstract class JedisClientCommand<T> {

	protected JedisClientPoolManager poolManager;

	protected ClusterInfoCacheManager clusterManager;

	protected int maxRedirection;

	public JedisClientCommand(ClusterInfoCacheManager clusterManager, JedisClientPoolManager poolManager,int maxRedirection) {
		this.clusterManager = clusterManager;
		this.poolManager = poolManager;
		this.maxRedirection = maxRedirection;
	}

	public abstract T execute(Jedis client);
	
	public abstract String methodName();

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

		Jedis jedis = this.poolManager.lease(srcMaster);

		T result = null;
		Transaction tran = Cat.newTransaction("Cache.redis", methodName());
		try {
			
			Cat.logEvent("Cache.redis.server", String.format("%s(%s)", srcMaster.getHost(), srcMaster.getPort()));
			result = execute(jedis);
			tran.setStatus(Transaction.SUCCESS);
			this.poolManager.release(jedis);
		} catch (ConnectionException t) {
			Cat.logError(String.format("scoket error,execute %s,for slot_%s", jedis.getClient().getHost(), slot.getId()),t);
			tran.setStatus(t);
			closeClient(jedis);
			return readWithRetries(key, slot, redirections - 1, false);
		} catch (RedirectionException t) {
			Cat.logError(String.format("data error,execute %s,for slot_%s", jedis.getClient().getHost(), slot.getId()), t);
			tran.setStatus(t);
			this.poolManager.release(jedis);
			return readWithRetries(key, slot, redirections - 1, false);
		}finally{
			tran.complete();
		}


		return result;
	}

	private void migrate(HostAndPort srcMaster, HostAndPort toMaster, String key, Slot slot) {
		Jedis jedis = null;
		Transaction tran = Cat.newTransaction("Cache.redis", "default:migrate");
		try {
			jedis = this.poolManager.lease(srcMaster);
			Cat.logEvent("Cache.redis.server", String.format("%s(%s)", srcMaster.getHost(), srcMaster.getPort()));
			jedis.migrate(toMaster.getHost(), toMaster.getPort(), key, 0, 500);
			tran.setStatus(Transaction.SUCCESS);
			this.poolManager.release(jedis);
		} catch (ConnectionException t) {
			Cat.logError(String.format("socket error,migrate %s to %s,for slot_%s", srcMaster, toMaster, slot.getId()), t);
			tran.setStatus(t);
			closeClient(jedis);
		} catch (DataException t) {
			Cat.logError(String.format("data error,migrate %s to %s,for slot_%s", srcMaster, toMaster, slot.getId()), t);
			tran.setStatus(t);
			this.poolManager.release(jedis);
		} catch (Exception t) {
			Cat.logError(String.format("pool error,migrate %s to %s,for slot_%s", srcMaster, toMaster, slot.getId()), t);
			tran.setStatus(t);
			closeClient(jedis);
		}finally{
			tran.complete();
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

		Jedis jedis = this.poolManager.lease(srcMaster);

		T result = null;
		Transaction tran = Cat.newTransaction("Cache.redis", methodName());
		try {
			
			Cat.logEvent("Cache.redis.server", String.format("%s(%s)", srcMaster.getHost(), srcMaster.getPort()));
			result = execute(jedis);
			tran.setStatus(Transaction.SUCCESS);
			this.poolManager.release(jedis);
		} catch (ConnectionException t) {
			Cat.logError(String.format("scoket error,execute %s,for slot_%s", jedis.getClient().getHost(), slot.getId()),t);
			tran.setStatus(t);
			closeClient(jedis);
			return writeWithRetries(key, slot, redirections - 1);
		} catch (RedirectionException t) {
			Cat.logError(String.format("data error,execute %s,for slot_%s", jedis.getClient().getHost(), slot.getId()), t);
			tran.setStatus(t);
			this.poolManager.release(jedis);
			return writeWithRetries(key, slot, redirections - 1);
		}finally{
			tran.complete();
		}

		return result;
	}

	private void closeClient(Jedis jedis) {
		try {
			this.poolManager.releaseClose(jedis);
		} catch (Throwable t) {
			Cat.logError(String.format("close %s fail.", jedis.getClient().getHost()), t);
		}
	}

	private Group getGroup(int gid) {
		Group group = this.poolManager.getGroup(gid);
		checkNotNull(group, "group is null,gid=%s", gid);
		return group;
	}

}
