package com.jt.cart.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.cart.mapper.CartMapper;
import com.jt.cart.pojo.Cart;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private CartMapper cartMapper;

	@Override
	public List<Cart> findCartListByUserId(Long userId) {
		Cart cart = new Cart();
		cart.setUserId(userId);
		
		return cartMapper.select(cart);
	}

	/**
	 * 1.根据userId和itemId查询数据库
	 * 2.如果数据库中有数据,则做数量的更新操作
	 *   如果数据库中没有数据,则做购物车新增
	 */
	@Override
	public void saveCart(Cart cart) {
		Cart cartDB = cartMapper.findCartByUI(cart);
		if(cartDB == null){
			cart.setCreated(new Date());
			cart.setUpdated(cart.getCreated());
			cartMapper.insert(cart);
		}else{
			int num = cart.getNum() + cartDB.getNum();
			cartDB.setNum(num); //数量的更新操作
			cartMapper.updateByPrimaryKeySelective(cartDB);
		}
	}

	@Override
	public void updateCartNum(Cart cart) {
		//由于条件限制 不能使用通用Mapper
		cart.setUpdated(new Date());
		cartMapper.updateCartNum(cart);
	}
	
	
	
	
	
	
	
	
	
	
	
}
