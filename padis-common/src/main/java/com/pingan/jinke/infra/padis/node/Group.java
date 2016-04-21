package com.pingan.jinke.infra.padis.node;

import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.Status;

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
