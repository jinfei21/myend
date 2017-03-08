package com.yjfei.padis.metric.core;


import static com.yjfei.padis.metric.ConfigConstant.AgentPolicy;
import static com.yjfei.padis.metric.ConfigConstant.BUFFER_SIZE;
import static com.yjfei.padis.metric.ConfigConstant.BufferSize;
import static com.yjfei.padis.metric.ConfigConstant.Consistency;
import static com.yjfei.padis.metric.ConfigConstant.DBName;
import static com.yjfei.padis.metric.ConfigConstant.PassWord;
import static com.yjfei.padis.metric.ConfigConstant.Retention;
import static com.yjfei.padis.metric.ConfigConstant.ServerAddr;
import static com.yjfei.padis.metric.ConfigConstant.UserName;
import static com.yjfei.padis.metric.ConfigConstant.WORKER_BUFFER_SIZE;
import static com.yjfei.padis.metric.ConfigConstant.WORKER_SIZE;
import static com.yjfei.padis.metric.ConfigConstant.WorkerBufSize;
import static com.yjfei.padis.metric.ConfigConstant.WorkerSize;

import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.SendPolicy;
import com.yjfei.padis.util.ConfigUtil;

public class ConfigLoader {

	public static MetricConfig loadConfig(){
		MetricConfig config = new MetricConfig();
		
		ConfigUtil.mergeConfig("config.properties", "metric.properties");
		config.setBufferSize(ConfigUtil.getInt("config.properties", BufferSize,BUFFER_SIZE));
		config.setConsistent(ConfigUtil.getString("config.properties", Consistency, "quorum").toUpperCase());
		config.setDbName(ConfigUtil.getString("config.properties", DBName, "default"));
		config.setUsername(ConfigUtil.getString("config.properties", UserName, "root"));
		config.setPassword(ConfigUtil.getString("config.properties", PassWord, "root"));
		config.setWorkerSize(ConfigUtil.getInt("config.properties", WorkerSize,WORKER_SIZE));
		config.setWorkerBufSize(ConfigUtil.getInt("config.properties", WorkerBufSize,WORKER_BUFFER_SIZE));
		config.setRetention(ConfigUtil.getString("config.properties", Retention, "default"));
		config.setServerAddr(ConfigUtil.getString("config.properties", ServerAddr, "default"));
		config.setSendPolicy(SendPolicy.getSendPolicy(ConfigUtil.getString("config.properties", AgentPolicy)));
		
		return config;
	}
}
