package com.yjfei.padis.util;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

@Slf4j
public class ConfigUtil {
	
	private static Map<String,Properties> props = null;
	
	
	private static class Config{
		private static Map<String,Properties> configMap = Maps.newHashMap();
		
		public static synchronized void loadConfig(String path){
			
			try{
				InputStream in = IOStream.getResourceAsStream(path);
				Properties prop = new Properties();
				prop.load(in);
				configMap.put(path, prop);
				if(in != null){
					in.close();
				}
			}catch(Exception e){
				log.warn("load config fail:"+path, e);
			}
		}
	}
	
	public static void addResource(String path){
		Config.loadConfig(path);
	}
	
	public static void mergeConfig(String defPath,String realPath){
		Properties prop1 = getProperties(defPath);
		Properties prop2 = getProperties(realPath);
		if(prop1 != null && prop2 != null){
			prop1.putAll(prop2);
		}
		
	}
	
	public static Properties getProperties(String path){
		
		if(props ==null){
			synchronized (ConfigUtil.class) {
				if(props == null){
					props = Config.configMap;
				}
			}
		}
		
		if(props.get(path) == null){
			Config.loadConfig(path);
		}
		
		return props.get(path);
	}

	public static String getString(String path, String key) {
		Properties prop = getProperties(path);
		
		if(prop != null){
			return prop.getProperty(key);
		}else{
			return null;
		}
		
	}
	
	public static String getString(String path, String key,String defValue) {
		
		String value = getString(path,key);
		if(Strings.isNullOrEmpty(value)){
			return defValue;
		}
		return value;
	}
	
	public static int getInt(String path ,String key,int defValue){
		String value = getString(path,key);
		if(value == null){
			return defValue;
		}
		
		return Integer.parseInt(value);
	}
}
