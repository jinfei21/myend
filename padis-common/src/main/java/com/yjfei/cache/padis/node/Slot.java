package com.yjfei.cache.padis.node;

import com.yjfei.cache.padis.common.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Slot {

	private int id;
	
	private Status status;
	
	private long create;
	
	private int src_gid;
	
	private int to_gid;

	private long modify;
		
}
