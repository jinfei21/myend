package com.yjfei.padis.node;

import com.yjfei.padis.common.HostAndPort;
import com.yjfei.padis.common.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class Group {

	private int id;
	
	private Status status;
	
	private HostAndPort master;
		
	private HostAndPort slave;
	
	private long creatTime;
	
	private long modifyTime;

	
}
