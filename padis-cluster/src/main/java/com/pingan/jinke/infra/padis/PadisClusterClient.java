package com.pingan.jinke.infra.padis;

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
		return jedisCluster.get(makeKey(key));
	}

	@Override
	public String set(String key, String value) throws Exception {
		return jedisCluster.set(makeKey(key), value);
	}


	
}
