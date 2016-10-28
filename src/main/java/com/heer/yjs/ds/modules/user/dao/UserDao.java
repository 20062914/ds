package com.heer.yjs.ds.modules.user.dao;

import org.mybatis.spring.annotation.MapperScan;

import com.heer.yjs.ds.common.persistence.CrudDao;
import com.heer.yjs.ds.modules.user.entity.User;

@MapperScan
public interface UserDao extends CrudDao<User> {

}
