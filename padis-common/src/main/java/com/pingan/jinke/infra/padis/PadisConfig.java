package com.pingan.jinke.infra.padis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static com.pingan.jinke.infra.padis.common.Constant.*;

@NoArgsConstructor
@Setter
@Getter
public class PadisConfig {

	private String instance;
	
	private String nameSpace;
	
	private String zkAddr;
	
	private int maxRedirections = DEFAULT_MAX_DIRECTION;
	
	private int connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
	
	private int soTimeout = DEFAULT_SO_TIMEOUT;	
	
    private int maxTotal = DEFAULT_MAX_TOTAL;

    private int maxIdle = DEFAULT_MAX_IDLE;

    private int minIdle = DEFAULT_MIN_IDLE;
    
    public PadisConfig(PadisConfig config){
    	this.instance = config.instance;
    	this.nameSpace = config.nameSpace;
    	this.zkAddr = config.zkAddr;
    	this.maxRedirections = config.maxRedirections;
    	this.connectionTimeout = config.connectionTimeout;
    	this.soTimeout = config.soTimeout;
    	this.maxTotal = config.maxTotal;
    	this.maxIdle = config.maxIdle;
    	this.minIdle = config.minIdle;
    }
    
    public PadisConfig(String zkAddr,String instance, String namespace){
    	this.nameSpace = namespace;
    	this.zkAddr = zkAddr;
    	this.instance = instance;
    }
}