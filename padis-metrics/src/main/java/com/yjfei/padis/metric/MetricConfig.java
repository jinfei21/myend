package com.yjfei.padis.metric;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class MetricConfig {

	private String serverAddr;
	
	private String username;
	
	private String password;
	
	private String dbName;
	
	private int bufferSize;
	
	private int workerSize;
	
	private int workerBufSize;
	
	private SendPolicy sendPolicy;
	
	private String retention;
	
	private String consistent;
	
	public MetricConfig(String serverAddr, String username, String password) {
		this.serverAddr = serverAddr;
		this.username = username;
		this.password = password;
	}

	public MetricConfig(String serverAddr, String username, String password,
			String dbname) {
		this.serverAddr = serverAddr;
		this.username = username;
		this.password = password;
		this.dbName = dbname;
	}

	public MetricConfig(String serverAddr, String username, String password,
			String dbName, int bufferSize, int workerSize, int workerBufSize,
			SendPolicy sendPolicy, String retention, String consistent) {
		this.serverAddr = serverAddr;
		this.username = username;
		this.password = password;
		this.dbName = dbName;
		this.bufferSize = bufferSize;
		this.workerSize = workerSize;
		this.workerBufSize = workerBufSize;
		this.sendPolicy = sendPolicy;
		this.retention = retention;
		this.consistent = consistent.toUpperCase();
	}
	
	
	
}
