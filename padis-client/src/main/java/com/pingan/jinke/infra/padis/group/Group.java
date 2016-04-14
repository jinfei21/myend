package com.pingan.jinke.infra.padis.group;

import com.alibaba.fastjson.JSON;
import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Group {

	private int id;
	
	private Status status;
	
	private HostAndPort master;
		
	private HostAndPort slave;
	
	private long creatTime;
	
	private long modifyTime;

	
	public static void main(String args[]){
		
		Group g = new Group();
		g.setId(0);
		g.setMaster(new HostAndPort("1111", 1));
		g.setStatus(Status.ONLINE);
		
		String s = JSON.toJSONString(g);
		System.out.println(s);
		
		Group t = JSON.parseObject(s, Group.class);
		
		System.out.println(t);
	}
	
}
