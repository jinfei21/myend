package com.pingan.jinke.infra.padis.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.PoolConfig;
import com.pingan.jinke.infra.padis.common.PoolManager;
import com.pingan.jinke.infra.padis.node.Group;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class JedisClientPoolManager extends AbstractClientPoolManager implements PoolManager<Jedis> {

	private ConcurrentMap<HostAndPort, JedisPool> node2Pool;
	private GenericObjectPoolConfig poolConfig;

	public JedisClientPoolManager(String instance, final CoordinatorRegistryCenter coordinatorRegistryCenter,final PoolConfig config) {
		super(instance,coordinatorRegistryCenter);
		this.node2Pool = Maps.newConcurrentMap();
		this.poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(config.getMaxIdle());
		poolConfig.setMaxTotal(config.getMaxTotal());
		poolConfig.setMinIdle(config.getMinIdle());
	}

	@Override
	public void init() {
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
			node2Pool.put(node, new JedisPool(poolConfig, node.getHost(), node.getPort()));		
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
			pool = new JedisPool(poolConfig, node.getHost(), node.getPort());
			JedisPool prevPool = this.node2Pool.putIfAbsent(node, pool);
			if (null != prevPool) {
				pool = prevPool;
			}
			log.info(String.format("initial host:%s client pool.", node));
		}
		return pool.getResource();
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