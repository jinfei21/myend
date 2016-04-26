package com.pingan.jinke.infra.padis.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Migrate {

	private long create;
	
	private long modify;
	
	private int slot_id;
	
	private int to_gid;
	
	private Status status;
	
	private int from_gid;
	
	private int percent;
	
	private int delay;
	
}
