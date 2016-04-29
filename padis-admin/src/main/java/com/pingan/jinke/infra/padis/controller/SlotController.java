package com.pingan.jinke.infra.padis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.Result;
import com.pingan.jinke.infra.padis.common.Status;
import com.pingan.jinke.infra.padis.group.GroupService;
import com.pingan.jinke.infra.padis.node.Group;
import com.pingan.jinke.infra.padis.node.Slot;
import com.pingan.jinke.infra.padis.service.InstanceService;
import com.pingan.jinke.infra.padis.slot.SlotService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/slot")
@Slf4j
public class SlotController {

	@Resource(name = "zkRegCenter")
	private CoordinatorRegistryCenter coordinatorRegistryCenter;
	
	@Resource(name = "instanceService")
	private InstanceService instanceService;
	
	@Resource(name = "groupService")
	private GroupService groupService;
	
	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor executor;
	
	@RequestMapping(value = "/distSlots",  method = RequestMethod.POST)
	@ResponseBody
	public Result<String> distSlots(@RequestParam(value = "slotsInfo", defaultValue = "") String slotsInfo){
		Result<String> result = new Result<String>();
		
		try{
			JSONObject jsonObj = JSONObject.parseObject(slotsInfo);
//			log.info("SlotController distSlots slotsInfo: "+JSON.toJSONString(slotsInfo));
			String instance = jsonObj.getString("instance");
			
			if(instanceService.isExisted(instance)){
				result.setMessages("instance已经存在。");
				result.setSuccess(false);
				return result;
			}
			
			JSONArray slots = jsonObj.getJSONArray("slots");
			SlotService slotService = new SlotService(instance,coordinatorRegistryCenter);
			
			List<Slot> slotList = Lists.newArrayList();
			int count = 200;
			for(Object obj : slots){
				JSONObject jsonData = JSON.parseObject(obj.toString());
				int fromId = jsonData.getIntValue("fromId");
				int toId = jsonData.getIntValue("toId");
				int groupId = jsonData.getIntValue("groupId");
				
				if(fromId > toId || toId > 1023 || fromId < 0){
					result.setMessages("slot from 或slot to取值范围不正确.");
					result.setSuccess(false);
					return result;
				}
				
				for(int id = fromId; id <= toId; id++){
					long currentTime = System.currentTimeMillis();
					Slot slot = new Slot(id,Status.ONLINE,currentTime,groupId,0,currentTime);
					slotList.add(slot);
					if(slotList.size() == count){
						executor.execute(new DistSlotThread(slotList, slotService));
						slotList = Lists.newArrayList();
					}
				}
			}
			if(!slotList.isEmpty()){
				executor.execute(new DistSlotThread(slotList, slotService));
			}
			result.setSuccess(true);
		}catch (Throwable t) {
			log.error("add slot fail.", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/getSlotsInfo", method = RequestMethod.POST)
	@ResponseBody
	public Result<Object> getSlotsInfo(@RequestParam(value = "instance") String instance) {
		Result<Object> result = new Result<Object>();
		
		try {
			//Slots
			SlotService slotService = new SlotService(instance,coordinatorRegistryCenter);
			List<Slot> list = slotService.getAllSlots();
			String jsonStr = JSON.toJSONString(list,true);
			JSONArray slotJsonArray = JSONArray.parseArray(jsonStr);
			//Groups
			Set<Integer> set = slotService.getSlotGroups(list);
			List<Group> grpList = new ArrayList<Group>();
			for(int gid : set){
				Group grp = groupService.getGroup(gid);
				if(grp != null){
					grpList.add(grp);
				}
			}
			String grpJson = JSON.toJSONString(grpList,true);
			JSONArray grpJsonArray = JSONArray.parseArray(grpJson);
			
			JSONObject jsonData = new JSONObject();
			jsonData.put("instance", instance);
			jsonData.put("slots", slotJsonArray);
			jsonData.put("groups", grpJsonArray);
			result.setResult(jsonData);
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("get slotsInfo fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}
		
		return result;
	}
	
	
	class DistSlotThread implements Runnable{
		private List<Slot> slotList;
		private SlotService slotService;
		
		public DistSlotThread(List<Slot> slotList, SlotService slotService){
			this.slotList = slotList;
			this.slotService = slotService;
		}
		
		@Override
		public void run() {
			for(Slot slot : slotList){
				slotService.setSlot(slot);
			}
			slotList.clear();
		}
		
	}
	
}
