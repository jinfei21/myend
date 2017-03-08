package com.yjfei.padis;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yjfei.padis.IJedisx;
import com.yjfei.padis.JedisxConfig;
import com.yjfei.padis.util.RegExceptionHandler;

public class JedisxClusterClient extends AbstractClusterClient implements IJedisx{
	
	public JedisxClusterClient(JedisxConfig config){
		super(config);
	}

	@Override
	public void setNameSpace(String nameSpace) {
		super.setNameSpace(nameSpace);
	}
	
	@Override
	public String get(String key) throws Exception {	
		check();
		return jedisCluster.get(makeKey(key));
	}

	@Override
	public String set(String key, String value) throws Exception {
		check();
		return jedisCluster.set(makeKey(key), value);
	}

	@Override
	public Long delete(String key) throws Exception {
		check();
		return jedisCluster.del(key);
	}

	@Override
	public void close() {
		try {
			clusterConfig.close();
			jedisCluster.close();			
		} catch (IOException e) {
			RegExceptionHandler.handleException(e);
		}
	}

	@Override
	public Long decr(String key) throws Exception {
		check();
		return jedisCluster.decr(key);
	}

	@Override
	public Long incr(String key) throws Exception {
		check();
		return jedisCluster.incr(key);
	}

	@Override
	public Boolean exists(String key) throws Exception {
		check();
		return jedisCluster.exists(key);
	}

	@Override
	public Long hset(String key, String field, String value) throws Exception {
		check();
		return jedisCluster.hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) throws Exception {
		check();
		return jedisCluster.hget(key, field);
	}

	@Override
	public Map<String, String> hgetAll(String key) throws Exception {
		check();
		return jedisCluster.hgetAll(key);
	}

	@Override
	public Long rpush(String key, String... strings) throws Exception {
		check();
		return jedisCluster.rpush(key, strings);
	}

	@Override
	public Long lpush(String key, String... strings) throws Exception {
		check();
		return jedisCluster.lpush(key, strings);
	}

	@Override
	public String lpop(String key) throws Exception {
		check();
		return jedisCluster.lpop(key);
	}

	@Override
	public String rpop(String key) throws Exception {
		check();
		return jedisCluster.rpop(key);
	}

	@Override
	public List<String> lrange(String key, long start, long end)
			throws Exception {
		check();
		return jedisCluster.lrange(key, start, end);
	}

	@Override
	public Long sadd(String key, String... members) throws Exception {
		check();
		return jedisCluster.sadd(key, members);
	}

	@Override
	public String spop(String key) throws Exception {
		check();
		return jedisCluster.spop(key);
	}

	@Override
	public Set<String> smembers(String key) throws Exception {
		check();
		return jedisCluster.smembers(key);
	}

	@Override
	public Long zadd(String key, double score, String member) throws Exception {
		check();
		return jedisCluster.zadd(key, score, member);
	}

	@Override
	public Set<String> zrange(String key, long start, long end)
			throws Exception {
		check();
		return jedisCluster.zrange(key, start, end);
	}

	@Override
	public Long zrem(String key, String... member) throws Exception {
		check();
		return jedisCluster.zrem(key, member);
	}

	@Override
	public Long zcard(String key) throws Exception {
		check();
		return jedisCluster.zcard(key);
	}

	@Override
	public Long expire(String key, int seconds) throws Exception {
		check();
		return jedisCluster.expire(key, seconds);
	}

	@Override
	public String setex(String paramString1, String paramString2, int paramInt)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
