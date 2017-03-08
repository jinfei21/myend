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
public class InfluxdbTest2 {
	
	private InfluxDB influxdb;
	private ConsistencyLevel consistencyLevel;
	private long period;
	private String dbName;
	private String measurement;
	
	@Parameters
	public static Collection data(){
		return Arrays.asList(new Object[][]{
				{ "http://IP:8086",// addr
				  "root",// name
				  "root", // password
				  ConsistencyLevel.ONE, //consistent	
				  1 * 1000 * 30,//time
				  "jedisx",//dbName
				  "ggg"//measurement
				}
		});
				
	}
	
	public InfluxdbTest2(String addr,String name,String password,ConsistencyLevel level, long period, String dbName,String measurement){
		 this.influxdb = InfluxDBFactory.connect(addr, name, password);
		 this.consistencyLevel = level;
		 this.period = period;
		 this.dbName = dbName;
		 this.measurement = measurement;
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
		int totalNum = 0;
		String grpkey = null;
		
		influxdb.createDatabase(dbName);
		BatchPoints batchPoints = BatchPoints
								.database(dbName)
								.retentionPolicy("jedisx_6h")
								.consistency(consistencyLevel)
								.build();
		doQuery("before");
		
		long start = System.currentTimeMillis();
		long deadLine = start + period;
		
		while(System.currentTimeMillis() < deadLine){
				
			int index = random.nextInt(length);
			grpkey = groupList.get(index);
			groupMap.put(grpkey, groupMap.get(grpkey)+1);
	
			batchPoints.point(Point.measurement(measurement)
								.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.tag("name", grpkey)
								.addField("used", groupMap.get(grpkey))
								.build());
			totalNum++;
			try{
				influxdb.write(batchPoints);
				SleepUtils.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		System.out.println("===========add===========");
		System.out.println("total put metrics: " + totalNum);
		doQuery("after");
	}
	
	private void doQuery(String period){
		System.out.println("===========" + period + "===========");
		Query query = new Query("SELECT COUNT(used) FROM " + measurement, dbName);
		QueryResult result = influxdb.query(query);
		parseQueryResult(result);
	}
	
	private void parseQueryResult(QueryResult queryResult) {
		List<Result> results = queryResult.getResults(); 
		System.out.println("total query metrics: ");
		for(Result result : results){
			List<Series> series = result.getSeries();
			if(series != null){
				for(Series se : series){
					List<List<Object>> values = se.getValues();
					System.out.println(values.toString());
				}
			}else{
				System.out.println(0);
			}
		}
	}

}
