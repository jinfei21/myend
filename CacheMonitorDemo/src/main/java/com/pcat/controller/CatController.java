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
import com.dianping.cat.message.Transaction;
import com.pcat.service.CatService;
import com.pingan.jinke.infra.jedisx.JedisDirectClient;

@Controller
@RequestMapping("/cat")
public class CatController {

	private static Logger log = LoggerFactory.getLogger(CatController.class);

	@Resource
	private CatService catService;
	
	
	@Resource
	private JedisDirectClient jedis;

	@RequestMapping(value = "/syn", method = RequestMethod.GET)
	@ResponseBody
	public String synQuery(@RequestParam(value = "id", defaultValue = "") int id) {

		Transaction tran = Cat.newTransaction("CatDemoController", "synQuery");
		
		try{
			
			String result = catService.syn();
			
			tran.setStatus(Transaction.SUCCESS);
			return result;
		}catch(Throwable t){
			tran.setStatus(t);
			Cat.logError(t);
		}finally{
			tran.complete();
		}
		return null;
		
	}
	
	@RequestMapping(value = "/asy", method = RequestMethod.GET)
	@ResponseBody
	public void asyQuery(@RequestParam(value = "id", defaultValue = "") int id) {
		Transaction tran = Cat.newTransaction("CatDemoController", "asyQuery");
		
		try{			
			 catService.asy();
			 tran.setStatus(Transaction.SUCCESS); 			
		}catch(Throwable t){
			tran.setStatus(t);
			Cat.logError(t);
		}finally{
			tran.complete();
		}
	}

	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public String getCache(@RequestParam(value = "key", defaultValue = "") String key) {
		try {
			return jedis.get(key);
		} catch (Exception e) {
			Cat.logError(e);
		}
		return null;
	}
	
	@RequestMapping(value = "/set", method = RequestMethod.GET)
	@ResponseBody
	public String setCache(@RequestParam(value = "key", defaultValue = "") String key) {
		try {
			return jedis.set(key, "value");
		} catch (Exception e) {
			Cat.logError(e);
		}
		return null;
	}

}
