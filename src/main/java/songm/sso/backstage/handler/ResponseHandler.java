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

import songm.sso.backstage.entity.Attribute;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.entity.Result;
import songm.sso.backstage.entity.Session;
import songm.sso.backstage.entity.User;
import songm.sso.backstage.event.ActionEvent.EventType;
import songm.sso.backstage.event.ActionListenerManager;
import songm.sso.backstage.utils.JsonUtils;

public class ResponseHandler implements Handler {

    @Override
    public int operation() {
        return 0;
    }

    @Override
    public void action(ActionListenerManager listenerManager, Protocol pro) {
        Result<?> res = null;
        Type type = null;

        if (pro.getOperation() == Handler.Operation.USER_REPORT.getValue()) {
            type = new TypeToken<Result<Session>>() {
            }.getType();
        } else if (pro.getOperation() == Handler.Operation.USER_LOGIN.getValue()) {
            type = new TypeToken<Result<Session>>() {
            }.getType();
            res = JsonUtils.fromJson(pro.getBody(), type);
        } else if (pro.getOperation() == Handler.Operation.USER_LOGOUT.getValue()) {
            type = new TypeToken<Result<?>>() {
            }.getType();
        } else if (pro.getOperation() == Handler.Operation.USER_EDIT.getValue()) {
            type = new TypeToken<Result<User>>() {
            }.getType();
        } else if (pro.getOperation() == Handler.Operation.SESSION_GET.getValue()) {
            type = new TypeToken<Result<Session>>() {
            }.getType();
        } else if (pro.getOperation() == Handler.Operation.SESSION_ATTR_GET.getValue()) {
            type = new TypeToken<Result<Attribute>>() {
            }.getType();
        } else if (pro.getOperation() == Handler.Operation.SESSION_ATTR_SET.getValue()) {
            type = new TypeToken<Result<Attribute>>() {
            }.getType();
        }

        res = JsonUtils.fromJson(pro.getBody(), type);
        listenerManager.trigger(EventType.RESPONSE, res, pro.getSequence());
    }

}
