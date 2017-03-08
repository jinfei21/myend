package com.yjfei.padis.custom;

import static com.yjfei.padis.common.Status.ONLINE;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.node.Custom;
import com.yjfei.padis.node.CustomNode;
import com.yjfei.padis.storage.NodeStorage;
import com.yjfei.padis.util.IPUtils;

public class CustomService {
	private CustomNode customNode;
	
	private NodeStorage nodeStorage;
	
	private String customPath;
	
	public CustomService(String instance,CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.customNode = new CustomNode(instance);
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
	}
	
	public String getRootCustomPath(){
		return this.customNode.getRootCustomPath();
	}
	
	public String getLocalCustomPath(){
		return customPath;
	}
	
	public void registerCustom(){
		String ip = IPUtils.getIP();
		Custom custom = new Custom();
		custom.setCreate(System.currentTimeMillis());
		custom.setModify(System.currentTimeMillis());
		custom.setHost(ip);
		custom.setStatus(ONLINE);
		custom.setLimit(-1);
		String data = JSON.toJSONString(custom);
		customPath = nodeStorage.fillEphemeralSeqNodePath(customNode.getCustomPath(ip),data);
	}
	
	public Custom getLocalCustom(){	
		String data = this.nodeStorage.getNodePathDataDirectly(customPath);		
		Custom custom = JSON.parseObject(data, Custom.class);		
		return custom;
	}
	
	public Custom getLocalCustom(String node){
		String data = this.nodeStorage.getNodePathDataDirectly(customNode.getCustomPath(node));		
		Custom custom = JSON.parseObject(data, Custom.class);		
		return custom;
	}
	
	public Custom updateCustom(Custom custom) {
		Custom old = getLocalCustom();
		if (old != null) {
			custom.setCreate(old.getCreate());
			custom.setModify(System.currentTimeMillis());
			custom.setHost(old.getHost());
			String data = JSON.toJSONString(custom);
			this.nodeStorage.updateNodePath(customPath, data);
			return custom;
		} else {
			return null;
		}
	}
	
	public  Custom updateCustom(Custom custom,String node){
		Custom old = getLocalCustom(node);
		if (old != null) {
			custom.setCreate(old.getCreate());
			custom.setModify(System.currentTimeMillis());
			String data = JSON.toJSONString(custom);
			this.nodeStorage.updateNodePath(customNode.getCustomPath(node), data);
			return custom;
		} else {
			return null;
		}
		
	}
	
	
	public List<String> getAllCustomNode(){
		return this.nodeStorage.getNodePathChildrenKeys(customNode.getRootCustomPath());
	}
	
	public List<Custom> getAllCustom(){
		List<Custom> list = Lists.newArrayList();
		for(String node:getAllCustomNode()){
			Custom custom = getLocalCustom(customNode.getCustomPath(node));
		}
		return list;
	}
}
