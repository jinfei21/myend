package com.yjfei.cache.padis.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjfei.cache.padis.common.Migrate;
import com.yjfei.cache.padis.common.Result;
import com.yjfei.cache.padis.common.TaskInfo;
import com.yjfei.cache.padis.group.GroupService;
import com.yjfei.cache.padis.migrate.MigrateTask;
import com.yjfei.cache.padis.migrate.MigrateTaskManager;
import com.yjfei.cache.padis.node.Group;
import com.yjfei.cache.padis.service.InstanceService;
import com.yjfei.cache.padis.service.MigrateService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/migrate")
@Slf4j
public class MigrateController {
	
	@Resource(name = "instanceService")
	private InstanceService instanceService;
	
	@Resource(name = "migrateService")
	private MigrateService migrateService;
	
	@Resource(name = "migrateManager")
	private MigrateTaskManager migrateManager;
	
	@Resource(name = "groupService")
	private GroupService groupService;
	
	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor executor;

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
			
			if(d != null){
				delay = Integer.parseInt(d);
			}
			
			MigrateTask task = migrateManager.getTask(instance);
			
			if(task != null){
				result.setMessages("正在迁移 ,一次只能迁移一个！");
				result.setSuccess(false);
				return result;
			}
			
			addTask(instance, from, to, to_gid, delay);
			
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("add migrate task fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
	public void addTask(final String instance, final int from, final int to, final int gid, final int delay) {

		executor.execute(new Runnable() {
			
			@Override
			public void run() {
//				for (int i = from; i <= to; i++) {
//					migrateService.persistMigrate(instance, i, gid, delay);
//				}
				
				migrateService.persistMigrate(instance, from, to, gid, delay);
				if (!migrateManager.postTask(new TaskInfo(instance, from, to))) {
					for (int i = from; i <= to; i++) {
						migrateService.delSlotMigrate(instance, i);
					}
					log.warn(instance +"is migrating.please wait!");
				}
			}
		});


	}
	
	@RequestMapping(value = "/delTask", method = RequestMethod.POST)
	@ResponseBody
	public Result delTask(@RequestParam(value = "data", defaultValue = "") String data) {
		Result<String> result = new Result<String>();

		try {
			
			JSONObject jsonObj = JSONObject.parseObject(data);
			
			String instance = jsonObj.getString("instance");
			
			if(!instanceService.isExisted(instance)){
				result.setMessages("instance 不存在。");
				result.setSuccess(false);
				return result;
			}
			int slotid = Integer.parseInt(jsonObj.getString("slot_id"));
			migrateService.delSlotMigrate(instance, slotid);
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("delete migrate task fail!", t);
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
			Collections.sort(list, new Comparator<Migrate>(){

				@Override
				public int compare(Migrate arg0, Migrate arg1) {
					if(arg0 != null && arg1!=null){
						return arg0.getSlot_id() - arg1.getSlot_id() ;
					}else{
						return 0;
					}
				}
				
			});
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
