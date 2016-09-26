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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import songm.sso.backstage.SSOClient;
import songm.sso.backstage.SSOException;
import songm.sso.backstage.SSOException.ErrorCode;
import songm.sso.backstage.entity.Attribute;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Entity;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.entity.Session;
import songm.sso.backstage.entity.User;
import songm.sso.backstage.event.AbstractListener;
import songm.sso.backstage.event.ActionEvent;
import songm.sso.backstage.event.ActionEvent.EventType;
import songm.sso.backstage.event.ActionListener;
import songm.sso.backstage.event.ActionListenerManager;
import songm.sso.backstage.event.ConnectionListener;
import songm.sso.backstage.event.ResponseListener;
import songm.sso.backstage.handler.Handler.Operation;
import songm.sso.backstage.utils.JsonUtils;

/**
 * 后台客户端的实现
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 * @see #SSOClient
 * 
 */
public class SSOClientImpl implements SSOClient {

    private static final Logger LOG = LoggerFactory.getLogger(SSOClientImpl.class);

    private final String host;
    private final int port;

    private final ActionListenerManager listenerManager;
    private final EventLoopGroup group;
    
    private SSOClientInitializer clientInit;
    private ChannelFuture channelFuture;
    private ConnectionListener connectionListener;

    private int connState;
    
    private SSOClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
        this.listenerManager = new ActionListenerManager();
        this.group = new NioEventLoopGroup();
        this.init();
    }

    private Backstage backstage;
    private void init() {
        listenerManager.addListener(EventType.CONNECTING, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                connState = CONNECTING;
                if (connectionListener != null) {
                    connectionListener.onConnecting();
                }
            }
        });
        
        listenerManager.addListener(EventType.CONNECTED, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                connState = CONNECTED;
                backstage = (Backstage) event.getData();
                if (connectionListener != null) {
                    connectionListener.onConnected(backstage);
                }
            }
        });
        
        listenerManager.addListener(EventType.DISCONNECTED, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                connState = DISCONNECTED;
                backstage = (Backstage) event.getData();
                if (connectionListener != null) {
                    connectionListener.onDisconnected(ErrorCode.valueOf(
                            backstage.getErrorCode()));
                }
            }
        });
    }

    private static SSOClientImpl instance;
    public static SSOClientImpl getInstance() {
        if (instance == null) {
            throw new NullPointerException("SSOClient not init");
        }
        return instance;
    }

    public static SSOClientImpl init(String host, int port) {
        return new SSOClientImpl(host, port);
    }

    public Backstage getBacstage() {
        return backstage;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void connect(String key, String secret) throws SSOException {
        if (connState == CONNECTED || connState == CONNECTING) {
            return;
        }

        LOG.info("Connecting SongmSSO Server Host={} Port={}", host, port);
        this.clientInit = new SSOClientInitializer(listenerManager, key, secret);
        listenerManager.trigger(EventType.CONNECTING, null, null);

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(clientInit);
        b.remoteAddress(host, port);

        try {
            // 与服务器建立连接
            channelFuture = b.connect().await();
            System.out.println("=================与服务器建立连接");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("Connect failure", e);
            throw new SSOException(ErrorCode.CONN_ERROR, "connect", e);
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

    @Override
    public void addListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    @Override
    public void report(String sessionId, ResponseListener<Session> response) {
        Session session = new Session(sessionId);
        
        Protocol proto = new Protocol();
        proto.setOperation(Operation.USER_REPORT.getValue());
        proto.setSequence(new Date().getTime());
        proto.setBody(JsonUtils.toJson(session, Session.class).getBytes());

        listenerManager.addListener(EventType.RESPONSE, new ActionListener() {

            private Long sequence = proto.getSequence();

            @Override
            public Long getSequence() {
                return sequence;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (response == null) {
                    return;
                }
                Session ent = (Session) event.getData();
                if (ent.getSucceed()) {
                    response.onSuccess(ent);
                } else {
                    response.onError(ErrorCode.valueOf(ent.getErrorCode()));
                }
            }
        });

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public void login(String sessionId, String userId, String userInfo,
            ResponseListener<Session> response) {
        User user = new User();
        user.setSesId(sessionId);
        user.setUserId(userId);
        user.setUserInfo(userInfo);
        
        Protocol proto = new Protocol();
        proto.setOperation(Operation.USER_LOGIN.getValue());
        proto.setSequence(new Date().getTime());
        proto.setBody(JsonUtils.toJson(user, User.class).getBytes());

        listenerManager.addListener(EventType.RESPONSE, new ActionListener() {

            private Long sequence = proto.getSequence();

            @Override
            public Long getSequence() {
                return sequence;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (response == null) {
                    return;
                }
                Session ent = (Session) event.getData();
                if (ent.getSucceed()) {
                    response.onSuccess(ent);
                } else {
                    response.onError(ErrorCode.valueOf(ent.getErrorCode()));
                }
            }
        });

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public void logout(String sessionId, ResponseListener<Entity> response) {
        Session session = new Session(sessionId);
        
        Protocol proto = new Protocol();
        proto.setOperation(Operation.USER_LOGOUT.getValue());
        proto.setSequence(new Date().getTime());
        proto.setBody(JsonUtils.toJson(session, Session.class).getBytes());

        listenerManager.addListener(EventType.RESPONSE, new ActionListener() {

            private Long sequence = proto.getSequence();

            @Override
            public Long getSequence() {
                return sequence;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (response == null) {
                    return;
                }
                Entity ent = (Entity) event.getData();
                if (ent.getSucceed()) {
                    response.onSuccess(ent);
                } else {
                    response.onError(ErrorCode.valueOf(ent.getErrorCode()));
                }
            }
        });

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public void getSession(String sessionId, ResponseListener<Session> response) {
        Session session = new Session(sessionId);
        
        Protocol proto = new Protocol();
        proto.setOperation(Operation.SESSION_GET.getValue());
        proto.setSequence(new Date().getTime());
        proto.setBody(JsonUtils.toJson(session, Session.class).getBytes());

        listenerManager.addListener(EventType.RESPONSE, new ActionListener() {

            private Long sequence = proto.getSequence();

            @Override
            public Long getSequence() {
                return sequence;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (response == null) {
                    return;
                }
                Session ent = (Session) event.getData();
                if (ent.getSucceed()) {
                    response.onSuccess(ent);
                } else {
                    response.onError(ErrorCode.valueOf(ent.getErrorCode()));
                }
            }
        });

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public void setAttribute(String sessionId, String key, String value,
            ResponseListener<Attribute> response) {
        if (Session.USER_INFO.equals(key)) {
            throw new IllegalArgumentException("Argument 'key' does not allow for " + key);
        }
        
        Attribute attribute = new Attribute();
        attribute.setSesId(sessionId);
        attribute.setKey(key);
        attribute.setValue(value);
        
        Protocol proto = new Protocol();
        proto.setOperation(Operation.SESSION_ATTR_SET.getValue());
        proto.setSequence(new Date().getTime());
        proto.setBody(JsonUtils.toJson(attribute, Attribute.class).getBytes());

        listenerManager.addListener(EventType.RESPONSE, new ActionListener() {

            private Long sequence = proto.getSequence();

            @Override
            public Long getSequence() {
                return sequence;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (response == null) {
                    return;
                }
                Attribute ent = (Attribute) event.getData();
                if (ent.getSucceed()) {
                    response.onSuccess(ent);
                } else {
                    response.onError(ErrorCode.valueOf(ent.getErrorCode()));
                }
            }
        });

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public void getAttribute(String sessionId, String key,
            ResponseListener<Attribute> response) {
        Attribute attribute = new Attribute();
        attribute.setSesId(sessionId);
        attribute.setKey(key);
        
        Protocol proto = new Protocol();
        proto.setOperation(Operation.SESSION_ATTR_GET.getValue());
        proto.setSequence(new Date().getTime());
        proto.setBody(JsonUtils.toJson(attribute, Attribute.class).getBytes());

        listenerManager.addListener(EventType.RESPONSE, new ActionListener() {

            private Long sequence = proto.getSequence();

            @Override
            public Long getSequence() {
                return sequence;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                if (response == null) {
                    return;
                }
                Attribute ent = (Attribute) event.getData();
                if (ent.getSucceed()) {
                    response.onSuccess(ent);
                } else {
                    response.onError(ErrorCode.valueOf(ent.getErrorCode()));
                }
            }
        });

        channelFuture.channel().writeAndFlush(proto);
    }

    @Override
    public int getConnState() {
        return this.connState;
    }
    
}
