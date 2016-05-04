package com.yjfei.cache.padis.util;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

public class IPUtils {

	
	public static final String getIP(){
		String ip=null;
		
		try{
			Enumeration<NetworkInterface> er = NetworkInterface.getNetworkInterfaces();
			while(er.hasMoreElements()){
				NetworkInterface ni = er.nextElement();
				if(ni.getName().startsWith("eth")||ni.getName().startsWith("bond")){
					List<InterfaceAddress> list = ni.getInterfaceAddresses();
					for(InterfaceAddress interfaceAddress:list){
						InetAddress address = interfaceAddress.getAddress();
						if(address instanceof Inet4Address){
							ip = address.getHostAddress();
							break;
						}
					}
				}
			}
		}catch(SocketException e){
			e.printStackTrace();
		}
		
		if(ip==null){
			try{
				ip = InetAddress.getLocalHost().getHostAddress();
			}catch(UnknownHostException e){
				e.printStackTrace();
			}
		}
		return ip;
	}
}