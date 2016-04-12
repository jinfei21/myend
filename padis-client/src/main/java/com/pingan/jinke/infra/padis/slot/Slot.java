package com.pingan.jinke.infra.padis.slot;

import java.util.Date;

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
	
	private Date create;
	
	private int src_gid;
	
	private Date modify;
	
	public int to_gid;
	
}
