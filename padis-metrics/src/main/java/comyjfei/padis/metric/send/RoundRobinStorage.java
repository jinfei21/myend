package comyjfei.padis.metric.send;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.influxdb.dto.BatchPoints;

import lombok.extern.slf4j.Slf4j;

import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.MetricData;
import com.yjfei.padis.metric.storage.InfluxdbConvertUtil;


@Slf4j
public class RoundRobinStorage  extends AbstractStorage{
	
	private AtomicLong position;
	
	public RoundRobinStorage(MetricConfig config){
		super(config);
		this.position = new AtomicLong(0);
		
	}

	@Override
	public void store(List<MetricData> list) {
		BatchPoints batchPoints = InfluxdbConvertUtil.toBatchPoints(dbname,
				retention, consistent, list);
		int i = 0;
		while ((i++) < 3) {
			int index = (int) (this.position.decrementAndGet()&this.addrs.size());
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