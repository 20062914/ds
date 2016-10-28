package com.heer.yjs.ds.modules.user.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heer.yjs.ds.common.service.CrudService;
import com.heer.yjs.ds.modules.user.dao.UserDao;
import com.heer.yjs.ds.modules.user.entity.User;
import com.heer.yjs.ds.modules.user.mapper.UserMapper;
@Service("userSerivce")
public class UserSerivce extends CrudService<UserMapper, User> {


	 public User getUserById(String id){
		 return super.getD().getUserById(id);
	 }
	 
	 public int updateUserIgnoreEmpty(User u){
		 return super.getD().updateIgnoreEmpty(u);
	 }
}
