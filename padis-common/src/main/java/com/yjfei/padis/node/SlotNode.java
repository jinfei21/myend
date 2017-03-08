package com.yjfei.padis.node;

public class SlotNode {

	private String SLOT_ROOT;
	private String SLOT_FORMAT;
	
	public SlotNode(String instance){
		this.SLOT_ROOT = "/instances/"+instance+"/slots";
		this.SLOT_FORMAT = SLOT_ROOT+"/slot_%d";
	}
	
	public String getSlotPath(int id){
		return String.format(SLOT_FORMAT, id);
	}
	
	public String getRootSlotPath(){
		return SLOT_ROOT;
	}
}
