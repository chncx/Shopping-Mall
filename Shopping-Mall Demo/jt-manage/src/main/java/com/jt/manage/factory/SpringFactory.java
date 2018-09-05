package com.jt.manage.factory;

import java.util.Calendar;

import org.springframework.beans.factory.FactoryBean;

public class SpringFactory implements FactoryBean<Calendar>{
	
	@Override
	public Calendar getObject() throws Exception {
		System.out.println("spring调用工厂模式创建对象");
		return Calendar.getInstance();
	}

	//获取对象的类型
	@Override
	public Class<?> getObjectType() {
		
		return Calendar.class;
	}
	
	//通过工厂模式创建的对象 是否是单例的
	@Override
	public boolean isSingleton() {
		
		return false;
	}

}
