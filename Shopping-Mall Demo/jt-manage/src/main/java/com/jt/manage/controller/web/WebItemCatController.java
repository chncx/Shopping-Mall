package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.service.ItemCatService;

@Controller
@RequestMapping("/web")
public class WebItemCatController {
	
	@Autowired
	private ItemCatService itemCatService;
	
	//http://manage.jt.com/web/itemcat/all?callback=category.getDataService
	@RequestMapping("/itemcat/all")
	@ResponseBody
	public MappingJacksonValue findItemCatAll(String callback){
		//1.查询全部的商品分类信息
		ItemCatResult itemCatResult = itemCatService.findItemCatCache();//实现缓存
		
		//2.通过工具类回传数据
		MappingJacksonValue jacksonValue = new MappingJacksonValue(itemCatResult);
		
		//3.添加回调方法
		jacksonValue.setJsonpFunction(callback);
		return jacksonValue;
	}
}
