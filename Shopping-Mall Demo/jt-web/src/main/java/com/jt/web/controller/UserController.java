package com.jt.web.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.web.pojo.User;
import com.jt.web.service.UserService;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	//实现页面的通用跳转  http://www.jt.com/user/login.html
	@RequestMapping("/{moduleName}")
	public String toModule(@PathVariable String moduleName){
		
		return moduleName;
	}
	
	@RequestMapping("/doRegister")
	@ResponseBody
	public SysResult saveUser(User user){
		try {
			userService.saveUser(user);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "用户新增失败");
	}
	
	
	//使用用户登陆
		/*
		 * 关于requestMapping说明
		 *  主要作用:标识请求路径和方法的映射关系
		 *  SpringMVC组件:处理器映射器
		 *  请求url:"/service/user/doLogin?r=0.4649011087398145"
		 *  处理器映射器:/service/user/doLogin 映射class类型中的方法.
		 *  最终hander处理器处理该业务.
		 *  
		 *  cookie.setMaxAge(0);  删除cookie
		 *  cookie.setMaxAge(-1); 当关闭回话时Cookie删除
		 *  cookie.setMaxAge(>0); 设定了Cookie的声明周期 过时失效
		 */
		@RequestMapping("/doLogin")
		@ResponseBody
		public SysResult doLogin(User user,HttpServletResponse response){
			try {
				//获取服务端返回的Token数据
				String token = userService.findUserByUP(user);
				//判断返回值是否有效
				if(StringUtils.isEmpty(token)){
					
					throw new RuntimeException();
				}
				//表示获取token是正确的,将token保存到Cookie中
				Cookie cookie = new Cookie("JT_TICKET", token);
				cookie.setPath("/"); //表示在浏览器根目录生效
				cookie.setMaxAge(3600 * 24 * 7); //设定生命周期 单位秒
				response.addCookie(cookie);
				
				return SysResult.oK(); //表示程序正常返回
			} catch (Exception e) {
				e.printStackTrace();
			}
			return SysResult.build(201,"用户登陆失败");
		}
		
		
		@RequestMapping("/logout")
		public String logout(HttpServletRequest request,HttpServletResponse response){
			//获取cookie数据
			Cookie[] cookies  = request.getCookies();
			for (Cookie cookie : cookies) {
				if("JT_TICKET".equals(cookie.getName())){
					String ticket = cookie.getValue();
					jedisCluster.del(ticket);
					break; //跳出循环
				}
			}
			//清空Cooke数据
			Cookie cookie = new Cookie("JT_TICKET", "");
			cookie.setPath("/");
			cookie.setMaxAge(0);//立即删除
			response.addCookie(cookie);
		
			//重定向到系统首页
			return "redirect:/index.html";
		}
		
		
		
		
		
}
