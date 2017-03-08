package com.yjfei.padis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import redis.clients.jedis.Jedis;

import com.yjfei.padis.IJedisx;
import com.yjfei.padis.JedisxConfig;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.common.PoolConfig;
import com.yjfei.padis.common.ZookeeperConfiguration;
import com.yjfei.padis.core.ClusterInfoCacheManager;
import com.yjfei.padis.core.JedisClientCommand;
import com.yjfei.padis.core.JedisClientPoolManager;
import com.yjfei.padis.storage.ZookeeperRegistryCenter;

public class JedisDirectClient implements IJedisx{

	private JedisClientPoolManager poolManager;
	
	private ClusterInfoCacheManager clusterManager;
	
	private CoordinatorRegistryCenter regCenter;
	
	private JedisxConfig config;
	
	public JedisDirectClient(JedisxConfig config){
		this.config = new JedisxConfig(config);	
		regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(this.config.getZkAddr(), "padis", 1000, 3000, 3,config.getNestedPort(),config.getNestedDataDir()));
		regCenter.init();		
		this.poolManager = new JedisClientPoolManager(this.config.getInstance(),regCenter,new PoolConfig(config));		
		this.clusterManager = new ClusterInfoCacheManager(this.config.getInstance(),regCenter);		
		init();
	}
	
	public JedisDirectClient(String zkAddr,String instance, String namespace, String password){
		this(new JedisxConfig(zkAddr, instance, namespace, password));
	}
	
	public void init(){
		//启动监听器,加载slot信息，注册custom
		this.clusterManager.init();
		//初始化连接池
		this.poolManager.init();
	}

	@Override
	public void setNameSpace(String nameSpace) {
		this.config.setNameSpace(nameSpace);
	}

	@PreDestroy
	@Override
	public void close() {
		regCenter.close();
		poolManager.close();
	}
	
	private String makeKey(String key){
		return String.format("%s$%s$%s", config.getInstance(),config.getNameSpace(),key);
	}
	
	@Override
	public String set(final String key, final String value) throws Exception{
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return  client.set(targetKey, value);						
			}

			@Override
			public String methodName() {
				
				return "default:set";
			}			
		}.run(targetKey,true);
	}
	
	@Override	
	public String get(final  String key) throws Exception{
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return client.get(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:get";
			}	
		}.run(targetKey,false);
	}

	@Override
	public Long delete(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.del(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:del";
			}	
		}.run(targetKey,false);
	}

	@Override
	public Long decr(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.decr(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:decr";
			}	
		}.run(targetKey,true);
	}

	@Override
	public Long incr(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.incr(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:incr";
			}	
		}.run(targetKey,true);
	}

	@Override
	public Boolean exists(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Boolean>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Boolean execute(Jedis client) {
				return client.exists(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:exists";
			}	
		}.run(targetKey,false);
	}

	@Override
	public Long hset(String key, final String field, final String value) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.hset(targetKey, field, value);
			}
			@Override
			public String methodName() {
				
				return "default:hset";
			}	
		}.run(targetKey,true);
	}

	@Override
	public String hget(String key, final String field) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return client.hget(targetKey, field);
			}
			@Override
			public String methodName() {
				
				return "default:hget";
			}	
		}.run(targetKey,false);
	}

	@Override
	public Map<String, String> hgetAll(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Map<String, String>>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Map<String, String> execute(Jedis client) {
				return client.hgetAll(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:hgetAll";
			}	
		}.run(targetKey,false);
	}

	@Override
	public Long rpush(String key, final String... strings) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.rpush(targetKey, strings);
			}
			@Override
			public String methodName() {
				
				return "default:rpush";
			}
		}.run(targetKey,true);
	}

	@Override
	public Long lpush(String key, final String... strings) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.lpush(targetKey, strings);
			}
			@Override
			public String methodName() {
				
				return "default:lpush";
			}
		}.run(targetKey,true);
	}

	@Override
	public String lpop(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return client.lpop(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:lpop";
			}
		}.run(targetKey,true);
	}

	@Override
	public String rpop(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return client.rpop(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:rpop";
			}
		}.run(targetKey,true);
	}

	@Override
	public List<String> lrange(String key, final long start, final long end)
			throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<List<String>>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public List<String> execute(Jedis client) {
				return client.lrange(targetKey, start, end);
			}
			@Override
			public String methodName() {
				
				return "default:lrange";
			}
		}.run(targetKey,false);
	}

	@Override
	public Long sadd(String key, final String... members) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.sadd(targetKey, members);
			}			
			@Override
			public String methodName() {
				
				return "default:sadd";
			}
		}.run(targetKey,true);
	}

	@Override
	public String spop(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return client.spop(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:spop";
			}
		}.run(targetKey,true);
	}

	@Override
	public Set<String> smembers(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Set<String>>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Set<String> execute(Jedis client) {
				return client.smembers(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:smembers";
			}
		}.run(targetKey,false);
	}

	@Override
	public Long zadd(String key, final double score, final String member) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.zadd(targetKey, score, member);
			}
			@Override
			public String methodName() {
				
				return "default:zadd";
			}
		}.run(targetKey,true);
	}

	@Override
	public Set<String> zrange(String key, final long start, final long end)
			throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Set<String>>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Set<String> execute(Jedis client) {
				return client.zrange(targetKey, start, end);
			}
			@Override
			public String methodName() {
				
				return "default:zrange";
			}
		}.run(targetKey,false);
	}

	@Override
	public Long zrem(String key, final String... member) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.zrem(targetKey, member);
			}
			@Override
			public String methodName() {
				
				return "default:zrem";
			}
		}.run(targetKey,true);
	}

	@Override
	public Long zcard(String key) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.zcard(targetKey);
			}
			@Override
			public String methodName() {
				
				return "default:zcard";
			}
		}.run(targetKey,false);
	}

	@Override
	public Long expire(String key, final int seconds) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<Long>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public Long execute(Jedis client) {
				return client.expire(targetKey, seconds);
			}
			@Override
			public String methodName() {
				
				return "default:expire";
			}
		}.run(targetKey,false);
	}

	@Override
	public String setex(String key, final String value, final int seconds) throws Exception {
		final String targetKey = makeKey(key);
		return new JedisClientCommand<String>(clusterManager,poolManager,config.getMaxRedirections()){

			@Override
			public String execute(Jedis client) {
				return client.setex(targetKey, seconds, value);
			}
			@Override
			public String methodName() {
				
				return "default:setex";
			}
		}.run(targetKey,true);
	}
	
	
}