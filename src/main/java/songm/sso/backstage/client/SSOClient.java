package songm.sso.backstage.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import songm.sso.backstage.CodeUtils;
import songm.sso.backstage.JsonUtils;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;

public class SSOClient {

    private static final Logger LOG = LoggerFactory.getLogger(SSOClient.class);

    private final String host;
    private final int port;

    private String backstageId;
    private String serverKey;
    private String serverSecret;

    private final EventLoopGroup group;
    private final SSOClientInitializer clientInit;
    private ChannelFuture channelFuture;

    public SSOClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup();
        this.clientInit = new SSOClientInitializer();
    }

    public String getBackstageId() {
        return backstageId;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String key, String secret) {
        this.serverKey = key;
        this.serverSecret = secret;
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

    public void connect() throws InterruptedException {
        LOG.info("Connecting SongmSSO Server... Host:{} Port:{}", host, port);

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
            group.shutdownGracefully().sync();
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
        StringBuilder toSign = new StringBuilder(serverSecret)
                .append(nonce).append(timestamp);
        String sign = CodeUtils.sha1(toSign.toString());

        Backstage back = new Backstage();
        back.setServerKey(serverKey);
        back.setNonce(nonce);
        back.setTimestamp(timestamp);
        back.setSignature(sign);

        Protocol proto = new Protocol();
        proto.setVersion((short) 1);
        proto.setOperation(0);
        proto.setBody(JsonUtils.toJson(back).getBytes());

        channelFuture.channel().writeAndFlush(proto);
    }

    public static void main(String[] args) throws Exception {
        SSOClient client = new SSOClient("127.0.0.1", 9090);
        client.setServerKey("zhangsong", "123456");
        client.connect();
    }
}
