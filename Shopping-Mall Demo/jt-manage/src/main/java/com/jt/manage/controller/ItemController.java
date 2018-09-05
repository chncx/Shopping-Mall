package com.jt.manage.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemController {
	
	private static final Logger logger = 
			Logger.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;
	
	
	//要求:/find/itemAll要求返回全部商品信息 要求根据修改时间排序
/*	@RequestMapping("/find/itemAll")
	@ResponseBody
	public List<Item> findItem(){
		
		return itemService.findItemAll();
	}*/
	
	//http://localhost:8091/item/query?page=1&rows=50
	//实现商品分页查询
	@RequestMapping("/query")
	@ResponseBody
	public EasyUIResult findItemByPage(Integer page,Integer rows){
		
		return itemService.findItemByPage(page,rows);
	}
	
	
	@RequestMapping("/save")
	@ResponseBody
	public SysResult saveItem(Item item,String desc){
		try {
			itemService.saveItem(item,desc);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201,"商品新增失败");
	}
	
	//商品修改
	@RequestMapping("/update")
	@ResponseBody
	public SysResult updateItem(Item item,String desc){
		try {
			itemService.updateItem(item,desc);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "商品修改失败");
	}
	
	//ids:1,2,3,4,5,6
	@RequestMapping("/delete")
	@ResponseBody
	public SysResult deleteItem(Long[] ids){
		try {
			itemService.deleteItems(ids);
			//System.out.println("asdfasdf"); //效率太低
			logger.info("{我是一个打桩日志}");
			return SysResult.oK();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("!!!!!!!!!!!!!!!!!"+e.getMessage());
			//logger.error("~~~~~~~~~~"+e.getMessage());
		}
		return SysResult.build(201, "商品删除失败");
	}

	
	//实现商品上架  /item/reshelf
	@RequestMapping("/reshelf")
	@ResponseBody
	public SysResult reshelf(Long[] ids){
		try {
			int status = 1;  //商品上架
			itemService.updateStatus(status,ids);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "商品上架失败");
	}
	
	
	
	//商品下架  /item/instock
	@RequestMapping("/instock")
	@ResponseBody
	public SysResult instock(Long[] ids){
		try {
			int status = 2;  //商品下架
			itemService.updateStatus(status,ids);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "商品下架失败");
	}
	
	
	//实现商品描述信息回显
	@RequestMapping("/query/item/desc/{itemId}")
	@ResponseBody
	public SysResult findItemDescById(@PathVariable Long itemId){
		try {
			ItemDesc itemDesc = itemService.findItemDesc(itemId);
			return SysResult.oK(itemDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "商品详情查询失败");
	}
	
	
	
	
	
	
	
}
