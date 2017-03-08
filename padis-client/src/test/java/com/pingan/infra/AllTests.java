package com.pingan.infra;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.pingan.infra.client.JedisClientTest;
import com.pingan.infra.custom.CustomServiceTest;
import com.pingan.infra.group.GroupServiceTest;
import com.pingan.infra.slot.SlotServiceTest;


@RunWith(Suite.class)
@SuiteClasses({
	SlotServiceTest.class,
	GroupServiceTest.class,
	CustomServiceTest.class,
	JedisClientTest.class,
})
public class AllTests {


}
