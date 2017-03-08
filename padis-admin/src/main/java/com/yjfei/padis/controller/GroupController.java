package com.yjfei.padis.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yjfei.padis.common.HostAndPort;
import com.yjfei.padis.common.Result;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.group.GroupService;
import com.yjfei.padis.node.Group;
import com.yjfei.padis.storage.ZookeeperRegistryCenter;
import com.yjfei.padis.util.HostPortUtils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Controller
@RequestMapping(value = "/group")
@Slf4j
public class GroupController {

	@Resource(name = "groupService")
	private GroupService groupService;
	
	@Resource(name = "zkRegCenter")
	private ZookeeperRegistryCenter zkRegCenter;
	
	@Value("${passwd}")  
	private String passwd;

	@RequestMapping(value = "/groupList", method = RequestMethod.GET)
	@ResponseBody
	public Result groupList() {

		Result<List<Group>> result = new Result<List<Group>>();

		try {

			List<Group> list = groupService.getAllGroups();
			
			result.setSuccess(true);
			result.setResult(list);
		} catch (Throwable t) {
			log.error("get all group fail.", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
	@RequestMapping(value = "/addGroup",  method = RequestMethod.POST)
	@ResponseBody
	public Result addGroup(@RequestParam(value = "group", defaultValue = "") String groupStr){
		Result<String> result = new Result<String>();
		
		try{
			JSONObject jsonObj = JSONObject.parseObject(groupStr);
			
			String id = jsonObj.getString("id");
			String master = jsonObj.getString("master");
			String slave = jsonObj.getString("slave");

			Group group = new Group();
			group.setId(Integer.valueOf(id));
			group.setMaster(HostPortUtils.mapHostAndPort(master));
			group.setSlave(HostPortUtils.mapHostAndPort(slave));
			group.setStatus(Status.ONLINE);
			groupService.addGroup(group);
			result.setSuccess(true);
		}catch (Throwable t) {
			log.error("add group fail.", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/updateGroup",  method = RequestMethod.POST)
	@ResponseBody
	public Result updateGroup(@RequestParam(value = "group", defaultValue = "") String groupStr){
		Result<String> result = new Result<String>();
		
		try{
			JSONObject jsonObj = JSONObject.parseObject(groupStr);
			
			String id = jsonObj.getString("id");
			String master = jsonObj.getString("master");
			String slave = jsonObj.getString("slave");

			Group group = new Group();
			group.setId(Integer.valueOf(id));
			group.setMaster(HostPortUtils.mapHostAndPort(master));
			group.setSlave(HostPortUtils.mapHostAndPort(slave));
			group.setStatus(Status.ONLINE);
			groupService.replaceGroup(group);
			result.setSuccess(true);
		}catch (Throwable t) {
			log.error("update group fail.", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/getGroup", method = RequestMethod.GET)
	@ResponseBody
	public Result getGroupByID(@RequestParam(value = "id", defaultValue = "") int id){
		Result<Group> result = new Result<Group>();
		
		try{
			Group group = groupService.getGroup(id);
			if(group != null){
				result.setSuccess(true);
				result.setResult(group);
			}else{
				result.setSuccess(false);
				result.setMessages("group is not existed.");
			}
					
		}catch(Throwable t){
			log.error("get group fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/pingGroup", method = RequestMethod.GET)
	@ResponseBody
	public Result pingGroupByID(@RequestParam(value = "id", defaultValue = "") int id){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		
		try{
			Group group = groupService.getGroup(id);
			if(group != null){
				result.setSuccess(true);
				//
				Map<String,String> map = Maps.newHashMap();

				
				map.putAll(checkHost("master",group.getMaster()));
				map.putAll(checkHost("slave",group.getSlave()));
				
				result.setResult(map);
			}else{
				result.setSuccess(false);
				result.setMessages("group is not existed.");
			}
					
		}catch(Throwable t){
			log.error("get group fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		return result;
	}	
	
	private Map<String,String> checkHost(String type,HostAndPort host){
		Map<String,String> map = Maps.newHashMap();
		try{
			if(!pingHost(host)){
				map.put(host.toString(), type + " is not valid");				
			}else{
				map.put(host.toString(), type + " is ok");
			}
		}catch(Throwable t){
			map.put(host.toString(), type + " is not valid,"+t.getMessage());
		}
		return map;
	}
	
	private boolean pingHost(HostAndPort host) throws Throwable{
		if(host != null){
			try{
				Jedis jedis = new Jedis(host.getHost(), host.getPort());
				jedis.auth(passwd);
		          if (!jedis.ping().equals("PONG")) {
		              return false;
		          }
		          jedis.close();
		          return true;
			}catch(Throwable t){
				log.error("ping host fail!", t);
				throw t;
			}
		}
		return false;
	}
	
	@RequestMapping(value = "/pingZK", method = RequestMethod.GET)
	@ResponseBody
	public Result pingZK(){
		Result<String> result = new Result<String>();
		
		try{
			List<String> list = zkRegCenter.getChildrenKeys("/");
			
			if(list.size()!=0){
				result.setSuccess(true);
				result.setResult("OK");
			}else{
				result.setSuccess(false);
				result.setMessages("no data");
			}
			
		}catch(Throwable t){
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/delGroup", method = RequestMethod.GET)
	@ResponseBody
	public Result delGroupByID(@RequestParam(value = "id", defaultValue = "") int id){
		Result<String> result = new Result<String>();
		try{
			groupService.delGroup(id);
			result.setSuccess(true);
		}catch(Throwable t){
			log.error("delete group fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		return result;
	}
}
