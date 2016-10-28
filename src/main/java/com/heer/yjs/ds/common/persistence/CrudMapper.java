package com.heer.yjs.ds.common.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Mapper继承该接口后，无需编写mapper.xml文件，即可获得CRUD功能
 * 
 * @param <T>
 */
public interface CrudMapper<T> {

	/**
	 * 新增
	 */
	public int insertOne(T entity);

	/**
	 * 根据主键删除，主键名默认为id
	 */
	public int deleteByPK(Object pk);

	/**
	 * 根据主键修改，主键名默认为id
	 */
	public int updateByPK(T entity);
	
	/**
	 * 根据主键修改，主键名默认为id
	 */
	public int updateIgnoreEmpty(T entity);


	/**
	 * 根据主键查找，主键名默认为id
	 */
	public T selectByPK(Object pk);

	/**
	 * 查询全部
	 */
	public List<T> selectAll();
	
	/**
	 * @auther:Lint
	 * @createtTime: 2016年10月26日
	 * @注释: 根据entity模糊查询
	 **/
	public List<T> selectByCon(T entity);
	
	/**
	 * @auther:Lint
	 * @createtTime: 2016年10月26日
	 * @注释: 根据传入的sql查询
	 **/
	public List<T> selectBySql(T entity);
	
	/**
	 * find By sql
	 * @author Lint
	 * @注释： 源自 dsp 未实现自动注入！！
	 * 
	 */
	public List<Map<String,Object>> findBySql(@Param("sql")String sql);

}
