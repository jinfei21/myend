package com.pingan.jinke.infra.padis.group;

public class GroupNode {

	private String GROUP_ROOT;
	private String GROUP_FORMAT;

	
	public GroupNode(){
		this.GROUP_ROOT = "/servers";
		this.GROUP_FORMAT = GROUP_ROOT + "/group_%d";
	}
	
	public String getGroupPath(int id){
		return String.format(GROUP_FORMAT, id);
	}
	
	public String getRootGroupPath(){
		return this.GROUP_ROOT;
	}
}
