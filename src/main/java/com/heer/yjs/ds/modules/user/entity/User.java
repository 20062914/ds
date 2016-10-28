package com.heer.yjs.ds.modules.user.entity;

import com.heer.yjs.ds.common.annotations.PK;
import com.heer.yjs.ds.common.annotations.Table;
import com.heer.yjs.ds.common.persistence.DataEntity;



@Table(name="user")
public class User extends DataEntity<User> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3400214512259818905L;
	
	
	@PK
	private String id;
	private String username;
	private String password;
	private String xm;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getXm() {
		return xm;
	}
	public void setXm(String xm) {
		this.xm = xm;
	}

}
