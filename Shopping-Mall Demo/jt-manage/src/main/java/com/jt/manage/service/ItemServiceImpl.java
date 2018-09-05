package com.jt.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.vo.EasyUIResult;
import com.jt.manage.mapper.ItemDescMapper;
import com.jt.manage.mapper.ItemMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private ItemDescMapper itemDescMapper;

	@Override
	public List<Item> findItemAll() {
		
		return itemMapper.findItemAll();
	}

	@Override
	public EasyUIResult findItemByPage(Integer page, Integer rows) {
		
		/**
		 * 通用Mapper 查询操作时  如果传入的数据不为null,则会充当where条件
		 * select count(*) from tb_item
		 * select * from tb_item limit 0,20       1
s		   select * from tb_item limit 20,20      2
s          select * from tb_item limit 40,20
		 */
		int total = itemMapper.selectCount(null);
		
		int start = (page - 1) * rows;
		
		List<Item> itemList = itemMapper.findItemByPage(start,rows);
		
		EasyUIResult result = new EasyUIResult(total, itemList);
		
		return result;
	}

	@Override
	public void saveItem(Item item,String desc) {
		//需要补齐数据
		item.setStatus(1); //表示商品上架
		item.setCreated(new Date());
		item.setUpdated(item.getCreated());
		itemMapper.insert(item);
		
		//SELECT LAST_INSERT_ID() 获取当前线程内Id的最大值
		ItemDesc itemDesc = new ItemDesc();
		//System.out.println(item.getId());
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(item.getCreated());
		itemDesc.setUpdated(item.getCreated());
		itemDescMapper.insert(itemDesc);
		
	}

	@Override
	public void updateItem(Item item,String desc) {
		//为数据赋值
		item.setUpdated(new Date());
		//表示动态更新操作. 只更新不为null的数据
		itemMapper.updateByPrimaryKeySelective(item);
		
		//商品描述信息更新
		ItemDesc itemDesc= new ItemDesc();
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(item.getId());
		itemDesc.setUpdated(item.getUpdated());
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
	}

	@Override
	public void deleteItems(Long[] ids) {
		//根据主键删除
		itemMapper.deleteByIDS(ids);
		itemDescMapper.deleteByIDS(ids);
	}

	@Override
	public void updateStatus(int status, Long[] ids) {
		//update tb_item set status = #{status},updated = now() where id in (1,2,3,4,5)
		
		itemMapper.updateStatus(status,ids);
		
	}
	
	//根据itemId查询商品详情信息
	@Override
	public ItemDesc findItemDesc(Long itemId) {
		
		return itemDescMapper.selectByPrimaryKey(itemId);
	}

	@Override
	public Item finditemById(Long itemId) {
		
		return itemMapper.selectByPrimaryKey(itemId);
	}

	
	
	
	
	
	
	
	
	
}
