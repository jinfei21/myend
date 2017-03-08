package com.yjfei.padis.node;

public class CustomNode {
	private String CUSTOM_ROOT;
	private String CUSTOM_FORMAT;
	
	public CustomNode(String  instance){
		this.CUSTOM_ROOT = "/instances/"+instance+"/clients";
		this.CUSTOM_FORMAT = CUSTOM_ROOT +"/%s";
	}
	
	public String getRootCustomPath(){
		return this.CUSTOM_ROOT;
	}
	
	public String getCustomPath(String ip){		
		return String.format(CUSTOM_FORMAT, ip);
	}
	
}
