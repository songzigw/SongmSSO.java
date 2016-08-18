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

import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.event.ClientListener;

/**
 * 后台客户端
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public interface ISSOClient {

    public Backstage getBacstage();

    public String getServerKey();

    public String getServerSecret();

    public String getHost();

    public int getPort();
    
    public void addListener(ClientListener listener);

    public void connect(String key, String secret) throws SSOException;

    public void disconnect();

    public static enum Operation {
        /** 授权 */
        AUTH_REQUEST(1), AUTH_SUCCEED(2), AUTH_FAIL(3),

        /** 用户报道 */
        USER_REPORT(4),

        /** Session */
        SESSION_CREATE(5), SESSION_UPDATE(6), SESSION_REMOVE(7);

        private final int value;

        private Operation(int value) {
            this.value = value;
        }

        public int getValue() {
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
