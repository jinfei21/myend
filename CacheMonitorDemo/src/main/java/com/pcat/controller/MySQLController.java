package com.pcat.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.cat.Cat;
import com.pcat.entity.User;
import com.pcat.mapper.UserMapper;
import com.pingan.jinke.infra.jedisx.JedisDirectClient;

@Controller
@RequestMapping("/mysql")
public class MySQLController {

	private static Logger log = LoggerFactory.getLogger(MySQLController.class);

	@Resource
	private UserMapper userMapper;


	@RequestMapping(value = "/query", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@RequestParam(value = "id", defaultValue = "") int id) {
		return userMapper.selectByPrimaryKey(id);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	@ResponseBody
	public int delUser(@RequestParam(value = "id", defaultValue = "") int id) {
		return userMapper.deleteByPrimaryKey(id);
	}

	@RequestMapping(value = "/metric", method = RequestMethod.GET)
	@ResponseBody
	public void metric(){
		Cat.logMetricForCount("redis.pool", 1);
	}
	
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	@ResponseBody
	public void error(){
		Cat.logError(new RuntimeException("test error"));
	}

}
