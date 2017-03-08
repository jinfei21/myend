package com.yjfei.padis.metric;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.yjfei.padis.metric.AgentFactory;
import com.yjfei.padis.metric.IMetricAgent;
import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.SendPolicy;
import com.yjfei.padis.util.SleepUtils;

@Slf4j
@RunWith(Parameterized.class)
public class MetricTest {
	private MetricConfig config;
	private IMetricAgent agent;
	private int threadNum;
	
	@Parameters
	public static Collection data(){
		return Arrays.asList(new Object[][]{
			 { "IP:8086",// addr
				"root",// name
				"root", // password
				"jedisx",//dbname
				(int)Math.pow(2, 15),//bufferSize
				10,//workerSize
				20,//workerBufSize
				SendPolicy.Random,//sendPolicy
				"default",//retention
				"ONE", //consistent
				10//threadNum
			  }
		});
	}
	
	public MetricTest(String serverAddr, String username, String password,
			String dbName, int bufferSize, int workerSize, int workerBufSize,
			SendPolicy sendPolicy, String retention, String consistent, int threadNum){
		
		this.config = new MetricConfig(serverAddr, username, password,
				dbName, bufferSize, workerSize, workerBufSize,
				sendPolicy, retention, consistent);
		
		this.agent = AgentFactory.getAgent(config);
		
		this.threadNum = threadNum;
		
	}
	
	
	@Test
	public void influxdbTest(){
		
		
		ExecutorService service = Executors.newCachedThreadPool();
		for(int i=0; i<this.threadNum; i++){
			service.execute(new Producer(agent));
		}
		 
		while(true){
			SleepUtils.sleep(3000);
		}
	}
	
	
	class Producer implements Runnable{
		private IMetricAgent agent;
		private Map<String,String> tags = Maps.newHashMap();
		
		public Producer(IMetricAgent agent){
			this.agent = agent;
		}
		
		@Override
		public void run() {
			while(true){
				Stopwatch watch = Stopwatch.createStarted();
				for(int j=0; j<20000; j++){
					tags.put("name", "stg" + j);
					this.agent.log("stg","called",tags, j);
					SleepUtils.sleep(1);
				}
				System.out.println("2M total time： " + watch);
			}
		}
		
	}
}
