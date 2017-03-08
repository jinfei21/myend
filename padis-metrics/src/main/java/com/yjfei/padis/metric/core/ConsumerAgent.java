package com.yjfei.padis.metric.core;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.yjfei.padis.metric.MetricConfig;
import com.yjfei.padis.metric.MetricData;
import com.yjfei.padis.util.SleepUtils;

import comyjfei.padis.metric.send.SendAgentGroup;


@Slf4j
class ConsumerAgent implements Runnable{


	private RingBuffer<MetricData> ringBuffer;
	private volatile boolean runing;
	
	private SendAgentGroup sendGroup;
	
	public ConsumerAgent(RingBuffer<MetricData> ringBuffer, MetricConfig config) {
		this.ringBuffer = ringBuffer;
		this.sendGroup = new SendAgentGroup(config);
		this.runing = false;
	}
	
	public void start(){
		if(!this.runing){
			this.runing = true;
			new Thread(this).start();
			sendGroup.start();
		}
	}
	
	public void stop(){
		this.runing = false;
		this.sendGroup.stop();
	}

	@Override
	public void run() {
		while(runing){
		
			List<MetricData> list = this.ringBuffer.takeForTimeout(500, 5000);
			
			if(list.size() != 0){
				if(sendGroup.put(list)){
					log.info("miss data size:{}",list.size());
				}
			}else{
				SleepUtils.sleep(50);
			}
			
		}
		
	}
	
}
