package com.yjfei.padis.metric.storage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import com.yjfei.padis.metric.MetricData;

public class InfluxdbConvertUtil{
	
	
	private InfluxdbConvertUtil(){
		
	}
	
	public static BatchPoints toBatchPoints(String dbname,String retention,String consistent,List<MetricData> datas){
		BatchPoints batchPoints = BatchPoints
				.database(dbname)
				.retentionPolicy(retention)
				.consistency(ConsistencyLevel.valueOf(consistent))
				.build();
		
		for(MetricData data:datas){
			batchPoints.point(toPoint(data));
		}
		
		return batchPoints;
	}
	
	
	public static Point toPoint(MetricData data){
		
		return Point.measurement(data.getName())
				.tag(data.getTags())
				.fields(data.toField())
				.time(data.getTime(), TimeUnit.MILLISECONDS)
				.build();
	}
	
}
