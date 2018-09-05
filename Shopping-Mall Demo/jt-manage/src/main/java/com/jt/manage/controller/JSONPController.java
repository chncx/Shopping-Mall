package com.jt.manage.controller;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.manage.pojo.Item;

@Controller
public class JSONPController {
	
	/**
	 * 关于JSON写法
	 * @param callback
	 * @return
	 * @throws JsonProcessingException 
	 */
	
	//返回值data数据必须是JSON
	//http://manage.jt.com/web/testJSONP?callback=asdfasdfad
	//@RequestMapping("/web/testJSONP")
	//@ResponseBody
	/*public String  testJSONP(String callback){
		
		return callback + "(" + "{\"id\":\"100\",\"name\":\"tom\"}" +  ")";
	}*/
	
	
	//@RequestMapping("/web/testJSONP")
	//@ResponseBody
	public String  testJSONP(String callback) throws JsonProcessingException{
		Item item = new Item();
		item.setId(1000L);
		item.setTitle("卖姑娘的小火柴");
		ObjectMapper objectMapper = new ObjectMapper();
		String  json = objectMapper.writeValueAsString(item);
		return callback + "(" + json +  ")";
	}
	
	//利用Spring提供的JSONP的解决方案
	@RequestMapping("/web/testJSONP")
	@ResponseBody
	public MappingJacksonValue testJacksonValue(String callback){
		Item item = new Item();
		item.setId(1000L);
		item.setTitle("卖姑娘的小火柴");
		MappingJacksonValue jacksonValue = 
				new MappingJacksonValue(item);
		jacksonValue.setJsonpFunction(callback);
		return jacksonValue;
	}
	
	
	
}
