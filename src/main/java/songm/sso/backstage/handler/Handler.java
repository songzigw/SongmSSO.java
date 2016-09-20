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
package songm.sso.backstage.handler;

import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.ActionListenerManager;

/**
 * 消息事件处理
 * 
 * @author zhangsong
 *
 */
public interface Handler {

    public int operation();

    void action(ActionListenerManager listenerManager, Protocol pro);
    
    public static enum Operation {
        /** 连接授权 */
        CONN_AUTH(1),

        /** 用户报道 */
        USER_REPORT(2),
        /** 用户登入 */
        USER_LOGIN(8),
        /** 用户退出 */
        USER_LOGOUT(9),
        /** 用户信息编辑 */
        USER_EDIT(10),

        /** Session Create */
        //SESSION_CREATE(3),
        /** Session Update */
        //SESSION_UPDATE(4),
        /** Session Revove */
        //SESSION_REMOVE(5)
        /** Session属性设置 */
        SESSION_ATTR_SET(6),
        /** Session属性获取 */
        SESSION_ATTR_GET(7),
        /** Session对象获取 */
        SESSION_GET(11);

        private final int value;

        private Operation(int value) {
            this.value = value;
        }

        public final int getValue() {
            return value;
        }

        public static Operation getInstance(int v) {
            for (Operation type : Operation.values()) {
                if (type.getValue() == v) {
                    return type;
                }
            }
            return null;
        }
    }
}
