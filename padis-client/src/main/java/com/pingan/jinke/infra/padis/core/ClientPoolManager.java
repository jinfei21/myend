package com.pingan.jinke.infra.padis.core;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.Pool;
import com.pingan.jinke.infra.padis.common.PoolManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientPoolManager implements PoolManager{
	

	private ConcurrentMap<HostAndPort,ClientPool> node2Pool;
	
	public ClientPoolManager(String instance){
		this.node2Pool = Maps.newConcurrentMap();
	}
	
	@Override
	public void init(Set<HostAndPort> set) {
		for(HostAndPort node:set){
			node2Pool.put(node, new ClientPool(new ClientFactory(node),20) );
			log.info(String.format("initial host:%s client pool.", node));
		}
	}

	@Override
	public Client lease(HostAndPort node) throws Exception  {
		ClientPool pool = this.node2Pool.get(node);
		if(pool == null){			
			 pool = new ClientPool(new ClientFactory(node),20);
			 ClientPool prevPool = this.node2Pool.putIfAbsent(node, pool);
			 if(null != prevPool){
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
		
		if(pool == null){
			client.close();
		}else{
			pool.release(client);
		}
	}

	@Override
	public void releaseClose(Client client) throws Exception {
		HostAndPort node = client.getHostPort();
		ClientPool pool = this.node2Pool.get(node);
		
		if(pool == null){
			client.close();
		}else{
			pool.releaseClose(client);
		}
	}

	@Override
	public void closePool(HostAndPort node) {
		Pool pool = this.node2Pool.remove(node);
		if(pool != null){
			pool.close();
		}
	}

	
	
	public static void main(String args[]){
		
		Set<HostAndPort> set = Sets.newHashSet();
		
		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("1", 1));
		set.add(new HostAndPort("1", 1));
		
		set.add(new HostAndPort("2", 1));
		
		
		System.out.println("fsafsa");
		
	}
	
	
}
