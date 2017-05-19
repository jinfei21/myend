package com.yjfei.padis.client;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.Assert;

import com.google.common.collect.Lists;
import com.yjfei.padis.IJedisx;
import com.yjfei.padis.JedisDirectClient;
import com.yjfei.padis.JedisxConfig;

public class MultiJedisClientTest {

	public static void main(String args[]) {
		JedisxConfig config = new JedisxConfig();
		config.setInstance("test");
		config.setNameSpace("ns");
		config.setZkAddr("10.20.22.87:2181");
		config.setPassword("admin");
		JedisDirectClient client = new JedisDirectClient(config);

		ExecutorService service = Executors.newCachedThreadPool();
		List<Future> list = Lists.newArrayList();
		for (int i = 0; i < 5; i++) {
			Future f = service.submit(new ClientCallable(client));
			list.add(f);
		}

		for (Future f : list) {
			try {
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		service.shutdown();
		client.close();
	}

	static class ClientCallable implements Callable {

		private IJedisx client;

		public ClientCallable(IJedisx client) {
			this.client = client;
		}

		@Override
		public Object call() throws Exception {
			Random rand = new Random();
			for (int i = 0; i < 10000; i++) {
				long no = rand.nextLong();
				client.set("key" + no, "value" + no);

				System.out.println(client.get("key" + no));

				Assert.assertEquals("value" + no, client.get("key" + no));

				client.delete("key" + no);

				System.out.println(client.get("key" + no));

				Assert.assertEquals(null, client.get("key" + no));
			}
			return null;
		}

	}
}
