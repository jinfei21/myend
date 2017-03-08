package com.pingan.infra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.common.base.Joiner;
import com.yjfei.padis.common.ZookeeperConfiguration;
import com.yjfei.padis.storage.ZookeeperRegistryCenter;

public abstract class AbstractNestedZookeeperBaseTest {

	public static final int PORT = 3181;
	
	public static final String TMP_DIR = String.format("target/test_zk_data/%s/", System.nanoTime());
	
	public static final String ZK_CONNECTION = Joiner.on(":").join("localhost",PORT);
		
	protected static ZookeeperRegistryCenter zkRegCenter;

	
	@BeforeClass
	public static void setUp(){
		zkRegCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(ZK_CONNECTION, Thread.currentThread().getStackTrace()[1].getMethodName(), 1000, 3000, 3,PORT,TMP_DIR));
		zkRegCenter.init();
	}
	
	@AfterClass
	public static void tearDown(){
		zkRegCenter.close();
	}
}
