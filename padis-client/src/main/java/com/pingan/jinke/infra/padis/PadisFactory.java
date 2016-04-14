package com.pingan.jinke.infra.padis;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class PadisFactory {

	private static ConcurrentMap<String, IPadis> cache = Maps.newConcurrentMap();
	
	
	public PadisFactory(String zkAddr,String instance,String nameSpace){
		
	}
	
}