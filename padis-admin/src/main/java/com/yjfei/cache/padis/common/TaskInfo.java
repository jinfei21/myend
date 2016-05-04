package com.yjfei.cache.padis.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskInfo {
	private String instance;
	private int from;
	private int to;
	
	public TaskInfo(String instance, int from, int to) {
		this.from = from;
		this.to = to;
		this.instance = instance;
	}
}
