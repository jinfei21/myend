package com.yjfei.padis.metric;

import com.yjfei.padis.metric.core.AgentBuilder;
import com.yjfei.padis.metric.core.ConfigLoader;


public class AgentFactory {
	private static IMetricAgent instance;
	
	public static IMetricAgent getAgent(MetricConfig config){
		if(instance == null){
			synchronized(IMetricAgent.class){
				if(instance == null){					
					instance = new AgentBuilder().config(config).build();					
				}
			}
		}
		return instance;
	}
	
	
	public static IMetricAgent getAgent(){
		if(instance == null){
			MetricConfig config = ConfigLoader.loadConfig();
			return getAgent(config);
		}else{
			return instance;
		}
	}
}
