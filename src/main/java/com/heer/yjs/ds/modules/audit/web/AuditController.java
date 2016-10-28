package com.heer.yjs.ds.modules.audit.web;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.Page;
import com.heer.yjs.ds.common.utils.StringUtils;
import com.heer.yjs.ds.common.web.BaseController;
import com.heer.yjs.ds.modules.audit.entity.Audit;
import com.heer.yjs.ds.modules.audit.service.AuditSerivce;



@Controller
@RequestMapping(value="/audit")
public class AuditController extends BaseController {
	
	private transient Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	AuditSerivce auditSerivce;
	@RequestMapping(value="/get")
	public String getAudit(String id,HttpServletResponse  response ){
		Audit u  = auditSerivce.getById("333");

		if(StringUtils.isEmpty(u.getId()))
			return "error";
		return renderString(response,u);
	}
	@RequestMapping(value="/update")
	public String updateAudit(String id,HttpServletResponse  response ){
		Audit audit  = new Audit();
		audit.setId("333");
		audit.setOperate("天依吃包子");
		
		audit.setUser("洛天依");
		auditSerivce.save(audit); 
		audit  = auditSerivce.getById("333");
		if(StringUtils.isEmpty(audit.getId()))
			return "error";
		return renderString(response,audit); 
	}
	@RequestMapping(value="/query")
	public String queryAudit(String id,HttpServletResponse  response ){
		Audit u  = new Audit();
	//	u.setXm("天");
	//	u.setWhereSql("auditname > '333' and auditname <= '555'");
		u.setOrderBy("auditname desc");
		Page<Audit> res = auditSerivce.findPage(new Page<Audit>(1,10), u);
		return renderString(response,res);
	}
	
}
