package com.jt.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.vo.SysResult;
import com.jt.web.pojo.Cart;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private HttpClientService httpClient;
	
	//自己定义工具类
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<Cart> findCartByUserId(Long userId) {
		String url = "http://cart.jt.com/cart/query/"+userId;
		String result = httpClient.doGet(url);
		SysResult sysResult = null;
		try {
			sysResult = objectMapper.readValue(result, SysResult.class);
			if(sysResult.getStatus() == 200){
				List<Cart> cartList = 
				(List<Cart>) sysResult.getData();
				return cartList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveCart(Cart cart) {
		String url = "http://cart.jt.com/cart/save";
		Map<String,String> params = new HashMap<String, String>();
		String cartJSON = null;
		try {
			cartJSON = objectMapper.writeValueAsString(cart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.put("cartJSON", cartJSON);
		
		httpClient.doPost(url, params);
	}

	@Override
	public void updateCartNum(Long itemId, Long userId, Integer num) {
		String url = "http://cart.jt.com/cart/update/num/"+userId+"/"+itemId+"/"+num;

		httpClient.doGet(url);
	}
	
	
	
	
	
	
	
	
	
	
}
