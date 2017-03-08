package com.yjfei.padis.influxdb;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConsistentPolicyTest {
	
	private InfluxDB influxDB;
	
	private ConsistencyLevel consistencyLevel;
	
	@Parameters
	public static Collection data() {
		return Arrays.asList(new Object[][] { 
			  { "http://IP:8086",// addr
				"root",// name
				"root", // password
				ConsistencyLevel.ONE //consistent				
			  } ,
		
			  { "http://IP:8086",// addr
				"root",// name
				"root", // password
				ConsistencyLevel.ANY //consistent				
			  }  ,
		
			  { "http://IP:8086",// addr
				"root",// name
				"root", // password
				ConsistencyLevel.ALL //consistent				
			  }  ,
		
			  { "http://IP:8086",// addr
				"root",// name
				"root", // password
				ConsistencyLevel.QUORUM //consistent				
			  } 		
		});
	}
	
	public ConsistentPolicyTest(String addr,String name,String password,ConsistencyLevel level){
		 influxDB = InfluxDBFactory.connect(addr, name, password);
		 consistencyLevel = level;
	}
	

	@Test
	public void testCase(){
		String dbName = "yjfei";
		influxDB.createDatabase(dbName);
		
		BatchPoints batchPoints = BatchPoints
						.database(dbName)
						//.tag("async", "true")
						.retentionPolicy("default")
						.consistency(consistencyLevel)
						.build();
		Point point1 = Point.measurement("cpu")
							.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
							.addField("yjfei", 1L)
							.build();
		Point point2 = Point.measurement("cpu")
							.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
							.addField("yjfei1", 1L)
							.build();
		batchPoints.point(point1);
		batchPoints.point(point2);
		long start = System.currentTimeMillis();
		
		System.out.println(consistencyLevel.value() + " cost:"+(System.currentTimeMillis() - start));
	}
	
}
