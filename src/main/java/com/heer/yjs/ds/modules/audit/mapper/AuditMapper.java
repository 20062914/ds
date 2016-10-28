package com.heer.yjs.ds.modules.audit.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

import com.heer.yjs.ds.common.persistence.CrudMapper;
import com.heer.yjs.ds.modules.audit.entity.Audit;
import com.heer.yjs.ds.modules.user.entity.User;

@MapperScan
public interface AuditMapper extends CrudMapper<Audit> {

}
