package com.yjfei.padis;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IJedisx {

	String get(String key) throws Exception;
	
	String set(String key, String value) throws Exception;
	
	String setex(String key, String value, int seconds) throws Exception;
	
	Long delete(String key) throws Exception;
	
	Long expire(String key, int seconds) throws Exception;

	Long decr(String key) throws Exception;
	
	Long incr(String key) throws Exception;
	
	Boolean exists(String key) throws Exception;
	
	Long hset(String key, String field, String value) throws Exception;
	
	String hget(String key, String field) throws Exception;
	
	Map<String, String> hgetAll(String key) throws Exception;
	
	Long rpush(String key, String... strings) throws Exception;
	
	Long lpush(String key, String... strings) throws Exception;
	
	String lpop(String key) throws Exception;
	
	String rpop(String key) throws Exception;
	
	List<String> lrange(String key, long start, long end) throws Exception;
	
	Long sadd(String key, String... members) throws Exception;
	
	String spop(String key) throws Exception;
	
	Set<String> smembers(String key) throws Exception;
	
	Long zadd(String key, double score, String member) throws Exception;

	Set<String> zrange(String key, long start, long end) throws Exception;

	Long zrem(String key, String... member) throws Exception;

	Long zcard(String key) throws Exception;
	
	void setNameSpace(String nameSpace);
	
	void close();
}
