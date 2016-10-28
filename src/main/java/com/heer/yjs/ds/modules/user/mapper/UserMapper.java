package com.heer.yjs.ds.modules.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

import com.heer.yjs.ds.common.persistence.CrudMapper;
import com.heer.yjs.ds.modules.user.entity.User;

@MapperScan
public interface UserMapper extends CrudMapper<User> {
	
	@Select("select * from user where id= #{id}")
	 public User getUserById(@Param("id") String id);
}
