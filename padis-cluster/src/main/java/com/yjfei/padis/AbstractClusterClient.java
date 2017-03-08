package com.yjfei.padis;

import java.util.Random;
import java.util.Set;

import com.yjfei.padis.common.Status;
import com.yjfei.padis.node.Custom;
import com.yjfei.padis.util.SleepUtils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Slf4j
public abstract class AbstractClusterClient {
	protected JedisCluster jedisCluster;
	
	protected ClusterInfoCache clusterConfig;
	
	public AbstractClusterClient(JedisxConfig config){
		//this(new ZookeeperConfiguration(config.getZkAddr(), "padis", 1000, 3000, 3),config.getInstance(),config.getNameSpace(),config.getConnectionTimeout(),config.getMaxRedirections(),config.getSoTimeout());
		this.clusterConfig = new ClusterInfoCache(config);
		initializeCluster(clusterConfig);
	}
	
	private void initializeCluster(ClusterInfoCache clusterConfig){
		Set<HostAndPort> set = clusterConfig.getServers();
		if(set.isEmpty()){
			throw new RuntimeException("Remote cluster is null.");
		}
		this.jedisCluster = new JedisCluster(set, clusterConfig.getConnectionTimeout(), clusterConfig.getSoTimeout(), clusterConfig.getMaxRedirections(), clusterConfig.getPoolConfig());
	}
	
	protected String makeKey(String key){
		return String.format("%s$%s$%s", clusterConfig.getInstance(),clusterConfig.getNameSpace(),key);
	}
	
	public void setNameSpace(String nameSpace){
		this.clusterConfig.setNameSpace(nameSpace); 
	}
	
	public void check(){
		Custom custom = this.clusterConfig.getCustom();
		
		if(custom != null && custom.getStatus() == Status.LIMIT){
			int rand = new Random().nextInt(100);
			
			if(rand <= custom.getLimit()){
				SleepUtils.sleep(200);
			}
		}
		
	}
}
