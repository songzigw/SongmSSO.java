/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package songm.sso.backstage.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户与服务端的会话
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Session implements Serializable {

    private static final long serialVersionUID = 3367972053942347508L;

    /** 保存在客户端session标识 */
    public static final String USER_SESSION_KEY = "songm_session";
    
    public static final String USER_INFO = "user_info";

    /** 会话唯一标示 */
	private String sesId;

	/** 用户ID */
    private String userId;

	/** 会话创建时间 */
	private Date createdTime;

	/** 会话访问时间 */
	private Date accessTime;

	private Map<String, Object> attribute;
	
	public Session(String sessionId) {
		this.sesId = sessionId;
		createdTime = new Date();
		accessTime = createdTime;
	}

	public String getSesId() {
		return sesId;
	}

	public void setSesId(String id) {
		this.sesId = id;
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

	public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getAccessTime() {
		return accessTime.getTime();
	}

	public void updateAccessTime() {
		accessTime = new Date();
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sesId == null) ? 0 : sesId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Session other = (Session) obj;
        if (sesId == null) {
            if (other.sesId != null)
                return false;
        } else if (!sesId.equals(other.sesId))
            return false;
        return true;
    }
}
