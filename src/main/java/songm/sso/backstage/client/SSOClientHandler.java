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

import songm.sso.backstage.ISSOClient;
import songm.sso.backstage.JsonUtils;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.ActionEvent;
import songm.sso.backstage.event.ActionListenerManager;

/**
 * 事件消息处理
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
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
        LOG.debug("messageReceived: {}", pro);
        int op = pro.getOperation();
        if (op == ISSOClient.Operation.AUTH_SUCCEED.getValue()) {
            Backstage back = JsonUtils.fromJson(pro.getBody(), Backstage.class);
            ActionEvent event = new ActionEvent(
                    ActionEvent.EventType.CONNECTED, back);
            listenerManager.trigger(event);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("handlerRemoved: {}", ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOG.error("exceptionCaught", cause);
    }
}
