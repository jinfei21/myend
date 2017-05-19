package com.yjfei.padis.custom;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.yjfei.padis.AbstractNestedZookeeperBaseTest;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.custom.CustomService;
import com.yjfei.padis.node.Custom;
import com.yjfei.padis.util.IPUtils;

public class CustomServiceTest extends AbstractNestedZookeeperBaseTest {

	private CustomService customService;

	@Before
	public void setUpCase() {
		customService = new CustomService("test", zkRegCenter);
		customService.registerCustom();
	}
	
	
	@Test
	public void testGet(){
		Custom custom = customService.getLocalCustom();		
		Assert.assertEquals(custom.getHost(), IPUtils.getIP());
	}
	
	@Test
	public void testUpdate(){
		Custom custom = customService.getLocalCustom();	
		custom.setStatus(Status.LIMIT);
		customService.updateCustom(custom);
		Assert.assertEquals(Status.LIMIT, customService.getLocalCustom().getStatus());
	}
}
