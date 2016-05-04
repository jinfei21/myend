package com.yjfei.cache.padis.node;

import com.yjfei.cache.padis.common.HostAndPort;
import com.yjfei.cache.padis.common.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Group {

	private int id;
	
	private Status status;
	
	private HostAndPort master;
		
	private HostAndPort slave;
	
	private long creatTime;
	
	private long modifyTime;

	
}
