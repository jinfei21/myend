package com.pingan.infra.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.yjfei.padis.JedisDirectClient;
import com.yjfei.padis.JedisxConfig;

@RunWith(Parameterized.class)
public class JedisClientTest {

	private JedisDirectClient client;

	@Parameters
	public static Collection data() {
		return Arrays.asList(new Object[][] { { "ca",// instance
				"ns",// namespace
				"10.20.22.87:2181", // zkAddr
				"admin"
		} });
	}

	public JedisClientTest(String instance, String namespace, String zkAddr, String password) {

		JedisxConfig config = new JedisxConfig(zkAddr,instance, namespace, password);

		client = new JedisDirectClient(config);
	}

	@Test
	public void test() {

		try {
			Random rand = new Random();
			for (int i = 0; i < 1000; i++) {
				long no = rand.nextLong();
				client.set("key" + no, "value" + no);

				System.out.println(client.get("key" + no));

				Assert.assertEquals("value" + no, client.get("key" + no));

				client.delete("key" + no);

				System.out.println(client.get("key" + no));

				Assert.assertEquals(null, client.get("key" + no));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	public void tearDown() {
		client.close();
	}

}
