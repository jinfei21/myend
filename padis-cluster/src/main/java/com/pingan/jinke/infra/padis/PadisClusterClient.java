package com.pingan.jinke.infra.padis;

import java.io.IOException;

import com.pingan.jinke.infra.padis.util.RegExceptionHandler;

public class PadisClusterClient extends AbstractClusterClient implements IPadis{
	
	public PadisClusterClient(PadisConfig config){
		super(config);
	}

	@Override
	public void setNameSpace(String nameSpace) {
		super.setNameSpace(nameSpace);
	}
	
	@Override
	public String get(String key) throws Exception {	
		check();
		return jedisCluster.get(makeKey(key));
	}

	@Override
	public String set(String key, String value) throws Exception {
		check();
		return jedisCluster.set(makeKey(key), value);
	}

	@Override
	public Long delete(String key) throws Exception {
		check();
		return jedisCluster.del(key);
	}

	@Override
	public void close() {
		try {
			clusterConfig.close();
			jedisCluster.close();			
		} catch (IOException e) {
			RegExceptionHandler.handleException(e);
		}
	}

}
