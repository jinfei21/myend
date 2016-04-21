package com.pingan.jinke.infra.padis.common;

public enum Status {
	PRE_MIGRATE("pre_migrate","预迁移"),
	OFFLINE("offline","下线"),
	ONLINE("online","上线"),
	MIGRATE("migrate","迁移"),
	LIMIT("limit","限流"),
	PENDING("pending","等待"),
	ERROR("error","错误");
	
	private String name;
	private String text;
	
	Status(String name,String text){
		this.name = name;
		this.text = text;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getText(){
		return this.text;
	}
	
	public String toString(){
		return this.name;
	}
	
	public static Status getStatus(String name){
		for(Status n:values()){
			if(n.name.equals(name.trim())){
				return n;
			}
		}
		return ONLINE;
	}
}
