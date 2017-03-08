package com.yjfei.padis.metric.storage;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

import org.influxdb.InfluxDB;

import com.yjfei.padis.metric.Pool;
import com.yjfei.padis.util.SleepUtils;


@Slf4j
public class InfluxdbPool implements Pool<InfluxDB>{
	
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private HashSet<InfluxDB> leaseSet = new HashSet<InfluxDB>();
    private LinkedList<InfluxDB> freeList = new LinkedList<InfluxDB>();
    
    private volatile int maxClient = 100;
    private volatile long leaseTimeout = 2000;   
    private volatile long ttl = 1000*60*3;
    
    private String username;
	
    private String password;	
	
    private String addr;
    
    public InfluxdbPool(String addr,String username,String password){
    	this.username = username;
    	this.password = password;
    	this.addr = addr;
    }

	@Override
	public InfluxDB lease() throws Exception {
		return lease(leaseTimeout);
	}

	@Override
	public InfluxDB lease(long timeout) throws Exception {
		InfluxDB influxdb = null;
		
		try{
			lock.lock();
			Date deadline = new Date(System.currentTimeMillis()+timeout);
			while(true){
				while((influxdb=freeList.pollFirst()) != null){		
					leaseSet.add(influxdb);
					return influxdb;					
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
	
	private InfluxDB createToLease(){
		InfluxDB influxdb = InfluxdbFactory.getServer(addr, username, password);		
		leaseSet.add(influxdb);
		return influxdb;
	}
	
	@Override
	public void release(InfluxDB influxdb) {
		try{
			lock.lock();
			leaseSet.remove(influxdb);
			freeList.add(influxdb);
			condition.signal();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void releaseClose(InfluxDB influxdb) throws Exception {
		try{
			lock.lock();
			leaseSet.remove(influxdb);
			influxdb = null;
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void close() {
		while (leaseSet.size() != 0) {
			SleepUtils.sleep(100);
		}
		for (InfluxDB influxdb : freeList) {
			try {
				influxdb = null;
			} catch (Exception e) {
			}
		}
	}
	
}
