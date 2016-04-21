package com.pingan.jinke.infra.padis.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pingan.jinke.infra.padis.common.Result;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.group.GroupService;
import com.pingan.jinke.infra.padis.node.Group;
import com.pingan.jinke.infra.padis.util.HostPortUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/group")
@Slf4j
public class GroupController {

	@Resource(name = "groupService")
	private GroupService groupService;

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
