package com.yjfei.padis;

import static com.yjfei.padis.common.Constant.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class JedisxConfig {

	private String instance;
	
	private String nameSpace;
	
	private String zkAddr;
	
	private int maxRedirections = DEFAULT_MAX_DIRECTION;
	
	private int connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
	
	private int soTimeout = DEFAULT_SO_TIMEOUT;	
	
    private int maxTotal = DEFAULT_MAX_TOTAL;

    private int maxIdle = DEFAULT_MAX_IDLE;

    private int minIdle = DEFAULT_MIN_IDLE;
    
    private int nestedPort = -1;
    
    private String nestedDataDir;
    
    private String password;
    
    public JedisxConfig(JedisxConfig config){
    	this.instance = config.instance;
    	this.nameSpace = config.nameSpace;
    	this.zkAddr = config.zkAddr;
    	this.maxRedirections = config.maxRedirections;
    	this.connectionTimeout = config.connectionTimeout;
    	this.soTimeout = config.soTimeout;
    	this.maxTotal = config.maxTotal;
    	this.maxIdle = config.maxIdle;
    	this.minIdle = config.minIdle;
    	this.password = config.password;
    }
    
    public JedisxConfig(String zkAddr,String instance, String namespace, String password){
    	this.nameSpace = namespace;
    	this.zkAddr = zkAddr;
    	this.instance = instance;
    	this.password = password;
    }
    
    
}