package com.yjfei.padis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.yjfei.padis.client.JedisClientTest;
import com.yjfei.padis.custom.CustomServiceTest;
import com.yjfei.padis.group.GroupServiceTest;
import com.yjfei.padis.slot.SlotServiceTest;


@RunWith(Suite.class)
@SuiteClasses({
	SlotServiceTest.class,
	GroupServiceTest.class,
	CustomServiceTest.class,
	JedisClientTest.class,
})
public class AllTests {


}
