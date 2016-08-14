package songm.sso.backstage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import songm.sso.backstage.JsonUtils;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;

public class SSOClient {
    private final String host;
    private final int port;
    private final EventLoopGroup group;

    public SSOClient(String host, int port) {
        this.host = host;
        this.port = port;
        group = new NioEventLoopGroup();
    }

    public void start() throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.remoteAddress(host, port);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolCodec());
                }
            });
            
            ChannelFuture f = b.connect().sync();

            Backstage acc = new Backstage();
            acc.setServerKey("admin");
            Protocol proto = new Protocol();
            proto.setVersion((short) 1);
            proto.setOperation(0);
            proto.setBody(JsonUtils.toJson(acc).getBytes());
            f.channel().writeAndFlush(proto);

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new SSOClient("127.0.0.1", 9090).start();
    }
}
