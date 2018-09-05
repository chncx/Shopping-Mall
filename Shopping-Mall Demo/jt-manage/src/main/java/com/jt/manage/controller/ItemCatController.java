package com.jt.manage.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.EasyUITree;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.ItemCat;
import com.jt.manage.service.ItemCatService;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("/item/cat")
public class ItemCatController {
	
	
	
	
	@Autowired
	private ItemCatService itemCatService;
	
	/*
	 * get			/item/cat/all?page=2&rows=10
	 * post			写在表单中<input type="hidden" name="page" value="2">
	 * RESTFul		/itemcat/all/2/10
	 */
	@RequestMapping("/all/{page}/{rows}")
	@ResponseBody	//返回值是一个Json字符串
	public List<ItemCat> findAll(@PathVariable Integer page,@PathVariable  Integer rows){
		List<ItemCat> itemCatList = itemCatService.findAll(page, rows);
		return itemCatList;
	}
	
	/**问题:  @ResponseBody 为什么乱码????
	 *  
	 *  1.如果回传数据是一个对象使用@ResponseBody 返回时默认以utf-8编码
	 *  2.如果回传字符串,则默认以iso-8859-1编码
	 * 
	 * @param itemId
	 * @param response
	 * @throws IOException
	 */
	//实现商品分类目录的回显
	@RequestMapping(value="/queryItemName",produces="text/html;charset=utf-8")
	@ResponseBody
	public String findItemCatNameById(Long itemId,HttpServletResponse response) throws IOException{
		
		//String name = itemCatService.findNameById(itemId);
		//response.setContentType("text/html;charset=utf-8");
		
		//response.getWriter().write(name);
		return itemCatService.findNameById(itemId);
	}
	
	
	//http://localhost:8091/item/cat/list
	/**
	 * @RequestParam(value="id",defaultValue="0",required=true)
	 * 	id表示接收参数的名称
	 *  defaultValue 默认值
	 *  required=true 该参数必须传递,否则SpringMVC校验报错.
	 * @param parentId
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<EasyUITree> findItemCat(@RequestParam(value="id",defaultValue="0")Long parentId){
		
		//1.查询一级商品分类目录
		//Long parentId = 0L;
		//return itemCatService.findItemCatByParentId(parentId);
		return itemCatService.findCacheByParentId(parentId);
	}
	
	
}
