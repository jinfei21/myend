package com.pingan.jinke.infra.padis.migrate;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Maps;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.TaskInfo;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MigrateTaskManager {

	private CoordinatorRegistryCenter coordinatorRegistryCenter;
	
	private volatile boolean run;
	
	private ConcurrentMap<String, MigrateTask> taskQueue;
	
	private ThreadPoolTaskExecutor executor;
	
	public MigrateTaskManager(CoordinatorRegistryCenter coordinatorRegistryCenter,ThreadPoolTaskExecutor executor){
		this.coordinatorRegistryCenter = coordinatorRegistryCenter;	
		this.run = false;
		this.taskQueue = Maps.newConcurrentMap();
		this.executor = executor;
	}
	
	public boolean postTask(TaskInfo task){
		MigrateTask oldTask = taskQueue.putIfAbsent(task.getInstance(), new MigrateTask(task,coordinatorRegistryCenter));
		if(oldTask == null){
			return true;
		}else{
			return false;
		}
	}
	
	public void start(){
		if(!this.run){
			this.run = true;
			executor.execute(new ManageThread(),200);
		}
	}
	
	public void stop(){
		this.run = false;
	}
	
	
	class ManageThread implements Runnable{
		
		@Override
		public void run(){
			
			while(run){
				try{
					for(Entry<String, MigrateTask> entry:taskQueue.entrySet()){
						MigrateTask task = entry.getValue();
						if(!task.isFinished()){
							task.start( executor);
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
