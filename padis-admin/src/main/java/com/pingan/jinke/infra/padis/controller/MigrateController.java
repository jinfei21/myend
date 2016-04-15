package com.pingan.jinke.infra.padis.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pingan.jinke.infra.padis.common.Migrate;
import com.pingan.jinke.infra.padis.common.Result;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/migrate")
@Slf4j
public class MigrateController {

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
	
	@RequestMapping(value = "/addTask", method = RequestMethod.POST)
	@ResponseBody
	public Result getTask(@RequestParam(value = "data", defaultValue = "") String data) {
		Result<List<Migrate>> result = new Result<List<Migrate>>();

		try {

			
			result.setSuccess(true);
		} catch (Throwable t) {
			log.error("add migrate task fail!", t);
			result.setSuccess(false);
			result.setMessages(t.getMessage());
		}

		return result;
	}
	
}
