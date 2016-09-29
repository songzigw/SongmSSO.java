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

import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.ActionListenerManager;
import songm.sso.backstage.handler.Handler;
import songm.sso.backstage.handler.HandlerManager;
import songm.sso.backstage.handler.Handler.Operation;
import songm.sso.backstage.utils.CodeUtils;
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
    private HandlerManager handlerManager;
    private String key, secret;

    public SSOClientHandler(ActionListenerManager listenerManager, String key, String secret) {
        this.listenerManager = listenerManager;
        this.handlerManager = new HandlerManager();
        this.key = key;
        this.secret = secret;
    }

    private void authorization(ChannelHandlerContext ctx) throws InterruptedException {
        String nonce = String.valueOf(Math.random() * 1000000);
        long timestamp = System.currentTimeMillis();
        StringBuilder toSign = new StringBuilder(secret)
                        .append(nonce).append(timestamp);
        String sign = CodeUtils.sha1(toSign.toString());

        Backstage back = new Backstage();
        back.setServerKey(key);
        back.setNonce(nonce);
        back.setTimestamp(timestamp);
        back.setSignature(sign);

        Protocol proto = new Protocol();
        proto.setOperation(Operation.CONN_AUTH.getValue());
        proto.setBody(JsonUtils.toJsonBytes(back, back.getClass()));

        ctx.channel().writeAndFlush(proto).await();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        authorization(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Protocol pro)
            throws Exception {
        Handler handler = handlerManager.find(pro.getOperation());
        if (handler != null) {
            handler.action(listenerManager, pro);
        } else {
            LOG.warn("Not found handler: " + pro.getOperation());
        }
        LOG.debug("MessageReceived: {}", pro);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOG.debug("ChannelRead: {}", msg);
        super.channelRead(ctx, msg);
        
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
