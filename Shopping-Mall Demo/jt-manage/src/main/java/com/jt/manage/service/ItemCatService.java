package com.jt.manage.service;

import java.util.List;

import com.jt.common.vo.EasyUITree;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.pojo.ItemCat;

public interface ItemCatService {
	public List<ItemCat> findAll(Integer page, Integer rows);

	public String findNameById(Long itemId);

	public List<EasyUITree> findItemCatByParentId(Long parentId);
	
	//实现商品分类的缓存查询
	List<EasyUITree> findCacheByParentId(Long parentId);

	public ItemCatResult findItemCatAll();

	public ItemCatResult findItemCatCache();
}
