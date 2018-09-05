package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.web.pojo.Item;
import com.jt.web.pojo.ItemDesc;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private HttpClientService httpClient;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * jt-webservice层代码.需要访问manage.jt.com服务器获取
	 * Item对象信息.
	 * 知识点讲解:
	 * 	 跨域:在浏览器端,如果访问不同的服务器需要进行跨域的访问.
	 * 解决方案:
	 * 	如果在service层可以发出http请求,那么可以获取远程数据.
	 * 
	 * 跨系统调用参数的方式:
	 * 	1.JSON串进行参数传递   特点:简单快速 
	 * 	2.xml的方式(报文)    特点:安全性更好 内容较多(标签) 500
	 * 	
	 * 跨系统调用的步骤:
	 * 	1.确定远程的URL
	 *  2.判断调用是否需要参数 则封装为Map/restFul
	 *  3.调用工具类方法获取远程传输信息JSON数据.
	 *  4.将JSON转化为java对象
	 *  
	 *  作业:采用restFul实现数据获取
	 */
	@Override
	public Item findItemById(Long itemId) {
		Item item = null;
		String url = "http://manage.jt.com/web/item/findItemById";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("itemId", itemId+"");
		
		String result = httpClient.doGet(url,params);
		try {
			if(StringUtils.isEmpty(result)){
				
				throw new RuntimeException();
			}
			item = objectMapper.readValue(result, Item.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public ItemDesc findItemDescById(Long itemId) {
		ItemDesc itemDesc = null;
		String url = "http://manage.jt.com/web/item/findItemDescById/"+itemId;
		
		String result = httpClient.doGet(url);
		try {
			if(StringUtils.isEmpty(result)){
				
				throw new RuntimeException();
			}
			itemDesc = objectMapper.readValue(result, ItemDesc.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
	
	
	
	
	
	
	
	
	
	
}
