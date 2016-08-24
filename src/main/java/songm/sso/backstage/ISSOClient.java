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

package songm.sso.backstage;

import songm.sso.backstage.event.ClientListener;

/**
 * 后台客户端接口
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public interface ISSOClient {

    public void addListener(ClientListener listener);

    public void connect(String key, String secret) throws SSOException;

    public void disconnect();

    public static enum Operation {
        /** 连接授权 */
        CONN_AUTH(1),

        /** 用户报道 */
        USER_REPORT(2),

        /** Session Create */
        SESSION_CREATE(3),
        /** Session Update */
        SESSION_UPDATE(4),
        /** Session Revove */
        SESSION_REMOVE(5);

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
