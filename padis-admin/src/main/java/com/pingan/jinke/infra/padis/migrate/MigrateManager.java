package com.pingan.jinke.infra.padis.migrate;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.TaskInfo;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MigrateManager {

	private CoordinatorRegistryCenter coordinatorRegistryCenter;
	
	private volatile boolean run;
	
	private ConcurrentMap<String, MigrateTask> taskQueue;
	
	public MigrateManager(CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.coordinatorRegistryCenter = coordinatorRegistryCenter;	
		this.run = false;
		this.taskQueue = Maps.newConcurrentMap();
	}
	
	public boolean postTask(TaskInfo task){
		MigrateTask oldTask = taskQueue.putIfAbsent(task.getInstance(), new MigrateTask(task));
		if(oldTask == null){
			return true;
		}else{
			return false;
		}
	}
	
	public void start(){
		if(!this.run){
			this.run = true;
			new MigrateThread().start();
		}
	}
	
	public void stop(){
		this.run = false;
	}
	
	
	class MigrateThread extends Thread{
		
		@Override
		public void run(){
			while(run){
				try{
					for(Entry<String, MigrateTask> entry:taskQueue.entrySet()){
						MigrateTask task = entry.getValue();
						if(!task.isFinished()){
							task.start(coordinatorRegistryCenter);
						}else{
							taskQueue.remove(entry.getKey());
						}
					}
					Thread.sleep(2000);
				}catch(Throwable t){
					log.error("start migrate task fail!", t);
				}
			}
		}
	}
}
