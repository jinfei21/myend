package com.pingan.jinke.infra.padis.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisCommand {
	
	/**
	 * This command is often used to test if a connection is still alive, or to
	 * measure latency.
	 *
	 * @return PONG
	 */
	String ping();

	Long delete(String key);
	
	String set(String key, String value);

	String set(String key, String value, SetParams params);

	String get(String key);

	Long decrBy(String key, long integer);

	Long decr(String key);

	Long incrBy(String key, long integer);

	Double incrByFloat(String key, double value);

	Long incr(String key);

	Long append(String key, String value);

	String substr(String key, int start, int end);

	Boolean exists(String key);

	Long persist(String key);

	String type(String key);

	Long expire(String key, int seconds);

	Long pexpire(String key, long milliseconds);

	Long expireAt(String key, long unixTime);

	Long pexpireAt(String key, long millisecondsTimestamp);

	Long ttl(String key);

	Long pttl(final String key);

	Long hset(String key, String field, String value);

	String hget(String key, String field);

	Map<String, String> hgetAll(String key);

	List<String> hmget(String key, String... fields);

	String hmset(final String key, final Map<String, String> hash);

	String migrate(final String host, final int port, final String key, final int destinationDb, final int timeout);

	Long rpush(String key, String... string);

	Long lpush(String key, String... string);

	String lpop(String key);

	String rpop(String key);

	Long llen(String key);

	List<String> lrange(String key, long start, long end);

	String ltrim(String key, long start, long end);

	String lindex(String key, long index);

	String lset(String key, long index, String value);

	Long sadd(String key, String... member);

	Set<String> smembers(String key);

	Long scard(String key);

	Long srem(String key, String... member);

	String spop(String key);

	Long zadd(String key, double score, String member);

	Long zadd(String key, double score, String member, ZAddParams params);

	Long zadd(String key, Map<String, Double> scoreMembers);

	Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params);

	Set<String> zrange(String key, long start, long end);

	Long zrem(String key, String... member);

	Long zcard(String key);

	Double zscore(String key, String member);

	List<String> sort(String key);

	List<String> sort(String key, SortingParams sortingParameters);

}
