package com.yjfei.cache.padis.core;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yjfei.cache.padis.common.ObjectFactory;
import com.yjfei.cache.padis.common.Pool;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientPool  implements Pool<Client>{
	
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private HashSet<Client> leaseSet = new HashSet<Client>();
    private LinkedList<Client> freeList = new LinkedList<Client>();
    private ObjectFactory<Client> factory;
    private volatile int maxClient;
    private volatile long leaseTimeout = 2000;   
    private volatile long ttl = 1000*60*3;    
    
    
	public ClientPool(ObjectFactory<Client> factory,int maxClient){		
		this.factory = factory;
		this.maxClient = maxClient;		
	}

	@Override
	public Client lease() throws Exception{
		return lease(leaseTimeout);
	}

	@Override
	public Client lease(long timeout) throws Exception {
		
		Client client = null;
		
		try{
			lock.lock();
			Date deadline = new Date(System.currentTimeMillis()+timeout);
			while(true){
				while((client=freeList.pollFirst()) != null){
					
					if(client.getCreateTime() +  ttl <= System.currentTimeMillis() ){
						try{
							client.close();
						}catch(Throwable t){
							log.error("close client error for freelist!host:"+client.toString());
						}
					}else{
						leaseSet.add(client);
						return client;
					}
				}
				if(clientCount() <= maxClient){
					return createToLease();
				}
				
				condition.awaitUntil(deadline);
				if(deadline.getTime() <= System.currentTimeMillis()){
					throw new TimeoutException("Timeout for client");
				}
				
			}
						
		}finally{
			lock.unlock();
		}
	}
	
	public int clientCount(){
		return freeList.size()+leaseSet.size();
	}
	
	private Client createToLease(){
		Client client = factory.make();		
		leaseSet.add(client);
		return client;
	}

	@Override
	public void release(Client client) {
		try{
			lock.lock();
			leaseSet.remove(client);
			freeList.add(client);
			client.updateTTL();
			condition.signal();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void releaseClose(Client client) throws Exception{
		try{
			lock.lock();
			leaseSet.remove(client);
			client.close();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void close() {
		while (leaseSet.size() != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		for (Client client : freeList) {
			try {
				client.close();
			} catch (Exception e) {
			}
		}
	}


}
