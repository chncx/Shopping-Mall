package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.vo.SysResult;
import com.jt.web.pojo.User;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private HttpClientService httpClient;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	
	@Override
	public void saveUser(User user) {
		//1.定义远程访问路径
		String url = "http://sso.jt.com/user/register";
		
		//2.封装user数据
		Map<String,String> params = new HashMap<String,String>();
		params.put("username", user.getUsername());
		params.put("password", user.getPassword());
		params.put("phone", user.getPhone());
		params.put("email", user.getEmail());
		
		//3.进行post请求
		String result =  httpClient.doPost(url, params);
		
		try {
			//4.将result数据转化为SysResult对象
			SysResult sysResult = objectMapper.readValue(result, SysResult.class);
			
			//5.判断后台入库操作是否正确
			if(200 != sysResult.getStatus()){
				
				throw new RuntimeException();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}


	@Override
	public String findUserByUP(User user) {
		String url = "http://sso.jt.com/user/login";
		Map<String,String> params = new HashMap<String,String>();
		params.put("username", user.getUsername());
		params.put("password", user.getPassword());
		String result = httpClient.doPost(url, params);
		//判断返回是否正确
		try {
			SysResult sysResult = objectMapper.readValue(result, SysResult.class);
			if(sysResult.getStatus() == 200){
				//表示数据返回正确
				return (String) sysResult.getData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	

}
