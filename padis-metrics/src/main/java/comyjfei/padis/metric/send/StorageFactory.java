package comyjfei.padis.metric.send;

import com.yjfei.padis.metric.IStorage;
import com.yjfei.padis.metric.MetricConfig;

public class StorageFactory {

	private StorageFactory() {

	}

	public static IStorage getStorage(MetricConfig config) {

		switch (config.getSendPolicy()) {
			case RoundRobin:
				return new RoundRobinStorage(config);
			case Random:
			default:
				break;
		}
		return new RandomStorage(config);
	}
}
