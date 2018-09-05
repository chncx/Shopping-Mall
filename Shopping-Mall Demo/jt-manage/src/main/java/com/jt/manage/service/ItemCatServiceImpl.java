package com.jt.manage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.common.service.BaseService;
import com.jt.common.service.RedisService;
import com.jt.common.vo.EasyUITree;
import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemCat;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

@Service
public class ItemCatServiceImpl extends BaseService<ItemCat> implements ItemCatService{
	//关联ItemCatMapper
	@Autowired
	private ItemCatMapper itemCatMapper;
	
	//注入jedis对象
	//private Jedis jedis;
	@Autowired
	private JedisCluster jedisCluster;
	//private RedisService redisService;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	
	public List<ItemCat> findAll(Integer page, Integer rows){
		//return itemCatMapper.findAll();
		//分页支持，startPage方法是静态
		//内部就调用拦截器，startPage相当于事务开启begin，开启分页操作
		//它下面第一条的执行的查询的SQL语句
		PageHelper.startPage(page, rows); 	
		//第一条查询SQL被拦截，SQL语句拼接 limit page, rows
		List<ItemCat> itemCatList = itemCatMapper.findAll();
		
		//返回值不能直接返回，必须放在PageInfo对象中
		//这里和线程安全有关！直接返回方式它会产生线程安全问题
		//怎么解决？利用ThreadLocal，把当前对象和当前线程绑定，每个用户独立线程，
		PageInfo<ItemCat> pageInfo = new PageInfo<ItemCat>(itemCatList);
		
		return pageInfo.getList();
		
	}

	@Override
	public String findNameById(Long itemId) {
		
		return itemCatMapper.selectByPrimaryKey(itemId).getName();
	}

	/**
	 * 1.根据条件查询需要的结果 where parent_id = 0
	 * 2.需要将ItemCat集合转化为List<EasyUITree>
	 * 3.通过循环遍历的方式实现List赋值.
	 * 
	 * state  "open"/"closed"
	 */
	@Override
	public List<EasyUITree> findItemCatByParentId(Long parentId) {
		ItemCat itemCat = new ItemCat();
		itemCat.setParentId(parentId);
		//查询需要的结果
		List<ItemCat> itemCatList = 
						itemCatMapper.select(itemCat);
		//2.创建返回集合对象
		List<EasyUITree> treeList = new ArrayList<EasyUITree>();
		
		//3.将集合进行转化
		for (ItemCat itemCatTemp : itemCatList) {
			EasyUITree easyUITree = new EasyUITree();
			easyUITree.setId(itemCatTemp.getId());
			easyUITree.setText(itemCatTemp.getName());
			//如果是父级则暂时先关闭,用户需要时在展开
			String state = 
	itemCatTemp.getIsParent() ? "closed" : "open";
			easyUITree.setState(state);
			treeList.add(easyUITree);
		}
		return treeList;
	}

	@Override
	public List<EasyUITree> findCacheByParentId(Long parentId) {
		
		String key = "ITEM_CAT_"+parentId;
		String result = jedisCluster.get(key);
		List<EasyUITree> easyUITreeList = null;
		try {
			//判断数据是否为null
			if(StringUtils.isEmpty(result)){
				//表示查询数据库
				easyUITreeList = 
						findItemCatByParentId(parentId);
				//将数据转化为JSON串
				String jsonData = objectMapper.writeValueAsString(easyUITreeList);
				//将数据保存到缓存中
				jedisCluster.set(key, jsonData);
				return easyUITreeList;
			}else{
				//表示缓存数据不为null;
				EasyUITree[] easyUITrees = 
				objectMapper.readValue(result,EasyUITree[].class);
				easyUITreeList = Arrays.asList(easyUITrees);
				return easyUITreeList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	/**
	 * 思考:
	 * 	1.首先获取1级商品分类信息
	 * 	2.根据1级商品分类菜单查询2级商品分类信息
	 *  3.根据2级商品分类信息查询3级商品分类信息
	 *  根据上述的描述信息,查询的效率太低了.
	 *  
	 *  改进:
	 *  1.能否采用合理的数据结构,让我们的查询只查询一次.实现商品
	 *  分类的划分.
	 *    Map<parentId,List<ItemCat>>
	 */
	@Override
	public ItemCatResult findItemCatAll() {
		
		//1.实现商品分类划分
		Map<Long,List<ItemCat>> map = 
				new HashMap<Long,List<ItemCat>>();
		
		//2.获取全部的商品分类信息
		ItemCat itemCatDB = new ItemCat();
		itemCatDB.setStatus(1);//表示状态正常
		List<ItemCat> itemCats = 
				itemCatMapper.select(itemCatDB);
		
		//3.实现商品数据的封装
		for (ItemCat itemCatTemp : itemCats) {
			if(map.containsKey(itemCatTemp.getParentId())){
				//Map中已经包含了该商品分类的父级 Id
				map.get(itemCatTemp.getParentId()).add(itemCatTemp);
			}else{
				List<ItemCat> itemCatTempList = 
						new ArrayList<ItemCat>();
				itemCatTempList.add(itemCatTemp);
				map.put(itemCatTemp.getParentId(), itemCatTempList);
			}
		}
		
		ItemCatResult result = new ItemCatResult();
		
		//4.准备一级商品分类的List集合信息
		List<ItemCatData> itemCatDataList1 = 
								new ArrayList<ItemCatData>();
		
		//5.为一级商品分类赋值
		for (ItemCat itemCat1 : map.get(0L)) {
			ItemCatData itemCatData1 = new ItemCatData();
			itemCatData1.setUrl("/products/"+itemCat1.getId()+".html");
			itemCatData1.setName("<a href='"+ itemCatData1.getUrl()+"'>"+ itemCat1.getName() +"</a>");
			
			//实现2级商品分类信息
			List<ItemCatData> itemCatDataList2 = 
					new ArrayList<ItemCatData>();
			
			//循环遍历2级商品分类信息
			for (ItemCat itemCat2 : map.get(itemCat1.getId())) {
				ItemCatData itemCatData2 = new ItemCatData();
				itemCatData2.setUrl("/products/"+itemCat2.getId());
				itemCatData2.setName(itemCat2.getName());
				
				//实现三级商品分类信息维护
				List<String> itemCatDataList3 = new ArrayList<String>();
				
				for (ItemCat itemCat3 : map.get(itemCat2.getId())) {
					itemCatDataList3.
				add("/products/"+ itemCat3.getId()+"|" + itemCat3.getName());
				}
				itemCatData2.setItems(itemCatDataList3);
				itemCatDataList2.add(itemCatData2);
			}
			itemCatData1.setItems(itemCatDataList2);
			itemCatDataList1.add(itemCatData1);
			if(itemCatDataList1.size() > 13){
				break;
			}
		}
		result.setItemCats(itemCatDataList1);
		return result;
	}

	
	/**
	 * 1.先查询缓存数据
	 * 	如果数据不为null: 需要将缓存中的数据转化为java对象
	 *  如果数据为null:  需要查询数据库,之后将数据保存到缓存中.
	 */
	@Override
	public ItemCatResult findItemCatCache() {
		String key = "ITEM_CAT_ALL";
		String jsonData = jedisCluster.get(key);
		ItemCatResult itemCatResult = null;
		try {
			if(StringUtils.isEmpty(jsonData)){
				itemCatResult = findItemCatAll();
				//将数据保存到缓存中
				String itemCatJSON = 
				objectMapper.writeValueAsString(itemCatResult);
				jedisCluster.set(key, itemCatJSON);
				//System.out.println("商品分类列表第一次查询");
			}else{
				//表示缓存中有数据
				itemCatResult
				= objectMapper.readValue(jsonData,ItemCatResult.class);
				//System.out.println("商品分类列表缓存操作");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemCatResult;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
