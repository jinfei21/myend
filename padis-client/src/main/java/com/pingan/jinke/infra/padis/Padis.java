package com.pingan.jinke.infra.padis;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.ZookeeperConfiguration;
import com.pingan.jinke.infra.padis.core.Client;
import com.pingan.jinke.infra.padis.core.ClientPoolManager;
import com.pingan.jinke.infra.padis.core.ClusterManager;
import com.pingan.jinke.infra.padis.core.PadisCommand;
import com.pingan.jinke.infra.padis.core.ZookeeperRegistryCenter;

public class Padis {

	private String instance;
	private String nameSpace;	
	
	private ClientPoolManager poolManager;
	
	private ClusterManager clusterManager;
	
	
	public Padis(ZookeeperConfiguration zkConfig,String instance,String nameSpace){
		this.instance = instance;
		this.nameSpace = nameSpace;
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zkConfig);
		regCenter.init();		
		regCenter.addCacheData("/"+instance);
		this.poolManager = new ClientPoolManager(instance);		
		this.clusterManager = new ClusterManager(instance,regCenter);		
		
	}
	
	public Padis(String zkAddr,String instance,String nameSpace){
		this(new ZookeeperConfiguration(zkAddr, "padis", 1000, 3000, 3),instance,nameSpace);
	}
	
	private void init(){
		//启动监听器
		this.clusterManager.startAllListeners();
		//初始化slot
		this.clusterManager.loadRemoteConfig();
		//初始化连接池
		this.poolManager.init(this.clusterManager.getAllMaster());
	}

	
	private String makeKey(String key){
		return String.format("%s$%s$%s", instance,nameSpace,key);
	}
	
	public String set(final String key, final String value) throws Exception{
		final String targetKey = makeKey(key);
		return new PadisCommand<String>(clusterManager,poolManager){

			@Override
			public String execute(Client client) {
				return client.set(targetKey, value);
			}
			
		}.run(targetKey,true);
	}
	
	public String get(final  String key) throws Exception{
		final String targetKey = makeKey(key);
		return new PadisCommand<String>(clusterManager,poolManager){

			@Override
			public String execute(Client client) {
				return client.get(targetKey);
			}
			
		}.run(targetKey,false);
	}
	
}
