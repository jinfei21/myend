package com.pingan.jinke.infra.padis.common;

public class Constant {
	public static final String CHARSET = "UTF-8";

	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = 6379;
	public static final int DEFAULT_TIMEOUT = 2000;
	public static final int DEFAULT_DATABASE = 0;

	public static final byte DOLLAR_BYTE = '$';
	public static final byte ASTERISK_BYTE = '*';
	public static final byte PLUS_BYTE = '+';
	public static final byte MINUS_BYTE = '-';
	public static final byte COLON_BYTE = ':';
	
	
	public static final String ASK_RESPONSE = "ASK";
	public static final String MOVED_RESPONSE = "MOVED";
	public static final String CLUSTERDOWN_RESPONSE = "CLUSTERDOWN";
	
	
	public static final String ZK_DIRECT_NAME_SPACE = "padis";
	
	public static final String ZK_Cluster_NAME_SPACE = "padis-cluster";
	
	
			
    public static final int DEFAULT_MAX_TOTAL = 8;

    public static final int DEFAULT_MAX_IDLE = 8;

    public static final int DEFAULT_MIN_IDLE = 0;
    
    public static final int DEFAULT_MAX_DIRECTION = 3;

    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;

    public static final int DEFAULT_SO_TIMEOUT = 3000;
}
