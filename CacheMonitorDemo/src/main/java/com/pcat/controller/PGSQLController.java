package com.pcat.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.cat.Cat;
import com.pcat.entity.Repository;
import com.pcat.mapper.RepositoryMapper;
import com.pingan.jinke.infra.jedisx.JedisDirectClient;


@Controller
@RequestMapping("/pg")
public class PGSQLController {
	private static Logger log = LoggerFactory.getLogger(PGSQLController.class);

	@Resource
	private RepositoryMapper repositoryMapper;

	
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	@ResponseBody
	public List<Repository> getRepository(@RequestParam(value = "id", defaultValue = "") int id) {
		return repositoryMapper.selectRepository(id);
	}
	

	
}
