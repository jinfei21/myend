package com.pingan.jinke.infra.padis.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.pingan.jinke.infra.padis.common.Migrate;
import com.pingan.jinke.infra.padis.common.Result;
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
	
	@RequestMapping(value = "/addTask", method = RequestMethod.POST)
	@ResponseBody
	public Result addTask(@RequestParam(value = "data", defaultValue = "") String data) {
		Result<String> result = new Result<String>();

		try {

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
	public Result getTask(@RequestParam(value = "data", defaultValue = "") String data) {
		Result<List<Migrate>> result = new Result<List<Migrate>>();

		try {

			List<Migrate> list = Lists.newArrayList();
			
			list.add(new Migrate());
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
