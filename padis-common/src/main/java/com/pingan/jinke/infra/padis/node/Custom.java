package com.pingan.jinke.infra.padis.node;

import java.util.Map;

import com.google.common.collect.Maps;
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
	
	public Map<String,String> toMap(){
		Map map = Maps.newHashMap();
		map.put("create", create);
		map.put("modify", modify);
		map.put("host", host);
		map.put("limit", limit);
		map.put("status", status.toString());
		return map;
	}
}
