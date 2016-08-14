package songm.sso.backstage.client;

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

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    public String getServerSecret() {
        return serverSecret;
    }

    public void setServerSecret(String serverSecret) {
        this.serverSecret = serverSecret;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private void connect() throws InterruptedException {
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

    private void auth() {
        String nonce = String.valueOf(Math.random() * 1000000);
        long timestamp = System.currentTimeMillis() / 1000;
        StringBuilder toSign = new StringBuilder(serverSecret).append(nonce)
                .append(timestamp);
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
        new SSOClient("127.0.0.1", 9090).connect();
    }
}
