package com.sunchenbin.store.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunchenbin.store.feilong.core.tools.jsonlib.JsonUtil;
import com.sunchenbin.store.manager.test.TestManager;
import com.sunchenbin.store.model.test.Test;

@Controller
public class TestController{

	@Autowired
	private TestManager testManager;
	
	/**
	 * 首页
	 */
	@RequestMapping("/testDate")
	@ResponseBody
	public String testDate(){
		Test test = new Test();
		int count = testManager.findTestCount();
		System.out.println(count);
		List<Test> testList = testManager.findTest(test);
		String json = JsonUtil.format(testList);
		return json;
	}
}
