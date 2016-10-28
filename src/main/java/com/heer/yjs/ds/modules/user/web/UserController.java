package com.heer.yjs.ds.modules.user.web;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heer.yjs.ds.common.mapper.JsonMapper;
import com.heer.yjs.ds.common.utils.StringUtils;
import com.heer.yjs.ds.common.web.BaseController;
import com.heer.yjs.ds.modules.user.entity.User;
import com.heer.yjs.ds.modules.user.service.UserSerivce;



@Controller
@RequestMapping(value="/user")
public class UserController extends BaseController {
	
	private transient Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	UserSerivce userSerivce;
	@RequestMapping(value="/get")
	public String getUser(String id,HttpServletResponse  response ){
		User u  = userSerivce.getById("333");

		logger.info(u.getXm());
		if(StringUtils.isEmpty(u.getId()))
			return "error";
		return renderString(response,u);
	}
	@RequestMapping(value="/update")
	public String updateUser(String id,HttpServletResponse  response ){
		User u  = new User();
		u.setId("333");
		u.setXm("天依");
		u.setPassword("洛天依");
		userSerivce.updateUserIgnoreEmpty(u); 
		u  = userSerivce.getById("333");
		if(StringUtils.isEmpty(u.getId()))
			return "error";
		return renderString(response,u); 
	}
	@RequestMapping(value="/query")
	public String queryUser(String id,HttpServletResponse  response ){
		User u  = new User();
	//	u.setXm("天");
	//	u.setWhereSql("username > '333' and username <= '555'");
		u.setOrderBy("username desc");
		Page<User> res = userSerivce.findPage(new Page<User>(1,10), u);
		return renderString(response,res);
	}
	@RequestMapping(value="/insert")
	public String add(String id,HttpServletResponse  response ){
		for (int i = 0; i < 50; i++) {
			User u  = new User();
			u.setXm("姓名"+i);
			u.setUsername("用户名"+i);
			u.setPassword("密码"+i);
			u = userSerivce.save(u);
		}
		return this.queryUser(id, response);
	}
	@RequestMapping(value="/delete")
	public String delete(String id,HttpServletResponse  response ){
		User u  = new User();
		u.setId("a953b60f65a14ff0ab6105fb41d83134");
		userSerivce.delete("a953b60f65a14ff0ab6105fb41d83134");
		return this.queryUser(id, response);
	}
}
