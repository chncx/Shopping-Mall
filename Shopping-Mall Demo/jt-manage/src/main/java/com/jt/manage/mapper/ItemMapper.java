package com.jt.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.jt.common.mapper.SysMapper;
import com.jt.manage.pojo.Item;

public interface ItemMapper extends SysMapper<Item>{

	List<Item> findItemAll();
	
	/**
	 * mybatis中不允许多值传参,必须将多值封装为单值
	 * 1.封装为对象
	 * 2.封装为Map  @Param()
	 * 3.封装为 array或者List
	 * 
	 * $符:只有以字段名称为参数时才使用$.除此之外都是用#号因为有预编译的效果
	 * 防止sql注入攻击.
	 *  说明: 如果使用#号会给参数添加一对""号
	 *  
	 * @param start
	 * @param rows
	 * @return
	 */
	@Select("select * from tb_item order by updated desc limit #{start},#{rows}")
	List<Item> findItemByPage(@Param("start")int start,@Param("rows") int rows);
	
	
	void updateStatus(@Param("status")int status,@Param("ids")Long[] ids);
	//暂时不写
}
