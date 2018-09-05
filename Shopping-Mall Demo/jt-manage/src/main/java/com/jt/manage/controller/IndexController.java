package com.jt.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	//跳转后台首页      /
	@RequestMapping("/index")
	public String index(){
		
		return "index";
	}
	
	//实现页面的通用跳转
	@RequestMapping("/page/{moduleName}")
	public String module(@PathVariable String moduleName){
		
		return moduleName;
	}
	
	
	
	
}
