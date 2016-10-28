package com.heer.yjs.ds.modules.audit.entity;

import java.util.Date;

import com.heer.yjs.ds.common.annotations.PK;
import com.heer.yjs.ds.common.annotations.Table;
import com.heer.yjs.ds.common.persistence.DataEntity;



@Table(name="audit")
public class Audit extends DataEntity<Audit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3400214512259818905L;
	@PK
	private String id;
	
	private String operate;
	
	private String user;
	
	private String infoid;
	
	private Date operatedate;

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getInfoid() {
		return infoid;
	}

	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}

	public Date getOperatedate() {
		return operatedate;
	}

	public void setOperatedate(Date operatedate) {
		this.operatedate = operatedate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

}
