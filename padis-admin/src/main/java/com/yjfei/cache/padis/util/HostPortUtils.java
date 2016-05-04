package com.yjfei.cache.padis.util;

import com.yjfei.cache.padis.common.HostAndPort;

public class HostPortUtils {
	
	
	public static HostAndPort mapHostAndPort(String host){
		HostAndPort hostPort = new HostAndPort();
		String[] hostStr = host.trim().split(":");
		hostPort.setHost(hostStr[0]);
		hostPort.setPort(Integer.valueOf(hostStr[1]));
		return hostPort;
	}

}
