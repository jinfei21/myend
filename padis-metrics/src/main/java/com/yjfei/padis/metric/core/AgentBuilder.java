package com.yjfei.padis.metric.core;

import com.yjfei.padis.metric.IMetricAgent;
import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.MetricData;

public class AgentBuilder {
	
	private MetricConfig config;
	
	public AgentBuilder(MetricConfig config){
		this.config = config;
	}
	
	public AgentBuilder(){
		
	}
	
	public AgentBuilder config(MetricConfig config){
		this.config = config;
		return this;
	}
	
	public IMetricAgent build(){
		RingBuffer<MetricData> ringBuffer = new RingBuffer<MetricData>(this.config.getBufferSize());
		ConsumerAgent consumerAgent = new ConsumerAgent(ringBuffer,this.config);
		consumerAgent.start();
		return new ProducerAgent(ringBuffer);
	}

}
