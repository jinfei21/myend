package com.pingan.jinke.infra.padis.node;

public class GroupNode {

	private String GROUP_ROOT;
	private String GROUP_ID_FORMAT;
	private String GROUP_NODE_FORMAT;
	
	public GroupNode(){
		this.GROUP_ROOT = "/servers";
		this.GROUP_ID_FORMAT = GROUP_ROOT + "/group_%d";
		this.GROUP_NODE_FORMAT = GROUP_ROOT + "/%s";
	}
	
	public String getGroupPath(int id){
		return String.format(GROUP_ID_FORMAT, id);
	}
	
	public String getGroupPath(String node){
		return String.format(GROUP_NODE_FORMAT, node);
	}
	
	public String getRootGroupPath(){
		return this.GROUP_ROOT;
	}
}
