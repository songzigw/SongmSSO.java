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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.CodeUtils;
import songm.sso.backstage.ISSOClient;
import songm.sso.backstage.JsonUtils;
import songm.sso.backstage.SSOException;
import songm.sso.backstage.SSOException.ErrorCode;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.AbstractListener;
import songm.sso.backstage.event.ActionEvent;
import songm.sso.backstage.event.ClientListener;
import songm.sso.backstage.event.ActionEvent.EventType;
import songm.sso.backstage.event.ActionListenerManager;

/**
 * 后台客户端的实现
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 * @see #ISSOClient
 * 
 */
public class SSOClient implements ISSOClient {

    private static final Logger LOG = LoggerFactory.getLogger(SSOClient.class);

    private final String host;
    private final int port;

    private Backstage backstage;
    private String serverKey;
    private String serverSecret;

    private final ActionListenerManager listenerManager;
    private final EventLoopGroup group;
    private final SSOClientInitializer clientInit;
    private ChannelFuture channelFuture;
    private ClientListener listener;

    private static SSOClient instance;

    private SSOClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup();
        this.listenerManager = new ActionListenerManager();
        this.clientInit = new SSOClientInitializer(listenerManager);
        this.init();
    }
    
    private void init() {
        listenerManager.addListener(EventType.CONNECTING, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (listener != null) {
                    listener.onConnecting();
                }
            }
        });
        
        listenerManager.addListener(EventType.CONNECTED, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                backstage = (Backstage) event.getData();
                if (listener != null) {
                    listener.onConnected(backstage);
                }
            }
        });
        
        listenerManager.addListener(EventType.DISCONNECTED, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String code = (String) event.getData();
                if (listener != null) {
                    listener.onDisconnected(ErrorCode.valueOf(code));
                }
            }
        });
    }

    public static SSOClient getInstance() {
        if (instance == null) {
            throw new NullPointerException("SSOClient not init");
        }
        return instance;
    }

    public static SSOClient init(String host, int port) {
        instance = new SSOClient(host, port);
        return instance;
    }

    @Override
    public Backstage getBacstage() {
        return backstage;
    }

    @Override
    public String getServerKey() {
        return serverKey;
    }

    @Override
    public String getServerSecret() {
        return serverSecret;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void connect(String key, String secret) throws SSOException {
        LOG.info("Connecting SongmSSO Server... Host:{} Port:{}", host, port);
        this.serverKey = key;
        this.serverSecret = secret;

        ActionEvent event = new ActionEvent(ActionEvent.EventType.CONNECTING,
                String.format("host=%s, post=%d", host, port));
        listenerManager.trigger(event);

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.remoteAddress(host, port);
        b.handler(clientInit);

        try {
            // 与服务器建立连接
            channelFuture = b.connect().sync();
            this.auth();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("Connect failure", e);
            throw new SSOException(ErrorCode.CONN_START, "connect", e);
        } finally {
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        if (channelFuture != null) {
            channelFuture.channel().close().syncUninterruptibly();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    private void auth() {
        String nonce = String.valueOf(Math.random() * 1000000);
        long timestamp = System.currentTimeMillis();
        StringBuilder toSign = new StringBuilder(serverSecret).append(nonce)
                .append(timestamp);
        String sign = CodeUtils.sha1(toSign.toString());

        backstage = new Backstage();
        backstage.setServerKey(serverKey);
        backstage.setNonce(nonce);
        backstage.setTimestamp(timestamp);
        backstage.setSignature(sign);

        Protocol proto = new Protocol();
        proto.setOperation(Operation.AUTH_REQUEST.getValue());
        proto.setBody(JsonUtils.toJson(backstage).getBytes());

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public void addListener(ClientListener listener) {
        this.listener = listener;
    }
    
    public static void main(String[] args) throws Exception {
        SSOClient client = SSOClient.init("127.0.0.1", 9090);
        client.connect("zhangsong", "123456");
    }
}
