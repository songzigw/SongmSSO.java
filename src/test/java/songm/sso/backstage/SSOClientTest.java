package songm.sso.backstage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import songm.sso.backstage.client.ProtocolCodec;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.utils.JsonUtils;

public class SSOClientTest {
    private final String host;
    private final int port;

    public SSOClientTest(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap().group(group)
                    .channel(NioSocketChannel.class).remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new ProtocolCodec());
                        }

                    });

            ChannelFuture f = b.connect().sync();

            Backstage acc = new Backstage();
            acc.setServerKey("admin");
            Protocol proto = new Protocol();
            proto.setVersion((short) 1);
            proto.setOperation(0);
            proto.setBody(JsonUtils.toJson(acc, Backstage.class).getBytes());
            f.channel().writeAndFlush(proto);

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new SSOClientTest("127.0.0.1", 9090).start();
    }
}
