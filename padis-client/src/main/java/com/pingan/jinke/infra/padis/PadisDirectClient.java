package com.pingan.jinke.infra.padis;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.ZookeeperConfiguration;
import com.pingan.jinke.infra.padis.core.Client;
import com.pingan.jinke.infra.padis.core.ClientPoolManager;
import com.pingan.jinke.infra.padis.core.ClusterManager;
import com.pingan.jinke.infra.padis.core.PadisCommand;
import com.pingan.jinke.infra.padis.storage.ZookeeperRegistryCenter;

class PadisDirectClient implements IPadis{

	private String instance;
	private String nameSpace;	
	
	private ClientPoolManager poolManager;
	
	private ClusterManager clusterManager;
	
	
	public PadisDirectClient(ZookeeperConfiguration zkConfig,String instance,String nameSpace){
		this.instance = instance;
		this.nameSpace = nameSpace;
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zkConfig);
		regCenter.init();		
		this.poolManager = new ClientPoolManager(instance);		
		this.clusterManager = new ClusterManager(instance,regCenter);		
		this.clusterManager.setPoolManager(poolManager);
	}
	
	public PadisDirectClient(String zkAddr,String instance,String nameSpace){
		this(new ZookeeperConfiguration(zkAddr, "padis", 1000, 3000, 3),instance,nameSpace);
	}
	
	public void init(){
		//启动监听器
		this.clusterManager.startAllListeners();
		//加载slot配置
		this.clusterManager.loadRemoteConfig();
		//初始化连接池
		this.poolManager.init(this.clusterManager.getAllMaster());
	}

	@Override
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	
	private String makeKey(String key){
		return String.format("%s$%s$%s", instance,nameSpace,key);
	}
	
	@Override
	public String set(final String key, final String value) throws Exception{
		final String targetKey = makeKey(key);
		return new PadisCommand<String>(clusterManager,poolManager){

			@Override
			public String execute(Client client) {
				return client.set(targetKey, value);
			}
			
		}.run(targetKey,true);
	}
	
	@Override	
	public String get(final  String key) throws Exception{
		final String targetKey = makeKey(key);
		return new PadisCommand<String>(clusterManager,poolManager){

			@Override
			public String execute(Client client) {
				return client.get(targetKey);
			}
			
		}.run(targetKey,false);
	}

	@Override
	public Long delete(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new PadisCommand<Long>(clusterManager,poolManager){

			@Override
			public Long execute(Client client) {
				return client.delete(targetKey);
			}
			
		}.run(targetKey,false);
	}
	
}
