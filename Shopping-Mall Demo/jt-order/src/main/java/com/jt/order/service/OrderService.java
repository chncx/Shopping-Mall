package com.jt.order.service;

import com.jt.order.pojo.Order;

public interface OrderService {

	String saveOrder(Order order);

	Order findOrderById(String orderId);

}
