package com.yjfei.padis.metric;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Maps;

@Setter
@Getter
public class MetricData {
	private String name;
	
	private String metric;
	
	private Map<String,String> tags;
	
	private long time;
	
	private Object value;
	
	public MetricData(){
		this.tags = Maps.newHashMap();
		this.time = System.currentTimeMillis();
		this.value = 0;
	}
	
	public MetricData(String name,String metric,Map<String,String> tags,long time,Object value){
		this.name = name;
		this.metric = metric;
		if(tags == null){
			this.tags = Maps.newHashMap();
		}else{
			this.tags = tags;
		}
		this.time = time;
		this.value = value;
	}
	
	public Map<String,Object> toField(){
		Map<String,Object> map = Maps.newHashMap();
		map.put(metric, value);
		return map;
	}
	@Override
	public String toString() {
		return "MetricData [name=" + name+",metric="+ metric + ", tags=" + tags + ", time=" + time
				+ ", value=" + value + "]";
	}
		
}
