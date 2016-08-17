package songm.sso.backstage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.CodeUtils;
import songm.sso.backstage.JsonUtils;
import songm.sso.backstage.SSOException;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.AbstractListener;
import songm.sso.backstage.event.ActionEvent;
import songm.sso.backstage.event.ActionEvent.EventType;
import songm.sso.backstage.event.ActionListenerManager;

public class SSOClient {

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
        listenerManager.addListener(EventType.CONNECTED, new AbstractListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                
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

    public Backstage getBacstage() {
        return backstage;
    }

    public String getServerKey() {
        return serverKey;
    }

    public String getServerSecret() {
        return serverSecret;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

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
            throw new SSOException("connect", e);
        } finally {
            disconnect();
        }
    }

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
        proto.setVersion((short) 1);
        proto.setOperation(0);
        proto.setBody(JsonUtils.toJson(backstage).getBytes());

        channelFuture.channel().writeAndFlush(proto);
    }

    public static void main(String[] args) throws Exception {
        SSOClient client = SSOClient.init("127.0.0.1", 9090);
        client.connect("zhangsong", "123456");
    }
}
