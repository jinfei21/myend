package com.yjfei.cache.padis.core;

import static com.yjfei.cache.padis.common.ProtocolCommand.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yjfei.cache.padis.common.Connection;
import com.yjfei.cache.padis.common.HostAndPort;
import com.yjfei.cache.padis.common.RedisCommand;
import com.yjfei.cache.padis.common.SetParams;
import com.yjfei.cache.padis.common.SortingParams;
import com.yjfei.cache.padis.common.ZAddParams;
import com.yjfei.cache.padis.util.BuilderFactory;
import com.yjfei.cache.padis.util.SafeEncoder;

public class Client extends Connection implements RedisCommand {

	public static final byte[] BYTES_TRUE = toByteArray(1);
	public static final byte[] BYTES_FALSE = toByteArray(0);
	public static final byte[][] EMPTY_ARGS = new byte[0][];
	
	private long createTime;

	public Client(HostAndPort hostPort) {
		super(hostPort);
		this.createTime = System.currentTimeMillis();
	}
	
	public long getCreateTime(){
		return this.createTime;
	}
	
	public void updateTTL(){
		this.createTime = System.currentTimeMillis();
	}

	@Override
	public String set(String key, String value) {
		sendCommand(SET, key, value);
		return getStatusCodeReply();
	}

	@Override
	public Long delete(String key) {
		sendCommand(DEL, key);
		return getIntegerReply();
	}
	
	@Override
	public String set(String key, String value, SetParams params) {
		sendCommand(SET, params.getByteParams(SafeEncoder.encode(key), SafeEncoder.encode(value)));
		return getStatusCodeReply();
	}

	@Override
	public String get(String key) {
		sendCommand(GET, key);
		return getBulkReply();
	}

	@Override
	public Long decrBy(String key, long integer) {
		sendCommand(DECRBY, SafeEncoder.encode(key), toByteArray(integer));
		return getIntegerReply();
	}

	@Override
	public Long decr(String key) {
		sendCommand(DECR, key);
		return getIntegerReply();
	}

	@Override
	public Long incrBy(String key, long integer) {
		sendCommand(INCRBY, SafeEncoder.encode(key), toByteArray(integer));
		return getIntegerReply();
	}

	@Override
	public Double incrByFloat(String key, double value) {
		sendCommand(INCRBYFLOAT, SafeEncoder.encode(key), toByteArray(value));
		String dval = getBulkReply();
		return (dval != null ? new Double(dval) : null);
	}

	@Override
	public Long incr(String key) {
		sendCommand(INCR, key);
		return getIntegerReply();
	}

	@Override
	public Long append(String key, String value) {
		sendCommand(APPEND, key, value);
		return getIntegerReply();
	}

	@Override
	public String substr(String key, int start, int end) {
		sendCommand(SUBSTR, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
		return getBulkReply();
	}

	@Override
	public Boolean exists(String key) {
		sendCommand(EXISTS, key);
		return getIntegerReply() == 1;
	}

	@Override
	public Long persist(String key) {
		sendCommand(PERSIST, key);
		return getIntegerReply();
	}

	@Override
	public String type(String key) {
		sendCommand(TYPE, key);
		return getStatusCodeReply();
	}

	@Override
	public Long expire(String key, int seconds) {
		sendCommand(EXPIRE, SafeEncoder.encode(key), toByteArray(seconds));
		return getIntegerReply();
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		sendCommand(PEXPIRE, SafeEncoder.encode(key), toByteArray(milliseconds));
		return getIntegerReply();
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		sendCommand(EXPIREAT, SafeEncoder.encode(key), toByteArray(unixTime));
		return getIntegerReply();
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {

		sendCommand(PEXPIREAT, SafeEncoder.encode(key), toByteArray(millisecondsTimestamp));
		return getIntegerReply();
	}

	@Override
	public Long ttl(String key) {
		sendCommand(TTL, key);
		return getIntegerReply();
	}

	@Override
	public Long pttl(String key) {
		sendCommand(PTTL, key);
		return getIntegerReply();
	}

	@Override
	public Long hset(String key, String field, String value) {
		sendCommand(HSET, key, field, value);
		return getIntegerReply();
	}

	@Override
	public String hget(String key, String field) {
		sendCommand(HGET, key, field);
		return getBulkReply();
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		sendCommand(HGETALL, key);
		return BuilderFactory.STRING_MAP.build(getBinaryMultiBulkReply());
	}

	@Override
	public String hmset(final String key, final Map<String, String> hash) {
		final List<byte[]> params = new ArrayList<byte[]>();
		params.add(SafeEncoder.encode(key));

		for (final Entry<String, String> entry : hash.entrySet()) {
			params.add(SafeEncoder.encode(entry.getKey()));
			params.add(SafeEncoder.encode(entry.getValue()));
		}

		sendCommand(HMSET, params.toArray(new byte[params.size()][]));
		return getStatusCodeReply();
	}

	@Override
	public List<String> hmget(String key, String... fields) {

		final byte[][] bfields = new byte[fields.length][];
		for (int i = 0; i < bfields.length; i++) {
			bfields[i] = SafeEncoder.encode(fields[i]);
		}
		final byte[][] params = new byte[fields.length + 1][];
		params[0] = SafeEncoder.encode(key);
		System.arraycopy(bfields, 0, params, 1, fields.length);
		sendCommand(HMGET, params);
		return BuilderFactory.STRING_LIST.build(getBinaryMultiBulkReply());
	}

	@Override
	public String migrate(String host, int port, String key, int destinationDb, int timeout) {
		sendCommand(MIGRATE, SafeEncoder.encode(host), toByteArray(port), SafeEncoder.encode(key),
				toByteArray(destinationDb), toByteArray(timeout));
		return getStatusCodeReply();
	}

	@Override
	public Long rpush(String key, String... strings) {
		sendCommand(RPUSH, joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(strings)));
		return getIntegerReply();
	}

	@Override
	public Long lpush(String key, String... strings) {
		sendCommand(LPUSH, joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(strings)));
		return getIntegerReply();
	}

	@Override
	public String lpop(String key) {
		sendCommand(LPOP, key);
		return getBulkReply();
	}

	@Override
	public String rpop(String key) {
		sendCommand(RPOP, key);
		return getBulkReply();
	}

	@Override
	public Long llen(String key) {
		sendCommand(LLEN, key);
		return getIntegerReply();
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		sendCommand(LRANGE, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
		return BuilderFactory.STRING_LIST.build(getBinaryMultiBulkReply());
	}

	@Override
	public String ltrim(String key, long start, long end) {
		sendCommand(LTRIM, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
		return getStatusCodeReply();
	}

	@Override
	public String lindex(String key, long index) {
		sendCommand(LINDEX, SafeEncoder.encode(key), toByteArray(index));
		return getBulkReply();
	}

	@Override
	public String lset(String key, long index, String value) {
		sendCommand(LSET, SafeEncoder.encode(key), toByteArray(index), SafeEncoder.encode(value));
		return getStatusCodeReply();
	}

	@Override
	public Long sadd(String key, String... members) {
		sendCommand(SADD, joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(members)));
		return getIntegerReply();
	}

	@Override
	public Set<String> smembers(String key) {
		sendCommand(SMEMBERS, key);
		return BuilderFactory.STRING_SET.build(getBinaryMultiBulkReply());
	}

	@Override
	public Long scard(String key) {
		sendCommand(SCARD, key);
		return getIntegerReply();
	}

	@Override
	public Long srem(String key, String... members) {
		sendCommand(SREM, joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(members)));
		return getIntegerReply();
	}

	@Override
	public String spop(String key) {
		sendCommand(SPOP, key);
		return getBulkReply();
	}

	@Override
	public Long zadd(String key, double score, String member) {
		sendCommand(ZADD, SafeEncoder.encode(key), toByteArray(score), SafeEncoder.encode(member));
		return getIntegerReply();
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {
		sendCommand(ZADD,
				params.getByteParams(SafeEncoder.encode(key), toByteArray(score), SafeEncoder.encode(member)));
		return getIntegerReply();
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {

		ArrayList<byte[]> args = new ArrayList<byte[]>(scoreMembers.size() * 2 + 1);
		args.add(SafeEncoder.encode(key));
		for (Entry<String, Double> entry : scoreMembers.entrySet()) {
			args.add(toByteArray(entry.getValue()));
			args.add(SafeEncoder.encode(entry.getKey()));
		}

		byte[][] argsArray = new byte[args.size()][];
		args.toArray(argsArray);

		sendCommand(ZADD, argsArray);

		return getIntegerReply();
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		ArrayList<byte[]> args = new ArrayList<byte[]>(scoreMembers.size() * 2 + 1);
		args.add(SafeEncoder.encode(key));
		for (Entry<String, Double> entry : scoreMembers.entrySet()) {
			args.add(toByteArray(entry.getValue()));
			args.add(SafeEncoder.encode(entry.getKey()));
		}
		byte[][] argsArray = new byte[args.size()][];
		args.toArray(argsArray);

		sendCommand(ZADD, params.getByteParams(SafeEncoder.encode(key), argsArray));

		return getIntegerReply();
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {

		sendCommand(ZRANGE, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
		return BuilderFactory.STRING_SET.build(getBinaryMultiBulkReply());
	}

	@Override
	public Long zrem(String key, String... members) {
		sendCommand(ZREM, joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(members)));
		return getIntegerReply();
	}

	@Override
	public Long zcard(String key) {
		sendCommand(ZCARD, key);
		return getIntegerReply();
	}

	@Override
	public Double zscore(String key, String member) {
		sendCommand(ZSCORE, key, member);
		final String score = getBulkReply();
		return (score != null ? new Double(score) : null);
	}

	@Override
	public List<String> sort(String key) {
		sendCommand(SORT, key);
		return BuilderFactory.STRING_LIST.build(getBinaryMultiBulkReply());
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
	    final List<byte[]> args = new ArrayList<byte[]>();
	    args.add(SafeEncoder.encode(key));
	    args.addAll(sortingParameters.getParams());
	    sendCommand(SORT, args.toArray(new byte[args.size()][]));
		return BuilderFactory.STRING_LIST.build(getBinaryMultiBulkReply());
	}
	
	@Override
	public String ping() {
		sendCommand(PING,EMPTY_ARGS);		
		return getStatusCodeReply();
	}
	
	@Override
	public Set<String> keys(String pattern) {
		sendCommand(KEYS, pattern);
		return BuilderFactory.STRING_SET.build(getBinaryMultiBulkReply());
	}

	private byte[][] joinParameters(byte[] first, byte[][] rest) {
		byte[][] result = new byte[rest.length + 1][];
		result[0] = first;
		System.arraycopy(rest, 0, result, 1, rest.length);
		return result;
	}

	public static final byte[] toByteArray(final boolean value) {
		return value ? BYTES_TRUE : BYTES_FALSE;
	}

	public static final byte[] toByteArray(final int value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public static final byte[] toByteArray(final long value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public static final byte[] toByteArray(final double value) {
		return SafeEncoder.encode(String.valueOf(value));
	}


}
