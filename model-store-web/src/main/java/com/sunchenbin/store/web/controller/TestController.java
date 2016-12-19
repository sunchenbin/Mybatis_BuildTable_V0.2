package com.sunchenbin.store.web.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunchenbin.store.feilong.core.tools.jsonlib.JsonUtil;
import com.sunchenbin.store.manager.common.BaseMysqlCRUDManager;
import com.sunchenbin.store.manager.test.TestManager;
import com.sunchenbin.store.model.test.Test;

@Controller
public class TestController{

	private final Logger LOGGER = Logger.getLogger(TestController.class.getName());  
	
	@Autowired
	private TestManager testManager;
	
	@Autowired
	private BaseMysqlCRUDManager<Test> baseMysqlCRUDManager;
	
	/**
	 * 首页
	 */
	@RequestMapping("/testDate")
	@ResponseBody
	public String testDate(){
		LOGGER.info("111111111111111111");
		LOGGER.debug("111111111111111111");
		LOGGER.warn("111111111111111111");
		Test test = new Test();
		test.setName("aaae333");
		test.setNumber(9L);
		test.setDescription("adfsdfe");
		
		baseMysqlCRUDManager.delete(test);
		baseMysqlCRUDManager.save(test);
		int count = testManager.findTestCount();
		System.out.println(count);
//		List<Test> testList = testManager.findTest(test);
		List<Test> query = baseMysqlCRUDManager.query(test);
		String json = JsonUtil.format(query);
		return json;
	}
}
