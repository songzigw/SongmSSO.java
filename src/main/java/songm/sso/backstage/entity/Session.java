/*
 * Copyright (c) 2016, zhangsong <songm.cn>.
 *
 */

package songm.sso.backstage.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端与服务端的会话
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Session {

	/** 默认超时间 */
	public static final long TIME_OUT = 1000 * 24 * 60 * 60;

	/** 客户端与服务端会话唯一标示符 */
	public static final String CLIENT_KEY = "songm_sso_key";

	/** 会话唯一标示 */
	private String id;

	/** 会话创建时间 */
	private Date createdTime;

	/** 会话访问时间 */
	private Date accessTime;

	private Map<String, Object> attribute;
	
	public Session(String sessionId) {
		this.id = sessionId;
		createdTime = new Date();
		accessTime = createdTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getAttribute(String name) {
		if (attribute == null) {
			return null;
		}
		return attribute.get(name);
	}

	public void setAttribute(String name, Object value) {
		if (attribute == null) {
			attribute = new HashMap<String, Object>();
		}
		attribute.put(name, value);
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}

	public long getCreatedTime() {
		return createdTime.getTime();
	}

	public long getAccessTime() {
		return accessTime.getTime();
	}

	public void updateAccessTime() {
		accessTime = new Date();
	}

	public boolean isTimeout() {
		if (new Date().getTime() - accessTime.getTime() > TIME_OUT) {
			return true;
		}
		return false;
	}
}
