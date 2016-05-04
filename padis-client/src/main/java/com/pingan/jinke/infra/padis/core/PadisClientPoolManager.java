package com.pingan.jinke.infra.padis.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pingan.jinke.infra.padis.common.Pool;
import com.pingan.jinke.infra.padis.common.PoolConfig;
import com.pingan.jinke.infra.padis.common.PoolManager;
import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.common.HostAndPort;
import com.yjfei.cache.padis.node.Group;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

@Slf4j
public class PadisClientPoolManager extends AbstractClientPoolManager implements PoolManager<Client> {

	private ConcurrentMap<HostAndPort, ClientPool> node2Pool;

	private PoolConfig config;
	
	public PadisClientPoolManager(String instance, final CoordinatorRegistryCenter coordinatorRegistryCenter,final PoolConfig config) {
		super(instance,coordinatorRegistryCenter);
		this.node2Pool = Maps.newConcurrentMap();
		this.config = config;
	}

	@Override
	public void init() {
		List<Group> groupList = this.groupListenerManager.getAllGroups();
		Set<HostAndPort> set = Sets.newHashSet();

		for (Group group : groupList) {
			this.groupMap.put(group.getId(), group);			
			set.add(group.getMaster());
		}
		
		init(set,config);
	}

	@Override
	public void init(Set<HostAndPort> set,PoolConfig config) {
		for(HostAndPort node:set){
			node2Pool.put(node, new ClientPool(new ClientFactory(node), config.getMaxTotal()));
			log.info(String.format("initial host:%s client pool.", node));
		}
	}


	@Override
	public Client lease(HostAndPort node) throws Exception {
		ClientPool pool = this.node2Pool.get(node);
		if (pool == null) {
			pool = new ClientPool(new ClientFactory(node), 20);
			ClientPool prevPool = this.node2Pool.putIfAbsent(node, pool);
			if (null != prevPool) {
				pool = prevPool;
			}
			log.info(String.format("initial host:%s client pool.", node));
		}
		return pool.lease();
	}

	@Override
	public void release(Client client) {
		HostAndPort node = client.getHostPort();
		ClientPool pool = this.node2Pool.get(node);

		if (pool == null) {
			client.close();
		} else {
			pool.release(client);
		}
	}

	@Override
	public void releaseClose(Client client) throws Exception {
		HostAndPort node = client.getHostPort();
		ClientPool pool = this.node2Pool.get(node);

		if (pool == null) {
			client.close();
		} else {
			pool.releaseClose(client);
		}
	}

	@Override
	public void closePool(HostAndPort node) {
		Pool pool = this.node2Pool.remove(node);
		if (pool != null) {
			pool.close();
		}
	}
	
	@Override
	public void close() {
		for(ClientPool pool:node2Pool.values()){
			pool.close();
		}
	}

	public static void main(String args[]) {

		Set<HostAndPort> set = Sets.newHashSet();

		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("2", 1));

		System.out.println("fsafsa");

	}


}
