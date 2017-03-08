package com.pingan.infra.group;

import static org.mockito.Mockito.when;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.common.HostAndPort;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.group.GroupService;
import com.yjfei.padis.node.Group;
import com.yjfei.padis.node.GroupNode;

public class GroupServiceTest {

	private GroupService groupService;
	
	@Mock
	private CoordinatorRegistryCenter coordinatorRegistryCenter;
	
	@Before
	public void initMocks(){
		MockitoAnnotations.initMocks(this);
		
		Group group = new Group(1,Status.ONLINE,new HostAndPort("10.20.22.87",6379),new HostAndPort("10.20.22.87",6479),System.currentTimeMillis(),System.currentTimeMillis());
		GroupNode groupNode = new GroupNode();
		
		this.groupService = new GroupService(coordinatorRegistryCenter);
		List<String> nodes = Lists.newArrayList();
		for(int i=0;i<5;i++){
			group.setId(i);
			when(coordinatorRegistryCenter.getDirectly(groupNode.getGroupPath(i))).thenReturn(JSON.toJSONString(group));
			when(coordinatorRegistryCenter.isExisted(groupNode.getGroupPath(i))).thenReturn(true);
			nodes.add("group_"+i);
		}
		when(coordinatorRegistryCenter.getChildrenKeys(groupNode.getRootGroupPath())).thenReturn(nodes);

	}
	
	@Test
	public void testList(){
		List<Group> list =  groupService.getAllGroups();
		Assert.assertEquals(5, list.size());
		Assert.assertEquals(0, list.get(0).getId());
		Assert.assertEquals(1, list.get(1).getId());
		Assert.assertEquals(2, list.get(2).getId());
		Assert.assertEquals(3, list.get(3).getId());
		Assert.assertEquals(4, list.get(4).getId());

	}
	
	@Test 
	public void testGet(){
		Group group = groupService.getGroup(0);
		Assert.assertEquals(0, group.getId());
	}
	
	@Test
	public void testUpdate(){
		Group group = groupService.getGroup(1);
		group.setStatus(Status.OFFLINE);
		groupService.replaceGroup(group);
	}
}
