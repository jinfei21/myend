package com.pingan.jinke.infra.padis.node;

import com.pingan.jinke.infra.padis.common.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
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
