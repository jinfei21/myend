package com.yjfei.padis.common;

import static com.yjfei.padis.common.Constant.DEFAULT_MAX_IDLE;
import static com.yjfei.padis.common.Constant.DEFAULT_MAX_TOTAL;
import static com.yjfei.padis.common.Constant.DEFAULT_MIN_IDLE;

import com.yjfei.padis.JedisxConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PoolConfig {

	private int maxTotal = DEFAULT_MAX_TOTAL;

	private int maxIdle = DEFAULT_MAX_IDLE;

	private int minIdle = DEFAULT_MIN_IDLE;
	
	private String passwd;

	public PoolConfig(int maxTotal, int maxIdle, int minIdle) {
		this.maxTotal = maxTotal;
		this.maxIdle = maxIdle;
		this.minIdle = minIdle;
	}
	
	public PoolConfig(JedisxConfig config){
    	this.maxTotal = config.getMaxTotal();
    	this.maxIdle = config.getMaxIdle();
    	this.minIdle = config.getMinIdle();
    	this.passwd = config.getPassword();
	}
}
