package com.pingan.jinke.infra.padis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pingan.jinke.infra.padis.common.Result;

@Controller
@RequestMapping(value = "/padis")
public class SlotController {

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public Result test() {
		
		Result<String> r = new Result<String>();
		r.setMessages("fsdafsdafasfas");
		r.setResult("ffsafsaffsdfafasaf");
		r.setSuccess(true);
		return r;
	}
	
}
