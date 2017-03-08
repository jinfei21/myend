package com.yjfei.padis.node;

import java.util.Map;

import com.google.common.collect.Maps;
import com.yjfei.padis.common.Status;

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
	
	private long sleep = 100;
	
	public Map<String,String> toMap(){
		Map map = Maps.newHashMap();
		map.put("create", create);
		map.put("modify", modify);
		map.put("host", host);
		map.put("limit", limit);
		map.put("sleep", sleep);
		map.put("status", status.toString());
		return map;
	}
}
