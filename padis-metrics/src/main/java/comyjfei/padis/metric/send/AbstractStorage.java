package comyjfei.padis.metric.send;

import java.util.List;
import java.util.Map;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.yjfei.padis.metric.IStorage;
import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.MetricData;
import com.yjfei.padis.metric.storage.InfluxdbPool;

public abstract class AbstractStorage implements IStorage{

	protected List<String> addrs;
	
	protected String username;
	
	protected String password;	
	
	protected String dbname;
	
	protected String retention;
	
	protected String consistent;
	
	protected Map<String,InfluxdbPool> influxdbCache;
	
	public AbstractStorage(MetricConfig config){
		this.addrs = Splitter.on(",")
				   .trimResults()
				   .omitEmptyStrings()
				   .splitToList(config.getServerAddr());
		this.username = config.getUsername();
		this.password = config.getPassword();
		this.dbname = config.getDbName();
		this.retention = config.getRetention();
		this.consistent = config.getConsistent();
		this.influxdbCache = Maps.newConcurrentMap();
		
		for(String addr:addrs){
			this.influxdbCache.put(addr, new InfluxdbPool(addr,username,password));
		}
	}
	
	protected void sendData(String server,BatchPoints batchPoints) throws Exception{
		InfluxdbPool pool = this.influxdbCache.get(server);
		
		InfluxDB influxdb = null;
		try {
			influxdb = pool.lease(2000);
			influxdb.createDatabase(batchPoints.getDatabase());
			influxdb.write(batchPoints);
		} catch (Exception e) {

			throw e;
		}finally{
			if(influxdb != null){
				pool.release(influxdb);
			}
		}
	}
	
	public abstract void store(List<MetricData> list);

}
