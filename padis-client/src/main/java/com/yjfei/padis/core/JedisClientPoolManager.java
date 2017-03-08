package com.yjfei.padis.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.common.HostAndPort;
import com.yjfei.padis.common.PoolConfig;
import com.yjfei.padis.common.PoolManager;
import com.yjfei.padis.node.Group;
import com.yjfei.padis.util.ConfigUtil;

@Slf4j
public class JedisClientPoolManager extends AbstractClientPoolManager implements PoolManager<Jedis> {
	private static final int DEFAULT_TIMEOUT = 2000;
	private ConcurrentMap<HostAndPort, JedisPool> node2Pool;
	private GenericObjectPoolConfig poolConfig;
	private String redisPwd;
	private AtomicLong count = new AtomicLong(0);//减少连接池信息
	
	public JedisClientPoolManager(String instance, final CoordinatorRegistryCenter coordinatorRegistryCenter,final PoolConfig config) {
		super(instance,coordinatorRegistryCenter);
		this.node2Pool = Maps.newConcurrentMap();
		this.poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(config.getMaxIdle());
		poolConfig.setMaxTotal(config.getMaxTotal());
		poolConfig.setMinIdle(config.getMinIdle());
		this.redisPwd = config.getPasswd();
	}

	@Override
	public void init() {
		if(null == this.redisPwd){
			this.redisPwd = ConfigUtil.getString("padis-config.properties", "redis.pwd");	
		}
		
		List<Group> groupList = this.groupListenerManager.getAllGroups();
		Set<HostAndPort> set = Sets.newHashSet();

		for (Group group : groupList) {
			this.groupMap.put(group.getId(), group);			
			set.add(group.getMaster());
		}

		init(set,null);
	}


	@Override
	public void init(Set<HostAndPort> set, PoolConfig config) {

		for(HostAndPort node:set){			
			node2Pool.put(node, new JedisPool(poolConfig, node.getHost(), node.getPort(), DEFAULT_TIMEOUT, this.redisPwd));		
			log.info(String.format("initial host:%s client pool.", node));
		}
	}
	
	

	@Override
	public void closePool(HostAndPort node) {
		JedisPool pool = this.node2Pool.remove(node);
		if (pool != null) {
			pool.close();
		}
	}

	@Override
	public Jedis lease(HostAndPort node) throws Exception {
		JedisPool pool = this.node2Pool.get(node);
		if (pool == null) {
			pool = new JedisPool(poolConfig, node.getHost(), node.getPort(), DEFAULT_TIMEOUT, this.redisPwd);
			JedisPool prevPool = this.node2Pool.putIfAbsent(node, pool);
			if (null != prevPool) {
				pool = prevPool;
			}
			log.info(String.format("initial host:%s client pool.", node));
		}
		Jedis jedis = pool.getResource();
		
		if(count.getAndIncrement() > 20){
			Cat.logEvent("Redis.pool", node.toString(),Event.SUCCESS,String.format("a:%s.i:%s", pool.getNumActive(),pool.getNumIdle()));	
			count.set(0);
		}
		return jedis;
	}

	@Override
	public void release(Jedis jedis) {
		HostAndPort node = new HostAndPort(jedis.getClient().getHost(),jedis.getClient().getPort());
		
		JedisPool pool = this.node2Pool.get(node);

		if (pool == null) {
			jedis.close();
		} else {
			pool.returnResource(jedis);
		}
	}

	@Override
	public void releaseClose(Jedis jedis) throws Exception {
		HostAndPort node = new HostAndPort(jedis.getClient().getHost(),jedis.getClient().getPort());
		
		JedisPool pool = this.node2Pool.get(node);
		
		if (pool == null) {
			jedis.close();
		} else {
			pool.returnBrokenResource(jedis);
		}
	}

	@Override
	public void close() {
		for(JedisPool pool:node2Pool.values()){
			pool.close();
		}
	}

}