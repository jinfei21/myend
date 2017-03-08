package comyjfei.padis.metric.send;

import java.util.List;
import java.util.Random;

import org.influxdb.dto.BatchPoints;
import lombok.extern.slf4j.Slf4j;

import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.MetricData;
import com.yjfei.padis.metric.storage.InfluxdbConvertUtil;

@Slf4j
public class RandomStorage extends AbstractStorage {

	private Random rand;

	public RandomStorage(MetricConfig config) {
		super(config);
		this.rand = new Random(this.addrs.size());

	}

	@Override
	public void store(List<MetricData> list) {
		BatchPoints batchPoints = InfluxdbConvertUtil.toBatchPoints(dbname,
				retention, consistent, list);
		int i = 0;
		while ((i++) < 3) {
			int index = this.rand.nextInt(this.addrs.size());
			String server = this.addrs.get(index);
			try {
				sendData(server, batchPoints);
				break;
			} catch (Exception e) {
				log.error("send data fail,server="+server, e);
			}
		}

	}

}
