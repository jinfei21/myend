package com.pingan.jinke.infra.padis.custom;

import com.pingan.jinke.infra.padis.common.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Custom {
	private long create;
		
	private long modify;
	
	private String host;
	
	private int limit;
	
	private Status status;
}
