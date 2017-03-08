package com.yjfei.padis.metric.core;

import static com.yjfei.padis.metric.ConfigConstant.BUFFER_SIZE;

import java.util.Map;

import com.yjfei.padis.metric.IMetricAgent;
import com.yjfei.padis.metric.MetricData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ProducerAgent implements IMetricAgent {
	private static RingBuffer<MetricData> ringBuffer;

	public ProducerAgent(RingBuffer<MetricData> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public ProducerAgent() {
		this.ringBuffer = new RingBuffer<MetricData>(BUFFER_SIZE);
	}

	private void put(final MetricData data) {
		if(this.ringBuffer.put(data)){
			log.info("miss data!");
		}
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, long value) {
		log(name,metric, tags, System.currentTimeMillis(), value);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, long time, long value) {
		MetricData metricData = new MetricData(name,metric, tags, time, value);
		put(metricData);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, int value) {
		log(name, metric,tags, System.currentTimeMillis(), value);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, long time, int value) {
		MetricData metricData = new MetricData(name,metric, tags, time, value);
		put(metricData);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, double value) {
		log(name, metric,tags, System.currentTimeMillis(), value);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, long time,
			double value) {
		MetricData metricData = new MetricData(name,metric, tags, time, value);
		put(metricData);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, float value) {
		log(name, metric,tags, System.currentTimeMillis(), value);
	}

	@Override
	public void log(String name, String metric,Map<String, String> tags, long time,
			float value) {
		MetricData metricData = new MetricData(name,metric, tags, time, value);
		put(metricData);
	}

}
