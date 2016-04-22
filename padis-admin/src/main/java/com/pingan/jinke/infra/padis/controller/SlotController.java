package com.pingan.jinke.infra.padis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
	
	@RequestMapping(value = "/distSlots",  method = RequestMethod.POST)
	@ResponseBody
	public Result<String> distSlots(@RequestParam(value = "slotsInfo", defaultValue = "") String slotsInfo){
		Result<String> result = new Result<String>();
		
		try{
			JSONObject jsonObj = JSONObject.parseObject(slotsInfo);
			log.debug("slotsInfo: "+JSON.toJSONString(slotsInfo));
			String instance = jsonObj.getString("instance");
			JSONArray slots = jsonObj.getJSONArray("slots");
			log.debug("slots: "+JSON.toJSONString(slots));
			for(Object obj : slots){
				JSONObject jsonData = JSON.parseObject(obj.toString());
				int fromId = jsonData.getIntValue("fromId");
				int toId = jsonData.getIntValue("toId");
				int groupId = jsonData.getIntValue("groupId");
				for(int id = fromId; id <= toId; id++){
					Slot slot = new Slot(id,Status.ONLINE,0,groupId,0,0);
					SlotService slotService = new SlotService(instance,coordinatorRegistryCenter);
					slotService.setSlot(slot);
				}
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
			Set<Integer> set = slotService.getAllGroups();
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
	
}
