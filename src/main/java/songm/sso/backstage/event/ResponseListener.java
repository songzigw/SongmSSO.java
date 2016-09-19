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
package songm.sso.backstage.event;

import java.util.EventListener;

import songm.sso.backstage.SSOException.ErrorCode;
import songm.sso.backstage.entity.Entity;

/**
 * 响应事件监听器
 *
 * @author zhangsong
 * @since 0.1, 2016-8-2
 * @version 0.1
 *
 */
public abstract class ResponseListener<T extends Entity> implements EventListener {

    /**
     * 当相应成功时
     * 
     * @param backstage
     */
    public abstract void onSuccess(T entity);

    /**
     * 当产生错误时
     * 
     * @param errorCode
     */
    public abstract void onError(ErrorCode errorCode);

}
