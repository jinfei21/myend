package com.yjfei.padis.metric.storage;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yjfei.padis.metric.MetricConfig;

@Slf4j
public class InfluxdbFactory {
	
	private InfluxdbFactory(){		
	}
	
	
	public static List<InfluxDB> getServers(MetricConfig config){
	
		List<String> list = Splitter.on(",")
				   .trimResults()
				   .omitEmptyStrings()
				   .splitToList(config.getServerAddr());
		
		return getServers(list,config.getUsername(),config.getPassword());

	}
	
	
	public static List<InfluxDB> getServers(List<String> addrs,String username,String password){
		List<InfluxDB> result = Lists.newArrayList();
		for(String addr:addrs){
			InfluxDB server = getServer(addr,username,password);
			
			if(server != null){
				result.add(server);
			}
		}
		return result;
	}
	
	public static InfluxDB getServer(String addr,String username,String password){
		try{
			InfluxDB influxdb = InfluxDBFactory.connect("http://"+addr,username, password);
			Pong response  = influxdb.ping();
			if(!response.getVersion().equalsIgnoreCase("unknown")){
				return influxdb;
			}
		}catch(Throwable t){
			log.error("get influxdb fail.server="+addr+",user="+username+",password="+password, t);
		}
		return null;
	}	
	
}
