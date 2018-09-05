package com.jt.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.web.pojo.User;
import com.jt.web.thread.UserThreadLocal;

import redis.clients.jedis.JedisCluster;

public class UserInterceptor implements HandlerInterceptor{
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	//在Controller方法执行之前
	/**
	 * 通过拦截器的方式实现用户登陆检测.如果用户没有登陆则跳转登录页面
	 * 问题:
	 * 		如何检测用户是否登录
	 * 		1.检测Cookie是否有值    JT_TICKET:token
	 *      2.检测redis缓存服务器是否有数据    token:userJSON
	 * 实现步骤:
	 * 		1.获取Cookie信息   获取token
	 *      2.获取redis中缓存数据
	 *      3.如果上述过程中有一个为null,应该跳转登录页面
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Cookie[] cookies  = request.getCookies();
		
		String token = null;
		for (Cookie cookie : cookies) {
			
			if("JT_TICKET".equals(cookie.getName())){
				token = cookie.getValue();
				break;
			}
		}
		
		//判断token是否为null
		if(!StringUtils.isEmpty(token)){
			
			//判断redis缓存中是否有数据
			String userJSON = jedisCluster.get(token);
			
			if(!StringUtils.isEmpty(userJSON)){
			
				User user = objectMapper.readValue(userJSON, User.class);
				//可以将user信息保持到request域中/session域 但是该方法无法在业务层中动态获取userId
				//需要在业务接口中 添加userId等信息 
				//request.setAttribute("JT_USER", user);
				UserThreadLocal.set(user);
				 //放行请求
				return true;
			}
		}
		
		//如果程序执行到这里说明用户登陆有误,则需要重新登陆
		response.sendRedirect("/user/login.html");
		return false; //表示拦截
	}

	//controller中的方法刚刚执行完
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	//返回页面前
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//将threadLocal销毁
		UserThreadLocal.remove();
		
	}

}
