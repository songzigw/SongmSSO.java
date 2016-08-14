package songm.sso.backstage.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.entity.Protocol;

@ChannelHandler.Sharable
public class SSOClientHandler extends SimpleChannelInboundHandler<Protocol> {

    private static final Logger LOG = LoggerFactory
            .getLogger(SSOClientHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Protocol pro)
            throws Exception {
        LOG.debug("messageReceived: {}", ctx);
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
