/**
 * com.heer.dsp
 */
package com.heer.yjs.ds.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heer.yjs.ds.common.persistence.CrudDao;
import com.heer.yjs.ds.common.persistence.CrudMapper;
import com.heer.yjs.ds.common.persistence.DataEntity;

/**
 * Service基类
 * @author qiukm
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class CrudService<D extends CrudMapper<T>, T extends DataEntity<T>> extends BaseService {
	
	/**
	 * 持久层对象
	 */
	@Autowired
	protected D dao;
	
	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(String id) {
		return dao.selectByPK(id);
	}
	
	/**
	 * 查询列表数据
	 * @param entity
	 * @return
	 */
	public List<T> findList(T entity) {
		return dao.selectByCon(entity);
	}
	
	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @param entity
	 * @return
	 **/
	public Page<T> findPage(Page<T> page, T entity) {
		PageHelper.startPage(page.getPageNum(),page.getPageSize());
		return (Page<T>) dao.selectByCon(entity);
	}

	/**
	 * 保存数据（插入或更新）
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public T save(T entity) {
		if (entity.getIsNewRecord()){
			entity.preInsert();
			dao.insertOne(entity);
		}else{
			entity.preUpdate();
			dao.updateIgnoreEmpty(entity);
		}
		return entity;
	}
	
	/**
	 * 删除数据bypk
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void delete(String id) {
		dao.deleteByPK(id);
	}

	
	public T getById(String id ){
		return dao.selectByPK(id);
		 
	}
	/**
	 * @auther:Lint
	 * @createtTime: 2016年10月26日
	 * @注释: 通过传入的sql条件查询
	 **/
	public List<T> findListBySql(T entity){
		return dao.selectBySql(entity);
	}
	
	/**
	 * @auther:Lint
	 * @createtTime: 2016年10月26日
	 * @注释：service层获取注入的mapper对象
	 **/
	public D getD(){
		return dao;
	} 
}
