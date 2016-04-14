package com.pingan.jinke.infra.padis.group;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

public class GroupService {

	private GroupNode groupNode;
	
	private NodeStorage nodeStorage;
	
	public GroupService(CoordinatorRegistryCenter coordinatorRegistryCenter){
		this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
		this.groupNode = new GroupNode();
		
	}
	
	public Group getGroup(int group_id){
		String groupPath = groupNode.getGroupPath(group_id);
		if(nodeStorage.isNodePathExisted(groupPath)){
			String data = nodeStorage.getNodePathDataDirectly(groupPath);
			Group group = JSON.parseObject(data, Group.class);		
			return group;
		}else{
			return null;
		}
	}
	
	public String getRootGroupPath(){
		return this.groupNode.getRootGroupPath();
	}
	
	public void delGroup(int group_id){
		nodeStorage.removeNodeIfExisted(groupNode.getGroupPath(group_id));
	}
	
	public List<Group> getAllGroups(){
		List<String> list = this.nodeStorage.getNodePathChildrenKeys(groupNode.getRootGroupPath());
		
		List<Group> groups = Lists.newArrayList();
		for(String node:list){
			String data = this.nodeStorage.getNodePathDataDirectly(groupNode.getRootGroupPath()+"/"+node);	
			Group group = JSON.parseObject(data, Group.class);	
			groups.add(group);
		}
		return groups;
	}
	
	public void replaceGroup(Group group){
		
		String groupPath = groupNode.getGroupPath(group.getId());
		String data = nodeStorage.getNodePathDataDirectly(groupPath);
		if(data == null){
			Group oldGroup = JSON.parseObject(data, Group.class);	
			group.setCreatTime(oldGroup.getCreatTime());
		}else{
			group.setCreatTime(System.currentTimeMillis());			
		}
		
		group.setModifyTime(System.currentTimeMillis());
		String json = JSON.toJSONString(group);
		this.nodeStorage.replaceNodePath(groupNode.getGroupPath(group.getId()), json);
	}
	
	public void addGroup(Group group){
		String groupPath = groupNode.getGroupPath(group.getId());
		
		if(nodeStorage.isNodePathExisted(groupPath)){
			throw new RuntimeException(String.format("group_%s is existed!",group.getId()));
		}else{
			group.setCreatTime(System.currentTimeMillis());
			group.setModifyTime(System.currentTimeMillis());
			String json = JSON.toJSONString(group);
			nodeStorage.replaceNodePath(groupNode.getGroupPath(group.getId()), json);
		}
	}
}
