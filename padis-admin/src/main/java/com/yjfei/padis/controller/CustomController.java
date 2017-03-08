package com.yjfei.padis.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yjfei.padis.common.Result;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.custom.CustomService;
import com.yjfei.padis.node.Custom;
import com.yjfei.padis.storage.ZookeeperRegistryCenter;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/custom")
@Slf4j
public class CustomController {

	@Resource(name = "zkRegCenter")
	private ZookeeperRegistryCenter zkRegCenter;
	
	@RequestMapping(value = "/updateCustom", method = RequestMethod.POST)
	@ResponseBody
	public Result updateCustom(@RequestParam(value = "data", defaultValue = "") String data) {
		Result<String> result = new Result<String>();

		try {
			
			JSONObject jsonObj = JSONObject.parseObject(data);
			
			String instance = jsonObj.getString("instance");
			String node = jsonObj.getString("node");
			String status = jsonObj.getString("status");
			String limit = jsonObj.getString("limit");
			long sleep = jsonObj.getLong("sleep");
			CustomService customService = new CustomService(instance,zkRegCenter);
			Custom custom = customService.getLocalCustom(node);
			if(limit != null){
				custom.setLimit(Integer.parseInt(limit));
			}else{
				custom.setLimit(-1);
			}
			custom.setSleep(sleep);
			custom.setStatus(Status.getStatus(status));
			customService.updateCustom(custom,node);
			
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("update custom fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
	
	@RequestMapping(value = "/getCustom", method = RequestMethod.GET)
	@ResponseBody
	public Result getCustom(@RequestParam(value = "data", defaultValue = "") String instance) {
		Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();

		try {
			if(StringUtils.isEmpty(instance)){
				result.setSuccess(true);
				return result;
			}
			CustomService customService = new CustomService(instance,zkRegCenter);
			List<Map<String,String>> list = Lists.newArrayList();
			for(String node:customService.getAllCustomNode()){
				Custom custom = customService.getLocalCustom(node);
				Map map = custom.toMap();
				map.put("node", node);
				map.put("instance", instance);
				list.add(map);				
			}
	
			result.setResult(list);
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("get custom fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
}
