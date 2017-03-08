package com.yjfei.padis.metric;

import lombok.Getter;

@Getter
public enum SendPolicy {
	Random("random"),
	RoundRobin("roundRobin");
	
	private String value;
	
	SendPolicy(String value){
		this.value = value;
	}
	
	
	
	public static SendPolicy getSendPolicy(String value){
		
		for(SendPolicy entry:SendPolicy.values()){
			if(entry.getValue().equalsIgnoreCase(value)){
				return entry;
			}
		}
		
		return Random;
	}
}
