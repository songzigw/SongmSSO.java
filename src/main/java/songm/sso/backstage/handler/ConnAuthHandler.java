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

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.ActionEvent.EventType;
import songm.sso.backstage.event.ActionListenerManager;
import songm.sso.backstage.utils.JsonUtils;
import songm.sso.backstage.entity.Result;

public class ConnAuthHandler implements Handler {

    @Override
    public int operation() {
        return Handler.Operation.CONN_AUTH.getValue();
    }

    @Override
    public void action(ActionListenerManager listenerManager, Protocol pro) {
        Type type = new TypeToken<Result<Backstage>>() {}.getType();
        Result<Backstage> res = JsonUtils.fromJson(pro.getBody(), type);
        if (res.getSucceed()) {
            listenerManager.trigger(EventType.CONNECTED, res, null);
        } else {
            listenerManager.trigger(EventType.DISCONNECTED, res, null);
        }
    }

}
