package com.jt.web.thread;

import com.jt.web.pojo.User;

public class UserThreadLocal {
	
	//如果保存数据有多个,则使用Map集合
	private static ThreadLocal<User> userThread = new ThreadLocal<User>();
	
	public static void set(User user){
		
		userThread.set(user);
	}
	
	public static User get(){
		
		return userThread.get();
	}
	
	//线程销毁方法
	public static void remove(){
		
		userThread.remove();
	}
}
