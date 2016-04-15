package com.pingan.jinke.infra.padis.custom;

import static com.pingan.jinke.infra.padis.common.Status.ONLINE;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.NodeStorage;
import com.pingan.jinke.infra.padis.util.NetUtils;

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
		String ip = NetUtils.getIP();
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
		return getLocalCustom(customPath);
	}
	
	public Custom getLocalCustom(String path){
		String data = this.nodeStorage.getNodePathDataDirectly(customPath);		
		Custom custom = JSON.parseObject(data, Custom.class);		
		return custom;
	}
	
	public Custom updateCustom(Custom custom){
		Custom old =  getLocalCustom();
		custom.setCreate(old.getCreate());
		custom.setModify(System.currentTimeMillis());
		String data = JSON.toJSONString(custom);
		this.nodeStorage.updateNodePath(customPath, data);
		return custom;
	}
	
	
	public List<String> getAllCustomPath(){
		return this.nodeStorage.getNodePathChildrenKeys(customNode.getRootCustomPath());
	}
}
