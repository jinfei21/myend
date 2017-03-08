package com.yjfei.padis.influxdb;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yjfei.padis.util.SleepUtils;

@RunWith(Parameterized.class)
public class DataCompleteTest {
	
	private InfluxDB influxdb;
	private ConsistencyLevel consistencyLevel;
	private String dbName;
	private String measurement;
	private int numPerMinutue;
	
	@Parameters
	public static Collection data(){
		return Arrays.asList(new Object[][]{
				{ "http://IP:8086",// addr
				  "root",// name
				  "root", // password
				  ConsistencyLevel.ONE, //consistent	
				  "jedisx",//dbName
				  "dataComplete",//measurement
				  100//numPerMinutue
				}
		});
				
	}
	
	public DataCompleteTest(String addr,String name,String password,ConsistencyLevel level, String dbName,String measurement, int numPerMinutue){
		 this.influxdb = InfluxDBFactory.connect(addr, name, password);
		 this.consistencyLevel = level;
		 this.dbName = dbName;
		 this.measurement = measurement;
		 this.numPerMinutue = numPerMinutue;
	}
	
	
	@Test
	public void test(){
		
		ConcurrentMap<String,Integer> groupMap = Maps.newConcurrentMap();
		List<String> groupList = Lists.newArrayList();
		groupList.add("G1");
		groupList.add("G2");
		groupList.add("G3");
		groupList.add("G4");
		groupList.add("G5");
	
		for(String key : groupList){
			groupMap.put(key, 0);
		}
		
		Random random = new Random();
		int length = groupList.size();
		String grpkey = null;
		
		influxdb.createDatabase(dbName);
		BatchPoints batchPoints = BatchPoints
								.database(dbName)
								.retentionPolicy("default")
								.consistency(consistencyLevel)
								.build();
		
		while(true){
			long deadLine = System.currentTimeMillis() + 1000 * 60;
			
			for (int i = 0; i < numPerMinutue; i++) {
				int index = random.nextInt(length);
				grpkey = groupList.get(index);
				groupMap.put(grpkey, groupMap.get(grpkey) + 1);
				batchPoints.point(Point.measurement(measurement)
								.time(System.currentTimeMillis(),TimeUnit.MILLISECONDS)
								.tag("name", grpkey + i)
								.addField("used", groupMap.get(grpkey)).build());
			}
			
			try{
				influxdb.write(batchPoints);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			while(System.currentTimeMillis() < deadLine){
				SleepUtils.sleep(100);
			}
		}
	}
	

}
