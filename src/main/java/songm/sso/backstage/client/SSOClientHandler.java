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

package songm.sso.backstage.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.ISSOClient.Operation;
import songm.sso.backstage.entity.Attribute;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Entity;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.entity.Session;
import songm.sso.backstage.event.ActionEvent.EventType;
import songm.sso.backstage.event.ActionListenerManager;
import songm.sso.backstage.utils.JsonUtils;

/**
 * 事件消息处理
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
@ChannelHandler.Sharable
public class SSOClientHandler extends SimpleChannelInboundHandler<Protocol> {

    private static final Logger LOG = LoggerFactory
            .getLogger(SSOClientHandler.class);

    private ActionListenerManager listenerManager;

    public SSOClientHandler(ActionListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Protocol pro)
            throws Exception {
        int oper = pro.getOperation();

        if (oper == Operation.CONN_AUTH.getValue()) {
            triggerConnAuth(pro);
        } else if (oper == Operation.USER_REPORT.getValue()) {
            Session session = JsonUtils.fromJson(pro.getBody(), Session.class);
            listenerManager.trigger(EventType.RESPONSE, session, pro.getSequence());
        } else if (oper == Operation.USER_LOGIN.getValue()) {
            Session session = JsonUtils.fromJson(pro.getBody(), Session.class);
            listenerManager.trigger(EventType.RESPONSE, session, pro.getSequence());
        } else if (oper == Operation.USER_LOGOUT.getValue()) {
            Session session = JsonUtils.fromJson(pro.getBody(), Session.class);
            listenerManager.trigger(EventType.RESPONSE, session, pro.getSequence());
        } else if (oper == Operation.USER_EDIT.getValue()) {
            Session session = JsonUtils.fromJson(pro.getBody(), Session.class);
            listenerManager.trigger(EventType.RESPONSE, session, pro.getSequence());
        } else if (oper == Operation.SESSION_GET.getValue()) {
            Session session = JsonUtils.fromJson(pro.getBody(), Session.class);
            listenerManager.trigger(EventType.RESPONSE, session, pro.getSequence());
        } else if (oper == Operation.SESSION_ATTR_GET.getValue()) {
            Attribute attr = JsonUtils.fromJson(pro.getBody(), Attribute.class);
            listenerManager.trigger(EventType.RESPONSE, attr, pro.getSequence());
        } else if (oper == Operation.SESSION_ATTR_SET.getValue()) {
            Entity ent = JsonUtils.fromJson(pro.getBody(), Entity.class);
            listenerManager.trigger(EventType.RESPONSE, ent, pro.getSequence());
        }
    }

    private void triggerConnAuth(Protocol pro) {
        Backstage back = JsonUtils.fromJson(pro.getBody(), Backstage.class);
        if (back.getSucceed()) {
            listenerManager.trigger(EventType.CONNECTED, back, null);
        } else {
            listenerManager.trigger(EventType.DISCONNECTED, back, null);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("HandlerRemoved", ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOG.error("ExceptionCaught", cause);
    }
}
