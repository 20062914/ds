package com.heer.yjs.ds.modules.audit.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heer.yjs.ds.common.service.CrudService;
import com.heer.yjs.ds.modules.audit.entity.Audit;
import com.heer.yjs.ds.modules.audit.mapper.AuditMapper;
import com.heer.yjs.ds.modules.user.dao.UserDao;
import com.heer.yjs.ds.modules.user.entity.User;
import com.heer.yjs.ds.modules.user.mapper.UserMapper;
@Service("auditSerivce") 
public class AuditSerivce extends CrudService<AuditMapper, Audit> {
	
	
}
