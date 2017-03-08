package comyjfei.padis.metric.send;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

import com.yjfei.padis.metric.IStorage;
import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.MetricData;
import com.yjfei.padis.util.SleepUtils;

@Slf4j
public class SendAgentGroup {

	private boolean runing;

	private SendAgent[] workers;

	private AtomicLong position;
	
	private IStorage storage;

	public SendAgentGroup(MetricConfig config) {
		this.workers = new SendAgent[config.getWorkerSize()];
		for (int i = 0; i < workers.length; i++) {
			this.workers[i] = new SendAgent(config.getWorkerBufSize());
		}
		this.position = new AtomicLong(0);
		this.storage = StorageFactory.getStorage(config);
	}

	public void start() {
		if (!this.runing) {
			this.runing = true;
			for (int i = 0; i < workers.length; i++) {
				this.workers[i].start();
			}
		}
	}

	public void stop() {
		this.runing = false;
	}

	/**
	 * 
	 * @param list
	 * @return 返回是否被覆盖，true代表丢失数据,false代表没有丢失数据
	 */
	public boolean put(List<MetricData> list) {
		int i = 0;
		boolean result = false;
		while (i < workers.length) {
			int index = (int) (position.incrementAndGet() % workers.length);
			// 返回真，表示成功放入队列
			result = workers[index].safePut(list);

			if (result) {
				break;
			}
			i++;
		}

		if (!result) {
			return workers[new Random(workers.length).nextInt(workers.length)].forcePut(list);
		}
		return false;
	}

	class SendAgent extends Thread {

		private BlockingQueue<List<MetricData>> queue;

		public SendAgent(int bufSize) {
			this.queue = new LinkedBlockingQueue<List<MetricData>>(bufSize);			
		}

		/**
		 * 
		 * @param list
		 * @return 返回true代表数据丢失
		 */
		public boolean forcePut(List<MetricData> list) {
			// 先尝试放一次
			if (this.safePut(list)) {
				return false;
			} else {
				this.queue.remove();
				if (!this.queue.offer(list)) {
					log.info("force offer fail.");
				}
			}
			return true;
		}

		/**
		 * 
		 * @param list
		 * @return 返回true代表成功放入
		 */
		public boolean safePut(List<MetricData> list) {
			return this.queue.offer(list);
		}

		@Override
		public void run() {
			while (runing) {

				try {
					List<MetricData> list = queue.poll(100,
							TimeUnit.MILLISECONDS);

					if (list == null) {
						SleepUtils.sleep(50);
					} else {
						// 发送数据
						storage.store(list);
						
					}
				} catch (Throwable e) {
					log.error("sendagent store fail.", e);
					SleepUtils.sleep(50);
				}

			}
		}

	}
}
