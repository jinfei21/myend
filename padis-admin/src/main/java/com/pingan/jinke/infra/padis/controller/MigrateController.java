package com.pingan.jinke.infra.padis.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pingan.jinke.infra.padis.common.Migrate;
import com.pingan.jinke.infra.padis.common.Result;
import com.pingan.jinke.infra.padis.group.GroupService;
import com.pingan.jinke.infra.padis.node.Group;
import com.pingan.jinke.infra.padis.service.InstanceService;
import com.pingan.jinke.infra.padis.service.MigrateService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/migrate")
@Slf4j
public class MigrateController {
	
	@Resource(name = "instanceService")
	private InstanceService instanceService;
	
	@Resource(name = "migrateService")
	private MigrateService migrateService;
	
	@Resource(name = "groupService")
	private GroupService groupService;
	
	@RequestMapping(value = "/addTask", method = RequestMethod.POST)
	@ResponseBody
	public Result addTask(@RequestParam(value = "data", defaultValue = "") String data) {
		Result<String> result = new Result<String>();

		try {
			
			JSONObject jsonObj = JSONObject.parseObject(data);
						
			int from = Integer.parseInt(jsonObj.getString("from"));
			int to = Integer.parseInt(jsonObj.getString("to"));
			
			if(from > to||to > 1023 || from < 0){
				result.setMessages("slot范围不正确,from 大于 to.");
				result.setSuccess(false);
				return result;
			}
			
			if((to - from) > 350){
				result.setMessages("slot一次只能迁移最大350个");
				result.setSuccess(false);
				return result;
			}
			
			String instance = jsonObj.getString("instance");
			
			if(!instanceService.isExisted(instance)){
				result.setMessages("instance 不存在。");
				result.setSuccess(false);
				return result;
			}
			
			int to_gid = Integer.parseInt(jsonObj.getString("new_group"));

			Group group = groupService.getGroup(to_gid);
			
			if(group == null){
				result.setMessages("group 不存在！");
				result.setSuccess(false);
				return result;
			}
			int delay = 500;
			String d = jsonObj.getString("delay");
			
			if(d == null){
				delay = Integer.parseInt(d);
			}
			
			migrateService.addTask(instance, from, to, to_gid, delay);
			
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("add migrate task fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
	@RequestMapping(value = "/getTask", method = RequestMethod.GET)
	@ResponseBody
	public Result getTask(@RequestParam(value = "data", defaultValue = "") String instance) {
		Result<List<Migrate>> result = new Result<List<Migrate>>();

		try {

			List<Migrate> list = migrateService.getTask(instance);
			result.setResult(list);
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("get migrate task fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
	
	@RequestMapping(value = "/getInstances", method = RequestMethod.GET)
	@ResponseBody
	public Result getInstances() {
		Result<List<String>> result = new Result<List<String>>();

		try {
			List<String> list = instanceService.getAllInstances();
			result.setResult(list);
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("get instances fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
}
